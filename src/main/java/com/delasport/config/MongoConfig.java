package com.delasport.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

  @Value("${app.mongo.connection-str}")
  private String connectionString;

  @Value("${app.mongo.database-name}")
  private String databaseName;

  //  @Bean
  //  public MongoClient customMongoClient() {
  //    final var serverApi = ServerApi.builder().version(ServerApiVersion.V1).build();
  //
  //    final var mongoClient =
  //        MongoClients.create(
  //            MongoClientSettings.builder()
  //                .applyConnectionString(new ConnectionString(connectionString))
  //                .serverApi(serverApi)
  //                .build());
  //
  ////    try (mongoClient) {
  ////      try {
  ////        // Send a ping to confirm a successful connection
  ////        final var database = mongoClient.getDatabase("admin");
  ////        database.runCommand(new Document("ping", 1));
  ////        System.out.println("Pinged your deployment. You successfully connected to MongoDB!");
  ////      } catch (MongoException e) {
  ////        System.out.println(e.getMessage());
  ////      }
  ////    }
  //
  //    return mongoClient;
  //  }

  @Bean
  public MongoClient customMongoClient() {
    return MongoClients.create(
        MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(connectionString))
            .applyToConnectionPoolSettings(
                builder -> builder.maxSize(50) // Adjust as needed
                )
            .build());
  }

  //  @Bean
  //  public MongoTemplate mongoTemplate() {
  //    return new MongoTemplate(customMongoClient(), databaseName);
  //  }
}
