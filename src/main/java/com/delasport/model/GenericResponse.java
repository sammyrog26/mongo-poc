package com.delasport.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GenericResponse {
  private Object message;
  private int statusCode;

  public GenericResponse(Object message, int statusCode) {
    this.message = message;
    this.statusCode = statusCode;
  }
}
