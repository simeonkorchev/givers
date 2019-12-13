package com.givers.repository.database;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.givers.repository.entity.Comment;

public interface CommentRepository extends ReactiveMongoRepository<Comment, String> {

}
