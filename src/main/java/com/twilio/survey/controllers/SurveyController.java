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
import javax.servlet.http.HttpSession;
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
    boolean sms = request.getParameter("MessageSid")!=null;

    Survey lastSurvey;
    try {
      lastSurvey = surveyService.findLast();
    } catch (IndexOutOfBoundsException e) {
      lastSurvey = null;
    }

    if (lastSurvey != null) {
      try {
        HttpSession session = request.getSession(true);
        if (session.getAttribute("questionId") == null) {
          response.getWriter().print(getFirstQuestionRedirect(lastSurvey, sms).toEscapedXML());
        }else{
          Long questionId = (Long) session.getAttribute("questionId");
          TwiMLResponse twiml = new TwiMLResponse();
          twiml.append(new Redirect("/save_response?qid=" + questionId));
          response.getWriter().print(twiml.toEscapedXML());
        }
      } catch (IOException e) {
        System.out.println("Couldn't write Twilio's response to XML");
      } catch (TwiMLException e) {
        System.out.println("Couldn't write Twilio's response to XML");
      }
    } else {
      try {
        response.getWriter().print(getHangupResponse(sms).toEscapedXML());
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
   * @param sms
   * @return TwiMLResponse
   */
  private TwiMLResponse getFirstQuestionRedirect(Survey survey, boolean sms) {
    TwiMLResponse twiml = new TwiMLResponse();
    String welcomeMessage = "Welcome to the " + survey.getTitle() + " survey";
    Verb welcomeVerb;
    if(!sms) {
      welcomeVerb = new Say(welcomeMessage);
    }else{
      welcomeVerb = new Message(welcomeMessage);
    }
    Redirect redirect = new Redirect("/question?survey=" + survey.getId() + "&question=1");
    redirect.setMethod("GET");
    try {
      twiml.append(welcomeVerb);
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
  private TwiMLResponse getHangupResponse(boolean sms) {
    String errorMessage = "We are sorry, there are no surveys available. Good bye.";

    TwiMLResponse twiml = new TwiMLResponse();
     try {
      if (!sms){
        twiml.append(new Say(errorMessage));
        twiml.append(new Hangup());
      }else{
        twiml.append(new Message(errorMessage));
      }

    } catch (TwiMLException e) {
      System.out.println("Couldn't append say or redirect to Twilio's response");
    }

    return twiml;
  }
}
