package com.twilio.survey.util;

import com.twilio.sdk.verbs.*;
import com.twilio.survey.models.Question;

/**
 * Class responsible of returning the appropriate TwiMLResponse based on the question
 * it receives
 */
public class VoiceQuestionHandler implements QuestionHandler{
  Question question;

  public VoiceQuestionHandler(Question question) {
    this.question = question;
  }

  /**
   * Static method that returns a generic TwiMLResponse when an non existent question is requested
   */
  public static TwiMLResponse getHangupResponse() {
    String errorMessage =
        "We are sorry, there are no more questions available for this survey. Good bye.";

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
    String defaultMessage =
        "Record your answer after the beep.";
    Say say = new Say(defaultMessage);
    Say questionSay = new Say(question.getBody());
    Pause pause = new Pause();
    Record record = new Record();
    record.setAction("/save_response?qid=" + question.getId());
    record.setMethod("POST");
    record.setTranscribe(true);
    record.setTranscribeCallback("/save_response?qid=" + question.getId());
    record.setMaxLength(60);

    try {
      response.append(say);
      response.append(pause);
      response.append(questionSay);
      response.append(record);
    } catch (TwiMLException e) {
      System.out.println("Couldn't append say or record to Twilio's response");
    }
    System.out.println(response.toEscapedXML());
    return response;
  }

  private TwiMLResponse getNumericResponse(TwiMLResponse response) {
    String defaultMessage =
        "For the next question select a number with the dial pad and then press the pound key";
    Say say = new Say(defaultMessage);
    Say questionSay = new Say(question.getBody());
    Pause pause = new Pause();
    Gather gather = new Gather();
    gather.setAction("/save_response?qid=" + question.getId());
    gather.setMethod("POST");
    gather.setFinishOnKey("#");

    try {
      response.append(say);
      response.append(pause);
      response.append(questionSay);
      response.append(gather);
    } catch (TwiMLException e) {
      System.out.println("Couldn't append say or gather to Twilio's response");
    }
    System.out.println(response.toEscapedXML());
    return response;
  }

  private TwiMLResponse getYesNoResponse(TwiMLResponse response) {
    String defaultMessage =
        "For the next question, press 1 for yes, and 0 for no. Then press the pound key.";
    Say say = new Say(defaultMessage);
    Say questionSay = new Say(question.getBody());
    Pause pause = new Pause();
    Gather gather = new Gather();
    gather.setAction("/save_response?qid=" + question.getId());
    gather.setMethod("POST");
    gather.setFinishOnKey("#");

    try {
      response.append(say);
      response.append(pause);
      response.append(questionSay);
      response.append(gather);
    } catch (TwiMLException e) {
      System.out.println("Couldn't append say or gather to Twilio's response");
    }
    System.out.println(response.toEscapedXML());
    return response;
  }
}
