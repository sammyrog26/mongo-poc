package com.delasport.model;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventsRepository extends MongoRepository<Events, Long> {}
