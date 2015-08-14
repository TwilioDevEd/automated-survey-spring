package com.twilio.survey.util;

import com.twilio.survey.models.Question;
import com.twilio.survey.models.Response;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public class ResponseHandler {
  Question question;
  HttpServletRequest request;

  public ResponseHandler(Question question, HttpServletRequest request) {
    this.question = question;
    this.request = request;
  }

  public Response getResponse() {
    switch (question.getType()) {
      case "voice":
        return getVoiceResponse();
      case "numeric":
        return getNumericResponse();
      case "yes-no":
        return getYesNoResponse();
      default:
        return new Response();
    }
  }

  private Response getVoiceResponse() {
    String recordedUrl = request.getParameter("RecordingUrl");
    String callSid = request.getParameter("CallSid");
    Response response = new Response(recordedUrl, callSid, question, new Date());

    return response;
  }

  private Response getNumericResponse() {
    String pressedNumber = request.getParameter("Digits");
    String callSid = request.getParameter("CallSid");
    Response response = new Response(pressedNumber, callSid, question, new Date());

    return response;
  }

  private Response getYesNoResponse() {
    String pressedNumber = request.getParameter("Digits");
    String callSid = request.getParameter("CallSid");
    Response response = new Response(pressedNumber, callSid, question, new Date());

    return response;
  }
}
