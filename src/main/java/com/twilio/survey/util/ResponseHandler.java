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
  boolean sms;

  public ResponseHandler(Question question, HttpServletRequest request) {
    this.question = question;
    this.request = request;
    this.sms = request.getParameter("MessageSid")!=null;
  }

  public Response getResponse() {
    if(this.sms){
      return getSMSResponse();
    }
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

  private Response getSMSResponse() {
    String text = request.getParameter("Body");
    String sessionSid = request.getParameter("MessageSid");
    Response response = new Response(text, sessionSid, question, new Date());
    return response;
  }

  private Response getTextResponse() {
    String text = request.getParameter("TranscriptionText");
    if(text == null){
      text = request.getParameter("RecordingUrl");
    }
    String sessionSid = request.getParameter("CallSid");
    Response response = new Response(text, sessionSid, question, new Date());

    return response;
  }

  private Response getNumericResponse() {
    String pressedNumber = request.getParameter("Digits");
    String sessionSid = request.getParameter("CallSid");
    Response response = new Response(pressedNumber, sessionSid, question, new Date());

    return response;
  }

  private Response getYesNoResponse() {
    String pressedNumber = request.getParameter("Digits");
    String sessionSid = request.getParameter("CallSid");
    Response response = new Response(pressedNumber, sessionSid, question, new Date());

    return response;
  }
}
