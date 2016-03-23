package com.twilio.survey.controllers;

import com.twilio.sdk.verbs.*;
import com.twilio.survey.models.Question;
import com.twilio.survey.models.Response;
import com.twilio.survey.models.Survey;
import com.twilio.survey.repositories.QuestionRepository;
import com.twilio.survey.repositories.ResponseRepository;
import com.twilio.survey.services.QuestionService;
import com.twilio.survey.services.ResponseService;
import com.twilio.survey.util.ResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedList;
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

  /**
   * End point that saves a question response and redirects the call to the next question,
   * if one is available.
   */
  @RequestMapping(value = "/save_response", method = RequestMethod.POST)
  public void readQuestion(HttpServletRequest request, HttpServletResponse response) throws Exception{
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

    persistResponse(responseHandler.getResponse());
    Survey survey = currentQuestion.getSurvey();
    List<Question> questions = survey.getQuestions();

    TwiMLResponse twiml = new TwiMLResponse();

    if (currentQuestion.hasNext()) {
      redirectToNextQuestion(currentQuestion, survey, questions, twiml);
    } else {
      this.appendLastMessage(request, twiml, "Tank you for taking the " + survey.getTitle() + " survey. Good Bye");
    }
    response.getWriter().print(twiml.toEscapedXML());
    response.setContentType("application/xml");
  }

  private void redirectToNextQuestion(Question currentQuestion, Survey survey, List<Question> questions, TwiMLResponse twiml) throws TwiMLException {
    int nextQuestionNumber = questions.indexOf(currentQuestion) + 2;
    Redirect redirect =
            new Redirect("/question?survey=" + survey.getId() + "&question=" + nextQuestionNumber);
    redirect.setMethod("GET");
    twiml.append(redirect);
  }

  private void persistResponse(Response questionResponse) {
    Question currentQuestion = questionResponse.getQuestion();
    Response previousResponse = responseService.getBySessionSidAndQuestion(questionResponse.getSessionSid(), currentQuestion);
    if(previousResponse!=null){
      // it's already answered. That's an update from Twilio API (Transcriptions, for instance)
      questionResponse.setId(previousResponse.getId());
    }

    /** creates the question response on the db */
    responseService.create(questionResponse);
  }

  private boolean hasNextQuestion(List<Question> questions, Question currentQuestion) {
    int questionIndex = questions.indexOf(currentQuestion);
    return questionIndex < questions.size() - 1;
  }

  private void appendLastMessage(HttpServletRequest request, TwiMLResponse response, String lastMessage){
    boolean sms = request.getParameter("MessageSid")!=null;
    List<Verb> verbs = new LinkedList<Verb>();
    if(sms){
      verbs.add(new Message(lastMessage));
    }else{
      verbs.add(new Say(lastMessage));
      verbs.add(new Hangup());
    }
    
    try {
      for (Verb verb: verbs) {
        response.append(verb);
      }
    } catch (TwiMLException e) {
      System.out.println("Couldn't append redirect to Twilio's response");
    }
    
  }
}
