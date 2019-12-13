FROM python:3.6
COPY . /code/
WORKDIR /code
RUN pip install --no-cache-dir -r requirements.txt

ENTRYPOINT python run.py