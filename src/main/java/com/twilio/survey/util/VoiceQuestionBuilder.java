package com.twilio.survey.util;

import com.twilio.sdk.verbs.TwiMLException;
import com.twilio.survey.models.Question;

/**
 * Class responsible of returning the appropriate TwiMLResponse based on the question
 * it receives
 */
public class VoiceQuestionBuilder implements QuestionBuilder {
    private Question question;
    private String recordingInstructions =
            "Record your answer after the beep.";
    String numericInstructions =
            "For the next question select a number with the dial pad and then press the pound key";
    String booleanInstructions =
            "For the next question, press 1 for yes, and 0 for no. Then press the pound key.";
    private String errorMessage =
            "We are sorry, there are no more questions available for this survey. Good bye.";


    public VoiceQuestionBuilder(Question question) {
        this.question = question;
    }

    /**
     * Bases on the question's type, a specific method is called. This method will construct
     * the specific TwiMLResponse
     */
    public String build() throws TwiMLException {
        switch (question.getType()) {
            case "text":
                return getRecordTwiML();
            case "numeric":
                return getGatherResponse(numericInstructions);
            case "yes-no":
                return getGatherResponse(booleanInstructions);
            default:
                return buildNoMoreQuestions();
        }
    }

    /**
     * method that returns a generic TwiMLResponse when an non existent question is requested
     */
    public String buildNoMoreQuestions() throws TwiMLException {
        return new TwiMLResponseBuilder().say(errorMessage).hangup().asString();
    }

    private String getRecordTwiML() throws TwiMLException {
        return new TwiMLResponseBuilder().say(recordingInstructions).pause().say(question.getBody()).record(question)
                .asString();
    }

    private String getGatherResponse(String defaultMessage) throws TwiMLException {
        return new TwiMLResponseBuilder().say(defaultMessage).pause().say(question.getBody()).gather(question).asString();
    }
}
