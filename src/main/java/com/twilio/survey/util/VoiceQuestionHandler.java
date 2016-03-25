package com.twilio.survey.util;

import com.twilio.sdk.verbs.*;
import com.twilio.survey.models.Question;

/**
 * Class responsible of returning the appropriate TwiMLResponse based on the question
 * it receives
 */
public class VoiceQuestionHandler implements QuestionHandler{
  private Question question;
  private String recordingInstructions =
          "Record your answer after the beep.";
  String numericInstructions =
          "For the next question select a number with the dial pad and then press the pound key";
  String booleanInstructions =
          "For the next question, press 1 for yes, and 0 for no. Then press the pound key.";
  private String errorMessage =
          "We are sorry, there are no more questions available for this survey. Good bye.";


  public VoiceQuestionHandler(Question question) {
    this.question = question;
  }

  /**
   * Bases on the question's type, a specific method is called. This method will construct
   * the specific TwiMLResponse
   */
  public String getTwilioResponse() throws TwiMLException{
    switch (question.getType()) {
      case "text":
        return getRecordTwiML();
      case "numeric":
        return getGatherResponse(numericInstructions);
      case "yes-no":
        return getGatherResponse(booleanInstructions);
      default:
        return getHangupResponse();
    }
  }

  /**
   * method that returns a generic TwiMLResponse when an non existent question is requested
   */
  public String getHangupResponse() throws TwiMLException{
    TwiMLResponse response =  new TwiMLResponse();
    response.append(new Say(errorMessage));
    response.append(new Hangup());
    return response.toEscapedXML();
  }

  private String getRecordTwiML() throws TwiMLException {
    TwiMLResponse response = new TwiMLResponse();

    response.append(new Say(recordingInstructions));
    response.append(new Pause());
    response.append(new Say(question.getBody()));

    Record record = new Record();
    record.setAction("/save_response?qid=" + question.getId());
    record.setMethod("POST");
    record.setTranscribe(true);
    record.setTranscribeCallback("/save_response?qid=" + question.getId());
    record.setMaxLength(60);
    response.append(record);

    return response.toEscapedXML();
  }

  private String getGatherResponse(String defaultMessage) throws TwiMLException {
    TwiMLResponse response = new TwiMLResponse();

    response.append(new Say(defaultMessage));
    response.append(new Pause());
    response.append(new Say(question.getBody()));

    Gather gather = new Gather();
    gather.setAction("/save_response?qid=" + question.getId());
    gather.setMethod("POST");
    gather.setFinishOnKey("#");
    response.append(gather);

    return response.toEscapedXML();
  }
}
