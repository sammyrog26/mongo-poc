package com.delasport.model;

import static org.springframework.http.HttpStatus.ALREADY_REPORTED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import java.util.Objects;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Builder
public class RestResponse {

  private Object message;
  private Integer statusCode;
  private Object eventId;

  public static RestResponse ok(final Object message) {
    return RestResponse.builder().message(message).statusCode(OK.value()).build();
  }

  public static RestResponse ok(final Object message, final Object eventId) {
    return RestResponse.builder().message(message).statusCode(OK.value()).eventId(eventId).build();
  }

  public static RestResponse notFound() {
    return notFound(null);
  }

  public static RestResponse notFound(final Object message) {
    log.info("{}", message);
    return RestResponse.builder()
        .message(resolveMessage(message))
        .statusCode(NOT_FOUND.value())
        .build();
  }

  public static RestResponse notFound(final Object message, final Object eventId) {
    log.info("{}", message);
    return RestResponse.builder()
        .message(resolveMessage(message))
        .statusCode(NOT_FOUND.value())
        .eventId(eventId)
        .build();
  }

  public static RestResponse badRequest(final String message, final Object eventId) {
    log.info("Record response: {}. EventId {}", message, eventId);
    return RestResponse.builder()
        .message(message)
        .statusCode(BAD_REQUEST.value())
        .eventId(eventId)
        .build();
  }

  public static RestResponse okAlreadySubmitted(final String message, final Object eventId) {
    log.info("Record response: {}. EventId {}", message, eventId);
    return RestResponse.builder()
        .message(message)
        .statusCode(ALREADY_REPORTED.value())
        .eventId(eventId)
        .build();
  }

  public static RestResponse notAcceptable(final String message, final Object eventId) {
    return RestResponse.builder()
        .message(message)
        .statusCode(NOT_ACCEPTABLE.value())
        .eventId(eventId)
        .build();
  }

  public static RestResponse badRequest(final String message) {
    return RestResponse.builder().message(message).statusCode(BAD_REQUEST.value()).build();
  }

  private static Object resolveMessage(Object message) {
    return Objects.isNull(message)
        ? "Record " + NOT_FOUND.getReasonPhrase().toLowerCase()
        : message;
  }
}
