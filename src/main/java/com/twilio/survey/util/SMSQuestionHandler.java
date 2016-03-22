package com.twilio.survey.util;

import com.twilio.sdk.verbs.*;
import com.twilio.survey.models.Question;

/**
 * Class responsible of returning the appropriate TwiMLResponse based on the question
 * it receives
 */
public class SMSQuestionHandler {
  Question question;

  public SMSQuestionHandler(Question question) {
    this.question = question;
  }

  /**
   * Static method that returns a generic TwiMLResponse when an non existent question is requested
   */
  public static TwiMLResponse getHangupResponse() {
    String errorMessage =
        "We are sorry, there are no more questions available for this survey. Good bye.";

    TwiMLResponse twiml = new TwiMLResponse();
    Message message = new Message(errorMessage);
    try {
      twiml.append(message);
    } catch (TwiMLException e) {
      System.out.println("Couldn't append say or redirect to Twilio's response");
    }

    return twiml;
  }

  /**
   * Bases on the question's type, a specific method is called. This method will construct
   * the specific TwiMLResponse
   */
  public TwiMLResponse getTwilioResponse() {
    TwiMLResponse response = new TwiMLResponse();
    switch (question.getType()) {
      case "text":
        return getTextResponse(response);
      case "numeric":
        return getNumericResponse(response);
      case "yes-no":
        return getYesNoResponse(response);
      default:
        return response;
    }
  }

  private TwiMLResponse getTextResponse(TwiMLResponse response) {
    Message questionMessage = new Message(question.getBody());

    try {
      response.append(questionMessage);
    } catch (TwiMLException e) {
      System.out.println("Couldn't append say or record to Twilio's response");
    }
    System.out.println(response.toEscapedXML());
    return response;
  }

  private TwiMLResponse getNumericResponse(TwiMLResponse response) {
    String questionInstructions =
        "For the next question, please answer with a number. " + question.getBody();
    Message message = new Message(questionInstructions);

    try {
      response.append(message);
    } catch (TwiMLException e) {
      System.out.println("Couldn't append say or gather to Twilio's response");
    }
    System.out.println(response.toEscapedXML());
    return response;
  }

  private TwiMLResponse getYesNoResponse(TwiMLResponse response) {
    String questionInstructions =
        "For the next question, type 1 for yes, and 0 for no. " + question.getBody();
    Message message = new Message(questionInstructions);

    try {
      response.append(message);
    } catch (TwiMLException e) {
      System.out.println("Couldn't append say or gather to Twilio's response");
    }
    System.out.println(response.toEscapedXML());
    return response;
  }
}
