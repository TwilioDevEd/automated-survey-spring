package com.twilio.survey.util;

import com.twilio.survey.models.Question;
import com.twilio.survey.models.Response;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Class returns the appropriate Response model
 * when you call the getResponse() method
 */
public class ResponseHandler {
  Question question;
  HttpServletRequest request;

  public ResponseHandler(Question question, HttpServletRequest request) {
    this.question = question;
    this.request = request;
  }

  public Response getResponse() {
    switch (question.getType()) {
      case "text":
        return getTextResponse();
      case "numeric":
        return getNumericResponse();
      case "yes-no":
        return getYesNoResponse();
      default:
        return new Response();
    }
  }

  private Response getTextResponse() {
    String recordedUrl = request.getParameter("RecordingUrl");
    String sessionSid = request.getParameter("SessionSid");
    Response response = new Response(recordedUrl, sessionSid, question, new Date());

    return response;
  }

  private Response getNumericResponse() {
    String pressedNumber = request.getParameter("Digits");
    String sessionSid = request.getParameter("SessionSid");
    Response response = new Response(pressedNumber, sessionSid, question, new Date());

    return response;
  }

  private Response getYesNoResponse() {
    String pressedNumber = request.getParameter("Digits");
    String sessionSid = request.getParameter("SessionSid");
    Response response = new Response(pressedNumber, sessionSid, question, new Date());

    return response;
  }
}
