package com.twilio.survey.controllers;

import com.twilio.sdk.verbs.Redirect;
import com.twilio.sdk.verbs.Say;
import com.twilio.sdk.verbs.TwiMLException;
import com.twilio.sdk.verbs.TwiMLResponse;
import com.twilio.survey.models.Survey;
import com.twilio.survey.repositories.SurveyRepository;
import com.twilio.survey.services.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class SurveyController {
  @Autowired
  private SurveyRepository surveyRepository;
  private SurveyService surveyService;

  public SurveyController() {}

  @RequestMapping(value = "/survey", method = RequestMethod.GET)
  public void welcome(HttpServletRequest request, HttpServletResponse response) {
    this.surveyService = new SurveyService(surveyRepository);

    Survey lastSurvey = surveyService.findLast();
    String welcomeMessage = "Welcome to the " + lastSurvey.getTitle() + " survey";

    TwiMLResponse twiml = new TwiMLResponse();
    Say say = new Say(welcomeMessage);
    Redirect redirect = new Redirect("/question?survey=" + lastSurvey.getId() + "&q=1");
    redirect.setMethod("GET");
    try {
      twiml.append(say);
      twiml.append(redirect);
    } catch (TwiMLException e) {
      System.out.println("Couldn't append say or redirect to Twilio's response");
    }

    try {
      System.out.println();
      response.getWriter().print(twiml.toEscapedXML());
      System.out.println(twiml.toEscapedXML());
    } catch (IOException e) {
      System.out.println("Couldn't write Twilio's response to XML");
    }
    response.setContentType("application/xml");
  }

  //  @RequestMapping("/")
  //  public String welcome(Map<String, Object> model) {
  //    model.put("time", new Date());
  //    model.put("message", "hola hola");
  //    return "welcome";
  //  }
}
