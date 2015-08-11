package com.twilio.survey.controllers;

import com.twilio.sdk.verbs.Redirect;
import com.twilio.sdk.verbs.Say;
import com.twilio.sdk.verbs.TwiMLException;
import com.twilio.sdk.verbs.TwiMLResponse;
import com.twilio.survey.models.Question;
import com.twilio.survey.models.Survey;
import com.twilio.survey.repositories.QuestionRepository;
import com.twilio.survey.repositories.SurveyRepository;
import com.twilio.survey.services.QuestionService;
import com.twilio.survey.services.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class SurveyController {
  private static final String template = "Hello, %s!";
  private final AtomicLong counter = new AtomicLong();
  @Autowired
  private SurveyRepository surveyRepository;
  @Autowired
  private QuestionRepository questionRepository;
  private SurveyService surveyService;
  private QuestionService questionService;

  public SurveyController() {}

  @RequestMapping(value="/survey", method=RequestMethod.GET)
  public void welcome(HttpServletRequest request, HttpServletResponse response) {
    this.surveyService = new SurveyService(surveyRepository);
    this.questionService = new QuestionService(questionRepository);

    Survey lastSurvey = surveyService.findLast();

    String welcomeMessage = "Welcome to the " + lastSurvey.getTitle() + "!";

    TwiMLResponse twiml = new TwiMLResponse();
    Say say = new Say(welcomeMessage + surveyService.findSurveyQuestions(lastSurvey).get(0).getBody());
    try {
      twiml.append(say);
    } catch (TwiMLException e) {
      e.printStackTrace();
    }
    Redirect redirect = new Redirect("/question?q=");
    response.setContentType("application/xml");
    try {
      response.getWriter().print(twiml.toXML());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

//  @RequestMapping("/")
//  public String welcome(Map<String, Object> model) {
//    model.put("time", new Date());
//    model.put("message", "hola hola");
//    return "welcome";
//  }
}
