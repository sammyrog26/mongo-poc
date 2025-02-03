package com.delasport.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "leagues")
@Data
public class Leagues {
  private Long id;
  private String title;
  private String gender;
  private Integer sportId;
  private Integer countryId;
}
