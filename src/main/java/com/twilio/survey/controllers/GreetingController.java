package com.twilio.survey.controllers;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import com.twilio.survey.models.Survey;
import com.twilio.survey.repositories.SurveyRepository;
import com.twilio.survey.services.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

  private static final String template = "Hello, %s!";
  private final AtomicLong counter = new AtomicLong();
  @Autowired
  private SurveyRepository surveyRepository;

  @RequestMapping("/greeting")
  public Greeting greeting(@RequestParam(value="name", defaultValue="Mario") String name) {

    SurveyService service = new SurveyService(surveyRepository);

    Survey survey = new Survey("Prueba Mario", new Date());
    service.create(survey);

    return new Greeting(counter.incrementAndGet(),
        String.format(template, name));
  }
}
