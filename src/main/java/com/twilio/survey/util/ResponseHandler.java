package com.twilio.survey.util;

import com.twilio.survey.models.Question;
import com.twilio.survey.models.Response;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Digits;
import java.util.Date;

/**
 * Class returns the appropriate Response model
 * when you call the getResponse() method
 */
public class ResponseHandler {
  private Question question;
  private HttpServletRequest request;

  public ResponseHandler(Question question, HttpServletRequest request) {
    this.question = question;
    this.request = request;
  }

  public Response getResponse() {
    String contentKey;
    if (question.getType().equals("text")) {
      contentKey = (request.getParameter("TranscriptionText") == null) ? "RecordingUrl" : "TranscriptionText";
    }else{
      contentKey = "Digits";
    }
    return buildResponse(contentKey);
  }

  private Response buildResponse(String contentKey) {
    String sessionKey = isSMS() ? "MessageSid" : "CallSid";
    if(isSMS()){
      contentKey = "Body";
    }
    String content = request.getParameter(contentKey);

    String sessionSid = request.getParameter(sessionKey);
    return new Response(content, sessionSid, question, new Date());
  }

  private boolean isSMS() {
    return request.getParameter("MessageSid")!=null;
  }

}
