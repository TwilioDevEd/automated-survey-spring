package com.twilio.survey.util;


import com.twilio.twiml.TwiMLException;

/**
 * Created by vshyba on 23/03/16.
 */
public interface QuestionBuilder {
    public String build() throws TwiMLException;

    public String buildNoMoreQuestions() throws TwiMLException;
}
