package com.twilio.survey.util;

import com.twilio.survey.models.Question;
import com.twilio.survey.models.Response;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Class returns the appropriate Response model
 * when you call the parse() method
 */
public class ResponseParser {
    private Question question;
    private HttpServletRequest request;

    public ResponseParser(Question question, HttpServletRequest request) {
        this.question = question;
        this.request = request;
    }

    public Response parse() {
        String contentKey;
        if (hasTextContent()) {
            contentKey = isValidTranscription() ? "RecordingUrl" : "TranscriptionText";
        } else {
            contentKey = "Digits";
        }
        return buildResponse(contentKey);
    }

    private boolean isValidTranscription() {
        return request.getParameter("TranscriptionText") == null;
    }

    private boolean hasTextContent() {
        return question.getType().equals("text");
    }

    private boolean isSMS() {
        return request.getParameter("MessageSid") != null;
    }

    private Response buildResponse(String contentKey) {
        String sessionKey = isSMS() ? "MessageSid" : "CallSid";
        if (isSMS()) {
            contentKey = "Body";
        }
        String content = request.getParameter(contentKey);
        String sessionSid = request.getParameter(sessionKey);
        return new Response(content, sessionSid, question, new Date());
    }


}
