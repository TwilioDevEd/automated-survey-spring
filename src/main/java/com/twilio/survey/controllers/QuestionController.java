package com.twilio.survey.controllers;

import com.twilio.sdk.verbs.TwiMLResponse;
import com.twilio.survey.models.Question;
import com.twilio.survey.models.Survey;
import com.twilio.survey.repositories.SurveyRepository;
import com.twilio.survey.services.SurveyService;
import com.twilio.survey.util.QuestionHandler;
import com.twilio.survey.util.SMSQuestionHandler;
import com.twilio.survey.util.VoiceQuestionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@Controller
public class QuestionController {
  @Autowired
  private SurveyRepository surveyRepository;
  private SurveyService surveyService;

  public QuestionController() {}

  /**
   * End point that returns the appropriate question response based on the parameters it receives
   */
  @RequestMapping(value = "/question", method = RequestMethod.GET)
  public void readQuestion(HttpServletRequest request, HttpServletResponse response) {
    this.surveyService = new SurveyService(surveyRepository);

    Long surveyId = null;
    int questionNumber = 0;


    try {
      surveyId = Long.parseLong(request.getParameter("survey"));
      questionNumber = Integer.parseInt(request.getParameter("question"));
    } catch (NumberFormatException e) {
      System.out.println("Numbers wrongly formatted, unable to parse");
    }

    Survey survey = surveyService.find(surveyId);
    List<Question> questions = survey.getQuestions();


    if (questionNumber <= questions.size()) {
      try {
        Question currentQuestion = questions.get(questionNumber - 1);
        HttpSession session = request.getSession(true);
        session.setAttribute("questionId", currentQuestion.getId());
        QuestionHandler handler = this.getQuestionHandler(currentQuestion, request);
        response.getWriter().print(handler.getTwilioResponse().toEscapedXML());
      } catch (IOException e) {
        System.out.println("Couldn't write Twilio's response to XML");
      }
    } else {
      try {
        response.getWriter().print(VoiceQuestionHandler.getHangupResponse().toEscapedXML());
      } catch (IOException e) {
        System.out.println("Couldn't write Twilio's response to XML");
      }
    }
    response.setContentType("application/xml");
  }

  private QuestionHandler getQuestionHandler(Question currentQuestion, HttpServletRequest request) {
    QuestionHandler questionHandler;

    if (request.getParameter("MessageSid")==null) {
      questionHandler = new VoiceQuestionHandler(currentQuestion);
    }else{
      questionHandler = new SMSQuestionHandler(currentQuestion);
    }

    return questionHandler;
  }
}
