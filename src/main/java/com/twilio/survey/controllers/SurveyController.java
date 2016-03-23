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
  public void welcome(HttpServletRequest request, HttpServletResponse response) throws Exception {
    this.surveyService = new SurveyService(surveyRepository);
    boolean isSms = request.getParameter("MessageSid")!=null;

    Survey lastSurvey = surveyService.findLast();

    if (lastSurvey != null) {
      HttpSession session = request.getSession(true);
      if (session.getAttribute("questionId") == null) {
        response.getWriter().print(getFirstQuestionRedirect(lastSurvey, isSms).toEscapedXML());
      }else{
        Long questionId = (Long) session.getAttribute("questionId");
        TwiMLResponse twiml = new TwiMLResponse();
        twiml.append(new Redirect("/save_response?qid=" + questionId));
        response.getWriter().print(twiml.toEscapedXML());
      }
    } else {
      response.getWriter().print(getHangupResponse(isSms).toEscapedXML());
    }
    response.setContentType("application/xml");
  }

  /**
   * Creates the TwiMLResponse for the first question of the survey
   *
   * @param survey Survey entity
   * @param isSms
   * @return TwiMLResponse
   */
  private TwiMLResponse getFirstQuestionRedirect(Survey survey, boolean isSms) throws Exception{
    TwiMLResponse twiml = new TwiMLResponse();
    String welcomeMessage = "Welcome to the " + survey.getTitle() + " survey";
    Verb welcomeVerb;
    if(!isSms) {
      welcomeVerb = new Say(welcomeMessage);
    }else{
      welcomeVerb = new Message(welcomeMessage);
    }
    Redirect redirect = new Redirect("/question?survey=" + survey.getId() + "&question=1");
    redirect.setMethod("GET");

    twiml.append(welcomeVerb);
    twiml.append(redirect);

    return twiml;
  }

  /**
   * Creates a TwiMLResponse for Hangup if no surveys are found on the database
   *
   * @return TwiMLResponse
   */
  private TwiMLResponse getHangupResponse(boolean isSms) throws Exception{
    String errorMessage = "We are sorry, there are no surveys available. Good bye.";

    TwiMLResponse twiml = new TwiMLResponse();
      if (!isSms){
        twiml.append(new Say(errorMessage));
        twiml.append(new Hangup());
      }else{
        twiml.append(new Message(errorMessage));
      }
    return twiml;
  }
}
