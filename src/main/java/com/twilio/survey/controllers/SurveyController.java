package com.twilio.survey.controllers;

import com.twilio.survey.repositories.SurveyRepository;
import com.twilio.survey.services.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

public class SurveyController {
  @Autowired
  private SurveyRepository surveyRepository;
  private SurveyService surveyService;

  public SurveyController() {
    this.surveyService = new SurveyService(surveyRepository);
  }

  @RequestMapping("/mario")
  public String getLast() {
    return surveyService.findLast().getId().toString();
  }
}
