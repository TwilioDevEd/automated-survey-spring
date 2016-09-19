package com.twilio.survey.util;

import com.twilio.survey.models.Question;
import com.twilio.survey.models.Survey;
import com.twilio.twiml.Body;
import com.twilio.twiml.Gather;
import com.twilio.twiml.Hangup;
import com.twilio.twiml.Message;
import com.twilio.twiml.MessagingResponse;
import com.twilio.twiml.Method;
import com.twilio.twiml.Record;
import com.twilio.twiml.Redirect;
import com.twilio.twiml.Say;
import com.twilio.twiml.TwiML;
import com.twilio.twiml.TwiMLException;
import com.twilio.twiml.VoiceResponse;

public class TwiMLUtil {

    public static String redirect(int nextQuestionNumber, Survey survey) throws TwiMLException {
        String nextQuestionURL = "/question?survey=" + survey.getId() + "&question=" + nextQuestionNumber;
        return redirect(nextQuestionURL, Method.GET).toXml();
    }

    public static String redirectPost(String url) throws TwiMLException {
        return redirect(url, Method.POST).toXml();
    }

    private static TwiML redirect(String url, Method method) {
        return new VoiceResponse.Builder()
                .redirect(new Redirect.Builder().url(url).method(method).build())
                .build();
    }

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

    public static String voiceResponse(String message) throws TwiMLException {
        return new VoiceResponse.Builder()
                .say(new Say.Builder(message).build())
                .hangup(new Hangup())
                .build()
                .toXml();
    }

    public static String messagingResponse(String message) throws TwiMLException {
        return new MessagingResponse.Builder()
                .message(new Message.Builder().body(new Body(message)).build())
                .build()
                .toXml();
    }

    public static String voiceResponseWithRedirect(String message, String questionUrl) throws TwiMLException {
        return new VoiceResponse.Builder()
                .say(new Say.Builder(message).build())
                .redirect(new Redirect.Builder().url(questionUrl).method(Method.GET).build())
                .build()
                .toXml();
    }

    public static String messagingResponseWithRedirect(String message, String redirectUrl) throws TwiMLException {
        return new MessagingResponse.Builder()
                .message(new Message.Builder().body(new Body(message)).build())
                .redirect(new Redirect.Builder().url(redirectUrl).method(Method.GET).build())
                .build()
                .toXml();
    }
}
