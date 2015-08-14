package com.twilio.survey.controllers;

import com.twilio.sdk.verbs.*;
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

  /**
   * End point that always returns the last survey inserted in the db
   * and redirects to the first question of that survey
   */
  @RequestMapping(value = "/survey", method = RequestMethod.GET)
  public void welcome(HttpServletRequest request, HttpServletResponse response) {
    this.surveyService = new SurveyService(surveyRepository);

    Survey lastSurvey;
    try {
      lastSurvey = surveyService.findLast();
    } catch (IndexOutOfBoundsException e) {
      lastSurvey = null;
    }

    if (lastSurvey != null) {
      try {
        response.getWriter().print(getFirstQuestionRedirect(lastSurvey).toEscapedXML());
      } catch (IOException e) {
        System.out.println("Couldn't write Twilio's response to XML");
      }
    } else {
      try {
        response.getWriter().print(getHangupResponse().toEscapedXML());
      } catch (IOException e) {
        System.out.println("Couldn't write Twilio's response to XML");
      }
    }
    response.setContentType("application/xml");
  }

  /**
   * Creates the TwiMLResponse for the first question of the survey
   *
   * @param survey Survey entity
   * @return TwiMLResponse
   */
  private TwiMLResponse getFirstQuestionRedirect(Survey survey) {
    String welcomeMessage = "Welcome to the " + survey.getTitle() + " survey";

    TwiMLResponse twiml = new TwiMLResponse();
    Say say = new Say(welcomeMessage);
    Redirect redirect = new Redirect("/question?survey=" + survey.getId() + "&question=1");
    redirect.setMethod("GET");
    try {
      twiml.append(say);
      twiml.append(redirect);
    } catch (TwiMLException e) {
      System.out.println("Couldn't append say or redirect to Twilio's response");
    }

    return twiml;
  }

  /**
   * Creates a TwiMLResponse for Hangup if no surveys are found on the database
   *
   * @return TwiMLResponse
   */
  private TwiMLResponse getHangupResponse() {
    String errorMessage = "We are sorry, there are no surveys available. Good bye.";

    TwiMLResponse twiml = new TwiMLResponse();
    Say say = new Say(errorMessage);
    Hangup hangup = new Hangup();
    try {
      twiml.append(say);
      twiml.append(hangup);
    } catch (TwiMLException e) {
      System.out.println("Couldn't append say or redirect to Twilio's response");
    }

    return twiml;
  }
}
