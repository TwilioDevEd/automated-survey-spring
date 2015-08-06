package com.twilio.survey.services;

import com.twilio.survey.models.Survey;
import com.twilio.survey.repositories.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SurveyService {
  private SurveyRepository surveyRepository;

  @Autowired
  public SurveyService(SurveyRepository surveyRepository) {
    this.surveyRepository = surveyRepository;
  }

  public void create(Survey survey) {
    surveyRepository.save(survey);
  }
}
