package com.givers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;


@Configuration
public class Config extends AbstractMongoConfiguration {
	@Value("${spring.data.mongodb.database}")
	private String databaseName;
	@Value("${spring.data.mongodb.uri}")
	private String databaseUri;
	
    @Override
    protected String getDatabaseName() {
        return databaseName;
    }
  
    @Override
    public MongoClient mongoClient() {
        MongoClient client = new MongoClient(new MongoClientURI(databaseUri));
        return client;
    }
}
