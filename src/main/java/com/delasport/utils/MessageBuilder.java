package com.delasport.utils;

import com.delasport.model.GenericResponse;
import org.springframework.http.HttpStatus;

public final class MessageBuilder {
  public static GenericResponse buildGenericResponse(
      final Object message, final Integer statusCode) {
    return GenericResponse.builder().message(message).statusCode(statusCode).build();
  }

  public static int buildStatusCodeError() {
    return HttpStatus.INTERNAL_SERVER_ERROR.value();
  }

  public static int buildStatusSuccess() {
    return HttpStatus.OK.value();
  }
}
