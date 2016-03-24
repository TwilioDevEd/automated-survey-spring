package com.twilio.survey.util;

import com.twilio.sdk.verbs.*;
import com.twilio.survey.models.Question;

/**
 * Class responsible of returning the appropriate TwiMLResponse based on the question
 * it receives
 */
public class SMSQuestionHandler implements QuestionHandler{
  Question question;

  public SMSQuestionHandler(Question question) {
    this.question = question;
  }

  /**
   * Bases on the question's type, a specific method is called. This method will construct
   * the specific TwiMLResponse
   */
  public String getTwilioResponse() throws TwiMLException{
    switch (question.getType()) {
      case "numeric":
        return getNumericResponse();
      case "yes-no":
        return getYesNoResponse();
      default:
        return renderTwiMLMessage(question.getBody());
    }
  }

  public String getHangupResponse() throws TwiMLException {
    String errorMessage =
            "We are sorry, there are no more questions available for this survey. Good bye.";
    return renderTwiMLMessage(errorMessage);
  }

  private String renderTwiMLMessage(String questionInstructions) throws TwiMLException {
    TwiMLResponse response = new TwiMLResponse();
    response.append(new Message(questionInstructions));
    return response.toEscapedXML();
  }

  private String getNumericResponse() throws TwiMLException{
    String questionInstructions =
        "For the next question, please answer with a number. " + question.getBody();
    return renderTwiMLMessage(questionInstructions);
  }

  private String getYesNoResponse() throws TwiMLException{
    String questionInstructions =
        "For the next question, type 1 for yes, and 0 for no. " + question.getBody();
    return renderTwiMLMessage(questionInstructions);
  }
}
