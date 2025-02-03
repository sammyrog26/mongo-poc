package com.delasport.model;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaguesRepository extends MongoRepository<Leagues, Long> {}
