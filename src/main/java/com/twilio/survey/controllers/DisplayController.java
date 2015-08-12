package com.twilio.survey.controllers;

import com.twilio.sdk.verbs.Redirect;
import com.twilio.sdk.verbs.Say;
import com.twilio.sdk.verbs.TwiMLException;
import com.twilio.sdk.verbs.TwiMLResponse;
import com.twilio.survey.models.Response;
import com.twilio.survey.models.Survey;
import com.twilio.survey.repositories.ResponseRepository;
import com.twilio.survey.repositories.SurveyRepository;
import com.twilio.survey.services.ResponseService;
import com.twilio.survey.services.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

@Controller
public class DisplayController {
  @Autowired
  private SurveyRepository surveyRepository;
  private SurveyService surveyService;
  @Autowired
  private ResponseRepository responseRepository;
  private ResponseService responseService;

  public DisplayController() {}

  @RequestMapping(value = "/", method = RequestMethod.GET)
  public String index(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
    this.surveyService = new SurveyService(surveyRepository);
    this.responseService = new ResponseService(responseRepository);

    Survey lastSurvey = surveyService.findLast();
    model.put("surveyTitle", lastSurvey.getTitle());

    model.put("questions", lastSurvey.getQuestions());

    return "index";
  }
}
