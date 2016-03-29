package com.twilio.survey.controllers;

import com.twilio.survey.models.Question;
import com.twilio.survey.models.Survey;
import com.twilio.survey.repositories.SurveyRepository;
import com.twilio.survey.services.SurveyService;
import com.twilio.survey.util.QuestionBuilder;
import com.twilio.survey.util.SMSQuestionBuilder;
import com.twilio.survey.util.VoiceQuestionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class QuestionController {
    @Autowired
    private SurveyRepository surveyRepository;
    private SurveyService surveyService;

    public QuestionController() {
    }

    /**
     * End point that returns the appropriate question response based on the parameters it receives
     */
    @RequestMapping(value = "/question", method = RequestMethod.GET)
    public void show(HttpServletRequest request, HttpServletResponse response) throws Exception {
        this.surveyService = new SurveyService(surveyRepository);
        Survey survey = surveyService.find(Long.parseLong(request.getParameter("survey")));

        Question currentQuestion = survey.getQuestionByNumber(Integer.parseInt(request.getParameter("question")));
        QuestionBuilder builder = getQuestionHandler(currentQuestion, request);

        if (currentQuestion != null) {
            response.getWriter().print(builder.build());
        } else {
            response.getWriter().print(builder.buildNoMoreQuestions());
        }
        response.setContentType("application/xml");
    }

    private void createSessionForQuestion(HttpServletRequest request, Question currentQuestion) {
        if (currentQuestion == null) {
            return;
        }
        HttpSession session = request.getSession(true);
        session.setAttribute("questionId", currentQuestion.getId());
    }

    private QuestionBuilder getQuestionHandler(Question currentQuestion, HttpServletRequest request) {
        if (isVoiceRequest(request)) {
            return new VoiceQuestionBuilder(currentQuestion);
        } else {
            createSessionForQuestion(request, currentQuestion);
            return new SMSQuestionBuilder(currentQuestion);
        }
    }

    private boolean isVoiceRequest(HttpServletRequest request) {
        return request.getParameter("MessageSid") == null;
    }
}
