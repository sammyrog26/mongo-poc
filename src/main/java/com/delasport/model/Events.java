package com.delasport.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "events")
@Data
public class Events {
  private Long id;
  private Long leagueId;
  private Long homeTeamId;
  private Long awayTeamId;
  private String homeTeamTitle;
  private String awayTeamTitle;
  private Integer sportId;
  private Integer leagueCountryId;
}
