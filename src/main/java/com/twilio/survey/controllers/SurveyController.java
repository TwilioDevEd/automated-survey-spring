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
   * Calls endpoint; Welcomes a user and redirects to the question controller if there is a survey to be answered.
   * Otherwise it plays a message and hang up the call if there is no survey available.
   */
  @RequestMapping(value = "/survey/call", method = RequestMethod.GET)
  public void welcomeCall(HttpServletRequest request, HttpServletResponse response) throws Exception {
    this.surveyService = new SurveyService(surveyRepository);

    Survey lastSurvey = surveyService.findLast();

    if (lastSurvey != null) {
      response.getWriter().print(getFirstQuestionRedirect(lastSurvey, request));
    } else {
      response.getWriter().print(getHangupResponse(request));
    }
    response.setContentType("application/xml");
  }

  /**
   * SMS endpoint; Welcomes a user and redirects to the question controller if there is a survey to be answered.
   * As SMS is just a message instead of a long running call, we store state by mapping a Twilio's Cookie to a Session
   */
  @RequestMapping(value = "/survey/sms", method = RequestMethod.GET)
  public void welcomeSMS(HttpServletRequest request, HttpServletResponse response) throws Exception {
    this.surveyService = new SurveyService(surveyRepository);

    Survey lastSurvey = surveyService.findLast();
    HttpSession session = request.getSession(false);

    if (lastSurvey != null) {
      if (session == null || session.isNew()) {
        // New session,
        response.getWriter().print(getFirstQuestionRedirect(lastSurvey, request));
      }else{
        // Ongoing session, redirect to ResponseController to save it's answer.
        response.getWriter().print(getSaveResponseRedirect(session));
      }
    } else {
      // No survey
      response.getWriter().print(getHangupResponse(request));
    }
    response.setContentType("application/xml");
  }

  private String getSaveResponseRedirect(HttpSession session) throws TwiMLException {
    return new TwiMLResponse().append(new Redirect("/save_response?qid=" + getQuestionIdFromSession(session))).toEscapedXML();
  }

  private Long getQuestionIdFromSession(HttpSession session) {
    return (Long) session.getAttribute("questionId");
  }

  /**
   * Creates the TwiMLResponse for the first question of the survey
   *
   * @param survey Survey entity
   * @param request HttpServletRequest request
   * @return TwiMLResponse
   */
  private String getFirstQuestionRedirect(Survey survey, HttpServletRequest request) throws Exception{
    TwiMLResponse twiml = new TwiMLResponse();
    String welcomeMessage = "Welcome to the " + survey.getTitle() + " survey";

    Verb welcomeVerb;
    if(request.getParameter("MessageSid") == null) {
      welcomeVerb = new Say(welcomeMessage);
    }else{
      welcomeVerb = new Message(welcomeMessage);
    }

    Redirect redirect = new Redirect("/question?survey=" + survey.getId() + "&question=1");
    redirect.setMethod("GET");

    twiml.append(welcomeVerb);
    twiml.append(redirect);
    return twiml.toEscapedXML();
  }

  /**
   * Creates a TwiMLResponse if no surveys are found on the database
   * For SMS, it's just a message
   * For Voice it should also send a Hangup to the ongoing call
   *
   * @return TwiMLResponse
   */
  private String getHangupResponse(HttpServletRequest request) throws Exception{
    String errorMessage = "We are sorry, there are no surveys available. Good bye.";

    TwiMLResponse twiml = new TwiMLResponse();
      if (request.getParameter("MessageSid") == null){
        twiml.append(new Say(errorMessage));
        twiml.append(new Hangup());
      }else{
        twiml.append(new Message(errorMessage));
        HttpSession session = request.getSession(false);
        if(session!=null){
          session.invalidate();
        }
      }
    return twiml.toEscapedXML();
  }
}
