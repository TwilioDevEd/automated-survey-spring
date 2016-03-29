package com.twilio.survey.util;

import com.twilio.sdk.verbs.*;
import com.twilio.survey.models.Question;

import javax.servlet.http.HttpServletRequest;

/**
 * TwiML Builder
 */
public class TwiMLResponseBuilder {
    private TwiMLResponse response;

    public TwiMLResponseBuilder() {
        response = new TwiMLResponse();
    }

    public TwiMLResponseBuilder message(String content) throws TwiMLException {
        response.append(new Message(content));
        return this;
    }

    public TwiMLResponseBuilder say(String content) throws TwiMLException {
        response.append(new Say(content));
        return this;
    }

    public TwiMLResponseBuilder writeContent(HttpServletRequest request, String content) throws TwiMLException {
        return writeContent(request, content, false);
    }

    public TwiMLResponseBuilder writeContent(HttpServletRequest request, String content, boolean hangup) throws TwiMLException {
        if (request.getParameter("MessageSid") != null) {
            message(content);
        } else {
            say(content);
            if (hangup) {
                hangup();
            }
        }
        return this;
    }

    public TwiMLResponseBuilder hangup() throws TwiMLException {
        response.append(new Hangup());
        return this;
    }

    public TwiMLResponseBuilder pause() throws TwiMLException {
        response.append(new Pause());
        return this;
    }

    public TwiMLResponseBuilder record(Question question) throws TwiMLException {
        Record record = new Record();
        record.setAction("/save_response?qid=" + question.getId());
        record.setMethod("POST");
        record.setTranscribe(true);
        record.setTranscribeCallback("/save_response?qid=" + question.getId());
        record.setMaxLength(60);
        response.append(record);
        return this;
    }

    public TwiMLResponseBuilder gather(Question question) throws TwiMLException {
        Gather gather = new Gather();
        gather.setAction("/save_response?qid=" + question.getId());
        gather.setMethod("POST");
        gather.setFinishOnKey("#");
        response.append(gather);
        return this;
    }

    public TwiMLResponseBuilder redirect(String url) throws TwiMLException {
        Redirect redirect = new Redirect(url);
        redirect.setMethod("GET");
        response.append(redirect);
        return this;
    }

    public TwiMLResponseBuilder redirectPOST(String url) throws TwiMLException {
        Redirect redirect = new Redirect(url);
        redirect.setMethod("POST");
        response.append(redirect);
        return this;
    }

    public String asString() {
        return response.toEscapedXML();
    }

}
