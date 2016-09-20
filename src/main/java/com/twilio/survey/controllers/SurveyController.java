package com.twilio.survey.controllers;

import com.twilio.survey.models.Survey;
import com.twilio.survey.repositories.SurveyRepository;
import com.twilio.survey.services.SurveyService;
import com.twilio.survey.util.TwiMLUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class SurveyController {
    @Autowired
    private SurveyRepository surveyRepository;
    private SurveyService surveyService;

    public SurveyController() {
    }

    /**
     * Calls endpoint; Welcomes a user and redirects to the question controller if there is a survey to be answered.
     * Otherwise it plays a message and hang up the call if there is no survey available.
     */
    @RequestMapping(value = "/survey/call", method = RequestMethod.GET)
    public void call(HttpServletRequest request, HttpServletResponse response) throws Exception {
        this.surveyService = new SurveyService(surveyRepository);

        Survey lastSurvey = surveyService.findLast();

        if (lastSurvey != null) {
            response.getWriter().print(getFirstQuestionRedirect(lastSurvey, request));
        } else {
            response.getWriter().print(getHangupResponse(request));
        }
        response.setContentType("application/xml");
    }

    /**
     * SMS endpoint; Welcomes a user and redirects to the question controller if there is a survey to be answered.
     * As SMS is just a message instead of a long running call, we store state by mapping a Twilio's Cookie to a Session
     */
    @RequestMapping(value = "/survey/sms", method = RequestMethod.GET)
    public void sms(HttpServletRequest request, HttpServletResponse response) throws Exception {
        this.surveyService = new SurveyService(surveyRepository);

        Survey lastSurvey = surveyService.findLast();
        HttpSession session = request.getSession(false);

        if (lastSurvey != null) {
            if (session == null || session.isNew()) {
                // New session,
                response.getWriter().print(getFirstQuestionRedirect(lastSurvey, request));
            } else {
                // Ongoing session, redirect to ResponseController to save it's answer.
                response.getWriter().print(getSaveResponseRedirect(session));
            }
        } else {
            // No survey
            response.getWriter().print(getHangupResponse(request));
        }
        response.setContentType("application/xml");
    }

    private String getSaveResponseRedirect(HttpSession session) throws Exception {
        String saveURL = "/save_response?qid=" + getQuestionIdFromSession(session);
        return TwiMLUtil.redirectPost(saveURL);
    }

    /**
     * Creates the TwiMLResponse for the first question of the survey
     *
     * @param survey  Survey entity
     * @param request HttpServletRequest request
     * @return TwiMLResponse
     */
    private String getFirstQuestionRedirect(Survey survey, HttpServletRequest request) throws Exception {
        String welcomeMessage = "Welcome to the " + survey.getTitle() + " survey";
        String questionURL = "/question?survey=" + survey.getId() + "&question=1";
        if (request.getParameter("MessageSid") != null) {
            return TwiMLUtil.messagingResponseWithRedirect(welcomeMessage, questionURL);
        } else {
            return TwiMLUtil.voiceResponseWithRedirect(welcomeMessage, questionURL);
        }
    }

    /**
     * Creates a TwiMLResponse if no surveys are found on the database
     * For SMS, it's just a message
     * For Voice it should also send a Hangup to the ongoing call
     *
     * @return TwiMLResponse
     */
    private String getHangupResponse(HttpServletRequest request) throws Exception {
        String errorMessage = "We are sorry, there are no surveys available. Good bye.";
        cleanSession(request);
        if (request.getParameter("MessageSid") != null) {
            return TwiMLUtil.messagingResponse(errorMessage);
        } else {
            return TwiMLUtil.voiceResponse(errorMessage);
        }
    }

    private void cleanSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    private Long getQuestionIdFromSession(HttpSession session) {
        return (Long) session.getAttribute("questionId");
    }
}
