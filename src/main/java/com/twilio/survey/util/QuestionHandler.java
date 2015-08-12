package com.twilio.survey.util;

import com.twilio.sdk.verbs.*;
import com.twilio.survey.models.Question;

public class QuestionHandler {
  Question question;

  public QuestionHandler(Question question) {
    this.question = question;
  }

  public TwiMLResponse getTwilioResponse() {
    TwiMLResponse response = new TwiMLResponse();
    switch (question.getType()) {
      case "voice":
        return getVoiceResponse(response);
      case "numeric":
        return getNumericResponse(response);
      case "yes-no":
        return getYesNoResponse(response);
      default:
        return response;
    }
  }

  private TwiMLResponse getVoiceResponse(TwiMLResponse response) {
    String defaultMessage = "Record your answer after the beep and press the pound key when you are done.";
    Say say = new Say(defaultMessage + question.getBody());
    Record record = new Record();
    record.setAction("/save_response?qid=" + question.getId());
    record.setMethod("POST");
    record.setFinishOnKey("#");

    try {
      response.append(say);
      response.append(record);
    } catch (TwiMLException e) {
      System.out.println("Couldn't append say or record to Twilio's response");
    }
    System.out.println(response.toEscapedXML());
    return response;
  }

  private TwiMLResponse getNumericResponse(TwiMLResponse response) {
    String defaultMessage = "For the next question select a number with the dial pad and the press the pound key";
    Say say = new Say(defaultMessage + question.getBody());
    Gather gather = new Gather();
    gather.setAction("/save_response?qid=" + question.getId());
    gather.setMethod("POST");
    gather.setFinishOnKey("#");

    try {
      response.append(say);
      response.append(gather);
    } catch (TwiMLException e) {
      System.out.println("Couldn't append say or gather to Twilio's response");
    }
    System.out.println(response.toEscapedXML());
    return response;
  }

  private TwiMLResponse getYesNoResponse(TwiMLResponse response) {
    String defaultMessage = "For the next question, press 1 for yes, and 2 for no. Then press the pound key.";
    Say say = new Say(defaultMessage + question.getBody());
    Gather gather = new Gather();
    gather.setAction("/save_response?qid=" + question.getId());
    gather.setMethod("POST");
    gather.setFinishOnKey("#");

    try {
      response.append(say);
      response.append(gather);
    } catch (TwiMLException e) {
      System.out.println("Couldn't append say or gather to Twilio's response");
    }
    System.out.println(response.toEscapedXML());
    return response;
  }
}
