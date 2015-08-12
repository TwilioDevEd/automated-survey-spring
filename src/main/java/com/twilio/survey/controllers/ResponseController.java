package com.twilio.survey.controllers;

import com.twilio.sdk.verbs.*;
import com.twilio.survey.models.Question;
import com.twilio.survey.models.Response;
import com.twilio.survey.models.Survey;
import com.twilio.survey.repositories.QuestionRepository;
import com.twilio.survey.repositories.ResponseRepository;
import com.twilio.survey.repositories.SurveyRepository;
import com.twilio.survey.services.QuestionService;
import com.twilio.survey.services.ResponseService;
import com.twilio.survey.services.SurveyService;
import com.twilio.survey.util.ResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
public class ResponseController {
  @Autowired
  private QuestionRepository questionRepository;
  private QuestionService questionService;
  @Autowired
  private ResponseRepository responseRepository;
  private ResponseService responseService;

  public ResponseController() {}

  @RequestMapping(value = "/save_response", method = RequestMethod.POST)
  public void readQuestion(HttpServletRequest request, HttpServletResponse response) {
    this.questionService = new QuestionService(questionRepository);
    this.responseService = new ResponseService(responseRepository);

    Long questionId = null;

    try {
      questionId = Long.parseLong(request.getParameter("qid"));
    } catch (NumberFormatException e) {
      System.out.println("Numbers wrongly formatted, unable to parse");
    }

    Question currentQuestion = questionService.find(questionId);

    ResponseHandler responseHandler = new ResponseHandler(currentQuestion, request);

    Response questionResponse = responseHandler.getResponse();

    responseService.create(questionResponse);
    Survey survey = currentQuestion.getSurvey();
    List<Question> questions = survey.getQuestions();
    int questionIndex = questions.indexOf(currentQuestion);
    TwiMLResponse twiml = new TwiMLResponse();
    if (questionIndex < questions.size() - 1) {
      response.setStatus(HttpServletResponse.SC_SEE_OTHER);
      try {
        response.sendRedirect("question?survey=" + survey.getId() + "");
      } catch (IOException e) {
        System.out.println("Unable to redirect to next question");
      }
      int nextQuestionNumber = questionIndex + 2;

      System.out.println("/question?survey=" + survey.getId() + "&q=" + nextQuestionNumber);
      Redirect redirect = new Redirect("/question?q=" + nextQuestionNumber + "&survey=" + survey.getId());
      redirect.setMethod("GET");
      try {
        twiml.append(redirect);
      } catch (TwiMLException e) {
        System.out.println("Couldn't append redirect to Twilio's response");
      }
    }
    else {
      Hangup hangup = new Hangup();
      try {
        twiml.append(hangup);
      } catch (TwiMLException e) {
        System.out.println("Couldn't append redirect to Twilio's response");
      }
    }
    try {
      System.out.println();
      response.getWriter().print(twiml.toEscapedXML());
    } catch (IOException e) {
      System.out.println("Couldn't write Twilio's response to XML");
    }
    response.setContentType("application/xml");
  }
}
