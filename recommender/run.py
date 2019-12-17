from pymongo import MongoClient
import pandas as pd
import scipy.sparse as sparse
import numpy as np
import random
import re
import implicit
from sklearn.preprocessing import MinMaxScaler  
from flask import Flask
import json
from flask import request, Response
import os
from pandas.io.json import json_normalize
from bson.json_util import dumps
from bson.objectid import ObjectId
import bson

app = Flask(__name__)

event_type_strength = {
    'CAUSE_TYPE_VIEWED': 0.5,
    'CAUSE_DETAILS_VIEWED': 1.0,
    'COMMENT_CREATED': 2.0,
    'ATTEND': 3.0,
}

model = []
sparse_content_person = []
sparse_person_content = []
person_vecs = []
content_vecs = []
grouped_df = []
user_to_cat_code = []
cause_to_cat_code = []

def _train_model(df):
  df['eventStrength'] = df['eventType'].apply(lambda x: 
  event_type_strength[x])
  #global grouped_df
  grouped_df = group_df(df)
  cat_code_to_user = dict( enumerate(grouped_df['username'].cat.categories ) )
  cat_code_to_cause = dict( enumerate(grouped_df['causeId'].cat.categories) )

  global user_to_cat_code
  extra = {v: k for k, v in cat_code_to_user.items()}
  if not user_to_cat_code:
    user_to_cat_code = extra
  else:
    user_to_cat_code.update(extra)
  
  global cause_to_cat_code
  cause_to_cat_code = {v: k for k, v in cat_code_to_cause.items()}
  grouped_df = create_categories(grouped_df)

  #global sparse_content_person
  sparse_content_person = sparse.csr_matrix((grouped_df['eventStrength'].astype(float), (grouped_df['causeId'], grouped_df['username'])))
  #global sparse_person_content
  sparse_person_content = sparse.csr_matrix((grouped_df['eventStrength'].astype(float), (grouped_df['username'], grouped_df['causeId'])))
  global model
  model = implicit.als.AlternatingLeastSquares(factors=20, regularization=0.1, iterations=100)
  alpha = 20
  data = (sparse_content_person * alpha).astype('double')
  model.fit(data)
  global person_vecs
  person_vecs = sparse.csr_matrix(model.user_factors)
  global content_vecs
  content_vecs = sparse.csr_matrix(model.item_factors)

def train_model():
  df = read_mongo("test", "log", host=os.getenv('MONGO_HOST'), port=os.getenv('MONGO_PORT'), username=os.getenv('MONGO_USERNAME'), password=os.getenv('MONGO_PASSWORD'))
  isempty = df.empty
  if isempty: 
    return
  _train_model(df)

def _connect_mongo(host, port, username, password, db):
    """ A util for making a connection to mongo """
    if username and password:
        mongo_uri = 'mongodb://%s:%s@%s:%s/%s?authSource=givers' % (username, password, host, port, db)
        conn = MongoClient(mongo_uri)
    else:
        conn = MongoClient(host, port)
    return conn[db]

def read_mongo(db, collection, query={}, host='localhost', port=27017, username=None, password=None, no_meta=True):
    """ Read from Mongo and Store into DataFrame """
  
    # Connect to MongoDB
    print("Connecting to MongoDB")
    db = _connect_mongo(host=host, port=port, username=username, password=password, db=db)

    # Make a query to the specific DB and Collection
    cursor = db[collection].find(query)

    # Expand the cursor and construct the DataFrame
    df =  pd.DataFrame(list(cursor))

    # Delete the _id
    if no_meta and '_id' in df:
        del df['_id']
    # Delete the _class
    if no_meta and '_class' in df:
        del df['_class']
    # Delete the created
    if no_meta and 'created' in df:
        del df['created']
    return df

@app.route('/api/v1/recommend/<username>', methods=['GET'])
def make_recommendation(username):
  if not username in user_to_cat_code:
      response = Response (
          mimetype="application/json",
          response=json.dumps("Unexpected username"),
          status=404
      )
      return response
  count = request.args.get('count', default = 3, type=int)
  if count <= 0:
      return Response (
          mimetype="application/json",
          response=json.dumps("Invalid value for count. It should be a positive number"),
          status=404
      )
  causeIds = recommend(user_to_cat_code[username], count)

  causes = get_causes(causeIds)
  response = Response(
      mimetype="application/json",
      response=causes,
      status=200
  )
  return response

