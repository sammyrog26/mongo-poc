package com.delasport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableMongoRepositories(basePackages = {"com.delasport.model"})
@ComponentScan(basePackages = {"com.delasport.*"})
public class MongoPocApplication {
  public static void main(String[] args) {
    SpringApplication.run(MongoPocApplication.class, args);
  }
}
