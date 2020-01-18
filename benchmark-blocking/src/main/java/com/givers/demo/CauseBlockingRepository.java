package com.givers.demo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CauseBlockingRepository  extends MongoRepository<Cause, String>{

}
