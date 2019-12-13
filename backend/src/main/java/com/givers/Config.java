package com.givers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.givers.recommender.ratings.algorithm.Recommender;
import com.givers.recommender.ratings.builder.Rating;
import com.givers.recommender.ratings.builder.RatingCalculator;
import com.givers.recommender.ratings.builder.RatingCalculatorImpl;
import com.givers.recommender.ratings.builder.UserRatingCalculator;
import com.givers.recommender.ratings.builder.UserRatingCalculatorImpl;
import com.givers.repository.database.CollectorRepository;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;


@Configuration
public class Config extends AbstractMongoConfiguration {
	@Value("${spring.data.mongodb.database}")
	private String databaseName;
	@Value("${spring.data.mongodb.uri}")
	private String databaseUri;
//	@Value("${MONGO_USER}")
//	private String databaseUser;
//	@Value("${MONGO_PASSWORD}")
//	private String databasePassword;
//	@Value("${PORT}")
//	private String databasePort;
//	@Value("${MONGO_HOST}")
//	private String databaseHost;
	
    @Override
    protected String getDatabaseName() {
        return databaseName;
    }
  
    @Override
    public MongoClient mongoClient() {
        MongoClient client = new MongoClient(new MongoClientURI(databaseUri));
        return client;
    }
//    
//    @Bean
//	public MongoTemplate mongoTemplate() throws Exception {
//		return new MongoTemplate(mongoClient(), getDatabaseName());
//	}
//  
	@Autowired
	CollectorRepository repository;
//
//	//TODO make constructors for RatingTransformer and RatingCalculator parameterized
//	//That could allow to have only one calculator
//	//Note that a change in the whole chain will be needed
//	//See https://stackoverflow.com/questions/35108778/spring-bean-with-runtime-constructor-arguments
	@Bean
	public Recommender getRecommender(Map<String, Rating> allRatings, Map<String, Rating> userRatings) {
		return new Recommender(getRatingCalculator(), getUserRatingCalculator());
	}
//
	@Bean
	public RatingCalculator getRatingCalculator() {
		return new RatingCalculatorImpl(repository);
	}
	
	@Bean
	public UserRatingCalculator getUserRatingCalculator() {
		return new UserRatingCalculatorImpl(repository);
	}
}
