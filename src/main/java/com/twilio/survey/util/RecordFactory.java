package com.twilio.survey.util;

import com.twilio.survey.models.Question;
import com.twilio.twiml.Gather;
import com.twilio.twiml.Method;
import com.twilio.twiml.Record;

public class RecordFactory {
    public static Record record(Question question) {
        return new Record.Builder()
                .action("/save_response?qid=" + question.getId())
                .method(Method.POST)
                .transcribe(true)
                .transcribeCallback("/save_response?qid=" + question.getId())
                .maxLength(60)
                .build();
    }

    public static Gather gather(Question question) {
        return new Gather.Builder()
                .action("/save_response?qid=" + question.getId())
                .method(Method.POST)
                .finishOnKey("#")
                .build();
    }

}
