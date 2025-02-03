package com.delasport.service;

import static com.delasport.constants.AppConstants.EVENTS;
import static com.delasport.constants.AppConstants.FAILED;
import static com.delasport.constants.AppConstants.LEAGUES;
import static com.delasport.constants.AppConstants.SUCCESSFUL;
import static com.delasport.utils.MessageBuilder.buildGenericResponse;
import static com.delasport.utils.MessageBuilder.buildStatusCodeError;
import static com.delasport.utils.MessageBuilder.buildStatusSuccess;

import com.delasport.model.Events;
import com.delasport.model.EventsRepository;
import com.delasport.model.GenericResponse;
import com.delasport.model.Leagues;
import com.delasport.model.LeaguesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MongoDBService {
  private final EventsRepository eventsRepository;
  private final LeaguesRepository leaguesRepository;

  @Transactional
  public GenericResponse addEvent(final Events event) {
    if (eventsRepository.findById(event.getId()).isPresent()) {
      return buildGenericResponse(
          genericResponse(FAILED, EVENTS, event.getId()), buildStatusCodeError());
    }
    eventsRepository.save(event);

    return buildGenericResponse(
        genericResponse(SUCCESSFUL, EVENTS, event.getId()), buildStatusSuccess());
  }

  @Transactional
  public GenericResponse addLeague(final Leagues league) {
    if (leaguesRepository.findById(league.getId()).isPresent()) {
      return buildGenericResponse(
          genericResponse(FAILED, LEAGUES, league.getId()), buildStatusCodeError());
    }
    leaguesRepository.save(league);

    return buildGenericResponse(
        genericResponse(SUCCESSFUL, LEAGUES, league.getId()), buildStatusSuccess());
  }

  private static String genericResponse(final String outcome, final String entity, final Long id) {
    return String.format("[%s} to add entity of type: %s for id %d.", outcome, entity, id);
  }
}
