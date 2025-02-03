package com.delasport;

import io.github.cdimascio.dotenv.Dotenv;
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
    final var dotEnv = Dotenv.load();
    System.setProperty("MONGO_DB_CLUSTER", dotEnv.get("MONGO_DB_CLUSTER"));
    System.setProperty("MONGO_DB_USER", dotEnv.get("MONGO_DB_USER"));
    System.setProperty("MONGO_DB_PASSWORD", dotEnv.get("MONGO_DB_PASSWORD"));
    System.setProperty("MONGO_DB_APP_NAME", dotEnv.get("MONGO_DB_APP_NAME"));
    System.setProperty("MONGO_DATABASE_NAME", dotEnv.get("MONGO_DATABASE_NAME"));
    SpringApplication.run(MongoPocApplication.class, args);
  }
}
