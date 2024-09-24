package com.example.demo.repository;

import com.example.demo.entity.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MongoRepository extends org.springframework.data.mongodb.repository.MongoRepository<UserEntity, String> {

    Optional<UserEntity> findByEmail(String email);


}
