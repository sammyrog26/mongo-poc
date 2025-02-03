package com.delasport.controller;

import static com.delasport.constants.AppConstants.STAGE_MONGO_GROUP;
import static com.delasport.constants.AppConstants.STAGE_MONGO_MATCH;
import static com.delasport.constants.AppConstants.STAGE_MONGO_SORT;
import static com.delasport.utils.MessageBuilder.buildGenericResponse;
import static com.delasport.utils.MessageBuilder.buildStatusCodeError;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.delasport.model.Events;
import com.delasport.model.GenericResponse;
import com.delasport.model.Leagues;
import com.delasport.service.MongoDBService;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Field;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import io.swagger.v3.oas.annotations.Operation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RestControllerMongo {

  private final MongoClient customMongoClient;

  private final MongoDBService mongoDBService;

  @GetMapping(value = "/getCollections/{databaseName}", produces = APPLICATION_JSON_VALUE)
  @Operation(summary = "Fetches collection names from a given database.")
  public GenericResponse getCollections(@PathVariable String databaseName) {
    final var database = customMongoClient.getDatabase(databaseName);
    final var collections = new ArrayList<>();
    database.listCollectionNames().forEach(collections::add);
    return new GenericResponse(collections, 200);
  }

  @GetMapping(value = "/getDatabases", produces = APPLICATION_JSON_VALUE)
  @Operation(summary = "Fetches all database names.")
  public GenericResponse getDatabases() {
    List<String> databases = new ArrayList<>();
    customMongoClient.listDatabaseNames().forEach(databases::add);
    return new GenericResponse(databases, 200);
  }

  @GetMapping(
      value = "/getDocuments/{databaseName}/{collectionName}",
      produces = APPLICATION_JSON_VALUE)
  @Operation(summary = "Retrieves all documents from the specified collection.")
  public GenericResponse getDocuments(
      @PathVariable String databaseName, @PathVariable String collectionName) {

    final var collection = getMongoCollection(databaseName, collectionName);

    List<Document> documents = new ArrayList<>();
    try (final var cursor = collection.find().iterator()) {
      documents = collectAndOrder(cursor);
    } catch (Exception e) {
      log.info(
          "[Get Documents] Cannot get documents for the collection: {}. Reason: {}",
          collection,
          ExceptionUtils.getStackTrace(e));
      GenericResponse.builder().message(e.getMessage()).statusCode(buildStatusCodeError()).build();
    }
    return GenericResponse.builder().message(documents).statusCode(getStatusCode()).build();
  }

  @PostMapping(
      value = "/createDocument/{databaseName}/{collectionName}",
      produces = APPLICATION_JSON_VALUE)
  @Operation(summary = "Creates a document in the specified collection.")
  public GenericResponse createDocument(
      @PathVariable String databaseName,
      @PathVariable String collectionName,
      @RequestBody Document document) {

    final var collection = getMongoCollection(databaseName, collectionName);
    collection.insertOne(document);
    return new GenericResponse("Document inserted successfully", 200);
  }

  @PostMapping(value = "/createEvents", produces = APPLICATION_JSON_VALUE)
  @Operation(summary = "Creates a document in the events collections")
  public GenericResponse createDocumentBasedOnEntity(@RequestBody Events document) {
    return mongoDBService.addEvent(document);
  }

  @PostMapping(value = "/createLeagues", produces = APPLICATION_JSON_VALUE)
  @Operation(summary = "Creates a document in the leagues collections")
  public GenericResponse createDocumentBasedOnEntity(@RequestBody Leagues document) {
    return mongoDBService.addLeague(document);
  }

  @PutMapping(
      value = "/updateDocument/{databaseName}/{collectionName}",
      produces = APPLICATION_JSON_VALUE)
  @Operation(summary = "Updates a document in the specified collection.")
  public GenericResponse updateDocument(
      @PathVariable String databaseName,
      @PathVariable String collectionName,
      @RequestParam String filterKey,
      @RequestParam String filterValue,
      @RequestBody Document update) {

    final var collection = getMongoCollection(databaseName, collectionName);
    Document filter = new Document(filterKey, filterValue);
    collection.updateOne(filter, new Document("$set", update));
    return new GenericResponse("Document updated successfully", 200);
  }

  @DeleteMapping(
      value = "/deleteDocument/{databaseName}/{collectionName}",
      produces = APPLICATION_JSON_VALUE)
  @Operation(summary = "Deletes a document in the specified collection.")
  public GenericResponse deleteDocument(
      @PathVariable String databaseName,
      @PathVariable String collectionName,
      @RequestParam String filterKey,
      @RequestParam String filterValue) {

    final var collection = getMongoCollection(databaseName, collectionName);
    final var filter = new Document(filterKey, filterValue);
    collection.deleteOne(filter);
    return new GenericResponse("Document deleted successfully", 200);
  }

  @GetMapping(
      value = "/aggregate/match/{databaseName}/{collectionName}",
      produces = APPLICATION_JSON_VALUE)
  @Operation(summary = "Performs a match aggregation on the specified collection.")
  public GenericResponse matchDocuments(
      @PathVariable String databaseName,
      @PathVariable String collectionName,
      @RequestParam String matchField,
      @RequestParam int matchValue) {

    final var collection = getMongoCollection(databaseName, collectionName);
    final var pipeline =
        List.of(new Document(STAGE_MONGO_MATCH, new Document(matchField, matchValue)));

    List<Document> results;
    try (final var cursor = collection.aggregate(pipeline).iterator()) {
      results = collectAndOrder(cursor);
    } catch (Exception e) {
      log.info(
          "[MATCH] Cannot match documents based on the matchField: {} and matchValue: {}. Reason: {}",
          matchField,
          matchValue,
          ExceptionUtils.getStackTrace(e));
      final var errorMessage =
          String.format(
              "[MATCH] Cannot match documents based on the matchField: %s and matchValue: %s. Reason: %s",
              matchField, matchValue, e.getMessage());
      return buildGenericResponse(errorMessage, buildStatusCodeError());
    }
    return buildGenericResponse(results, getStatusCode());
  }

  @GetMapping(
      value = "/aggregate/sort/{databaseName}/{collectionName}",
      produces = APPLICATION_JSON_VALUE)
  @Operation(summary = "Performs a sort aggregation on the specified collection.")
  public GenericResponse sortDocuments(
      @PathVariable String databaseName,
      @PathVariable String collectionName,
      @RequestParam String sortField,
      @RequestParam(required = false, defaultValue = "1") int sortOrder) {

    final var collection = getMongoCollection(databaseName, collectionName);
    final var pipeline =
        List.of(new Document(STAGE_MONGO_SORT, new Document(sortField, sortOrder)));

    return getDocumentsFromPipeline(
        collection.aggregate(pipeline),
        "[SORT] Cannot sort documents based on the sortField: {}. Reason: {}",
        sortField);
  }

  @GetMapping(
      value = "/aggregate/sortAndLimit/{databaseName}/{collectionName}",
      produces = APPLICATION_JSON_VALUE)
  @Operation(summary = "Performs a sort aggregation on the specified collection.")
  public GenericResponse sortAndLimitDocuments(
      @PathVariable String databaseName,
      @PathVariable String collectionName,
      @RequestParam String sortField,
      @RequestParam(required = false, defaultValue = "1") int sortOrder,
      @RequestParam(required = false, defaultValue = "10") int limit) {

    final var collection = getMongoCollection(databaseName, collectionName);
    final var pipeline =
        List.of(
            Aggregates.sort(
                sortOrder == 1 ? Sorts.ascending(sortField) : Sorts.descending(sortField)),
            Aggregates.limit(limit));

    return getDocumentsFromPipeline(
        collection.aggregate(pipeline),
        "[SORT] Cannot sort documents based on the sortField: {}. Reason: {}",
        sortField);
  }

  @GetMapping(
      value = "/aggregate/group/{databaseName}/{collectionName}",
      produces = APPLICATION_JSON_VALUE)
  @Operation(summary = "Performs a group aggregation on the specified collection.")
  public GenericResponse groupDocuments(
      @PathVariable String databaseName,
      @PathVariable String collectionName,
      @RequestParam String groupField) {

    final var collection = getMongoCollection(databaseName, collectionName);
    final var pipeline =
        List.of(new Document(STAGE_MONGO_GROUP, new Document("_id", "$" + groupField)));

    return getDocumentsFromPipeline(
        collection.aggregate(pipeline),
        "[GROUP] Cannot group documents based on the groupField: {}. Reason: {}",
        groupField);
  }

  @PostMapping(
      value = "/aggregate/group/age/{databaseName}/{collectionName}",
      produces = APPLICATION_JSON_VALUE)
  @Operation(summary = "Groups documents by name where age >= 20.")
  public GenericResponse groupByAge(
      @PathVariable String databaseName,
      @PathVariable String collectionName,
      @RequestParam String groupField) {

    final var collection = getMongoCollection(databaseName, collectionName);

    final var pipeline =
        Arrays.asList(
            Aggregates.match(Filters.gte("age", 20)),
            Aggregates.addFields(new Field<>("objectId", new Document("$toString", "$_id"))),
            Aggregates.group(
                "$" + groupField,
                Accumulators.push("message", "$$ROOT")) // Group by the specified field
            );

    List<Document> results;
    try {
      results = collection.aggregate(pipeline).into(new ArrayList<>());
    } catch (Exception e) {
      log.info(
          "[GROUP BY AGE] Cannot group documents by field {}. Reason: {}",
          groupField,
          ExceptionUtils.getStackTrace(e));
      final var messageError =
          String.format(
              "[GROUP BY AGE] Cannot group documents by field %s. Reason: %s",
              groupField, ExceptionUtils.getStackTrace(e));
      return buildGenericResponse(messageError, buildStatusCodeError());
    }

    return buildGenericResponse(results, getStatusCode());
  }

  private static int getStatusCode() {
    return HttpStatus.OK.value();
  }

  @GetMapping(
      value = "/aggregate/set/{databaseName}/{collectionName}",
      produces = APPLICATION_JSON_VALUE)
  @Operation(summary = "Performs a $set aggregation on the specified collection.")
  public GenericResponse setAndConcatFields(
      @PathVariable String databaseName, @PathVariable String collectionName) {

    if (!databaseName.equals("sample_training")) {
      return GenericResponse.builder()
          .message("Cannot do $set base for $concat for the given databaseName")
          .build();
    }

    final var collection = getMongoCollection(databaseName, collectionName);

    final var pipeline =
        List.of(
            Aggregates.addFields(
                new Field<>("place", new Document("$concat", List.of("$city", ", ", "$state"))),
                new Field<>("population", 10000)),
            Aggregates.limit(10));

    return getDocumentsFromPipeline(
        collection.aggregate(pipeline),
        "[SET] Cannot update fields using $set aggregation. Reason: {}",
        "place and pop");
  }

  @GetMapping(
      value = "/aggregate/join/{databaseName}/{eventsCollection}/{leaguesCollection}",
      produces = APPLICATION_JSON_VALUE)
  @Operation(summary = "Joins events with leagues to add leagueTitle and leagueGender")
  public GenericResponse getProcessedEvents(
      @PathVariable String databaseName,
      @PathVariable String eventsCollection,
      @PathVariable String leaguesCollection,
      @RequestParam Long eventId) {
    final var collection = getMongoCollection(databaseName, eventsCollection);

    final var pipeline =
        List.of(
            // Step 1: Join events with leagues
            Aggregates.lookup(leaguesCollection, "leagueId", "id", "leagueData"),

            // Step 2: Unwind the joined array
            Aggregates.unwind("$leagueData"),

            // Step 3: Add new fields from joined leagues collection
            Aggregates.addFields(
                new Field<>("leagueTitle", "$leagueData.title"),
                new Field<>("leagueGender", "$leagueData.gender")),

            // Step 4: Project final output (Remove unnecessary fields)
            Aggregates.project(
                Projections.fields(
                    Projections.excludeId(), // Exclude MongoDB _id field
                    Projections.include(
                        "id",
                        "leagueId",
                        "homeTeamId",
                        "awayTeamId",
                        "homeTeamTitle",
                        "awayTeamTitle",
                        "sportId",
                        "leagueCountryId",
                        "leagueTitle",
                        "leagueGender"))),
            Aggregates.match(Filters.eq("id", eventId)));

    return getDocumentsFromPipeline(
        collection.aggregate(pipeline),
        "[JOIN] Cannot process events with leagues. Reason: {}",
        "events");
  }

  private static GenericResponse getDocumentsFromPipeline(
      final AggregateIterable<Document> collection,
      final String errorString,
      final String groupField) {
    List<Document> results;
    try (final var cursor = collection.iterator()) {
      results = collectAndOrder(cursor);
    } catch (Exception e) {
      log.error(errorString, groupField, ExceptionUtils.getStackTrace(e));
      return buildGenericResponse(e.getMessage(), buildStatusCodeError());
    }
    return buildGenericResponse(results, getStatusCode());
  }

  private static List<Document> collectAndOrder(MongoCursor<Document> cursor) {
    return StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(cursor, Spliterator.ORDERED), false)
        .peek(
            document -> {
              final var idField = document.get("_id");
              if (idField instanceof ObjectId id) {
                document.put("userId", id.toHexString()); // Convert ObjectId to hex string
              }
            })
        .collect(Collectors.toList());
  }

  private MongoDatabase getMongoDatabase(final String databaseName) {
    return customMongoClient.getDatabase(databaseName);
  }

  private MongoCollection<Document> getMongoCollection(
      final String databaseName, final String collectionName) {
    return getMongoDatabase(databaseName).getCollection(collectionName);
  }
}