def get_causes(causeIds):
  db = _connect_mongo(host=os.getenv('MONGO_HOST'), port=os.getenv('MONGO_PORT'), username=os.getenv('MONGO_USERNAME'), password=os.getenv('MONGO_PASSWORD'), db="test")
  return [dumps(db["cause"].find({"_id": ObjectId(causeId)})) for causeId in causeIds]
  

def group_df(df):
  grouped_df = df.groupby(['causeId', 'username', 'causeName']).sum().reset_index()
  grouped_df['causeId'] = grouped_df['causeId'].astype("category")
  grouped_df['username'] = grouped_df['username'].astype("category")
  grouped_df['causeName'] = grouped_df['causeName'].astype("category")
  return grouped_df

def create_categories(grouped_df):
  grouped_df['username'] = grouped_df['username'].cat.codes
  grouped_df['causeId'] = grouped_df['causeId'].cat.codes
  return grouped_df

def recommend(person_id, num_contents=3):
  df = read_mongo("test", "log", host=os.getenv('MONGO_HOST'), port=os.getenv('MONGO_PORT'), username=os.getenv('MONGO_USERNAME'), password=os.getenv('MONGO_PASSWORD'))
  df['eventStrength'] = df['eventType'].apply(lambda x: 
  event_type_strength[x])
  #global grouped_df
  grouped_df = group_df(df)
  cat_to_cause_id = grouped_df['causeId'].cat.categories
  grouped_df = create_categories(grouped_df)

  sparse_content_person = sparse.csr_matrix((grouped_df['eventStrength'].astype(float), (grouped_df['causeId'], grouped_df['username'])))
  #global sparse_person_content
  sparse_person_content = sparse.csr_matrix((grouped_df['eventStrength'].astype(float), (grouped_df['username'], grouped_df['causeId'])))

  if sparse_person_content.nnz == 0:
    return
  # Get the interactions scores from the sparse person content matrix
  person_interactions = sparse_person_content[person_id,:].toarray()
  # Add 1 to everything, so that articles with no interaction yet become equal to 1
  person_interactions = person_interactions.reshape(-1) + 1
  # Make articles already interacted zero
  person_interactions[person_interactions > 1] = 0
  # Get dot product of person vector and all content vectors
  rec_vector = person_vecs[person_id,:].dot(content_vecs.T).toarray()

  # Scale this recommendation vector between 0 and 1
  min_max = MinMaxScaler()
  rec_vector_scaled = min_max.fit_transform(rec_vector.reshape(-1,1))[:,0]
  # Content already interacted have their recommendation multiplied by zero
  recommend_vector = person_interactions * rec_vector_scaled
  # Sort the indices of the content into order of best recommendations
  content_idx = np.argsort(recommend_vector)[::-1][:num_contents]

  # Start empty list to store titles and scores
  usernames = []
  scores = []
  names = []
  causeIds = []

  for idx in content_idx:
      # Append titles and scores to the list
      # names.append(grouped_df.causeName.loc[grouped_df.causeId == idx].iloc[0])
      names.append(grouped_df.causeName.loc[grouped_df.causeId == idx].iloc[0])
      # usernames.append(cat_code_to_user[grouped_df.username.loc[grouped_df.causeId == idx].iloc[0]])
      causeIds.append(cat_to_cause_id[grouped_df.causeId.loc[grouped_df.causeId == idx].iloc[0]])
      scores.append(recommend_vector[idx])

  return causeIds

@app.route('/api/v1/retrain', methods=['POST'])
def retrain_model():
  df = json_normalize(request.get_json())
  _train_model(df)
  return Response(
          mimetype="application/json",
          response=json.dumps("Created"),
          status=201
          )

app.before_first_request(train_model)
app.run(host="0.0.0.0",port=os.getenv('PORT'))



