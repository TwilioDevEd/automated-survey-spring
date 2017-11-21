package com.twilio.survey.util;

import com.twilio.survey.models.Question;
import com.twilio.twiml.voice.Gather;
import com.twilio.http.HttpMethod;
import com.twilio.twiml.voice.Record;

public class RecordFactory {
    public static Record record(Question question) {
        return new Record.Builder()
                .action("/save_response?qid=" + question.getId())
                .method(HttpMethod.POST)
                .transcribe(true)
                .transcribeCallback("/save_response?qid=" + question.getId())
                .maxLength(60)
                .build();
    }

    public static Gather gather(Question question) {
        return new Gather.Builder()
                .action("/save_response?qid=" + question.getId())
                .method(HttpMethod.POST)
                .finishOnKey("#")
                .build();
    }

}
