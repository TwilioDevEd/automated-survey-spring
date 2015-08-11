package com.twilio.survey.controllers;

import com.twilio.survey.repositories.SurveyRepository;
import com.twilio.survey.services.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class SurveyController {
  private static final String template = "Hello, %s!";
  private final AtomicLong counter = new AtomicLong();
  @Autowired
  private SurveyRepository surveyRepository;
  private SurveyService surveyService;

  public SurveyController() {
    this.surveyService = new SurveyService(surveyRepository);
  }

  @RequestMapping("/survey")
  public String welcome() {
    return "HOLA";
  }

//  @RequestMapping("/")
//  public String welcome(Map<String, Object> model) {
//    model.put("time", new Date());
//    model.put("message", "hola hola");
//    return "welcome";
//  }
}
