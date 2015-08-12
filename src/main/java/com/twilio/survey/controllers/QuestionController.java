package com.twilio.survey.controllers;

import com.twilio.sdk.verbs.Hangup;
import com.twilio.sdk.verbs.TwiMLException;
import com.twilio.sdk.verbs.TwiMLResponse;
import com.twilio.survey.models.Question;
import com.twilio.survey.models.Survey;
import com.twilio.survey.repositories.SurveyRepository;
import com.twilio.survey.services.SurveyService;
import com.twilio.survey.util.QuestionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
public class QuestionController {
  @Autowired
  private SurveyRepository surveyRepository;
  private SurveyService surveyService;

  public QuestionController() {}

  @RequestMapping(value = "/question", method = RequestMethod.GET)
  public void readQuestion(HttpServletRequest request, HttpServletResponse response) {
    this.surveyService = new SurveyService(surveyRepository);

    Long surveyId = null;
    int questionNumber = 0;
    System.out.println("*********QUESTION***********"+request.getParameter("survey"));
    System.out.println("********************"+request.getParameter("q"));
    System.out.println(request.getQueryString());

    try {
      surveyId = Long.parseLong(request.getParameter("survey"));
      questionNumber = Integer.parseInt(request.getParameter("q"));
      System.out.println("*****************" + questionNumber);
    } catch (NumberFormatException e) {
      System.out.println("Numbers wrongly formatted, unable to parse");
    }

    Survey survey = surveyService.find(surveyId);
    List<Question> questions = survey.getQuestions();

    if (questionNumber <= questions.size()) {
      Question currentQuestion = questions.get(questionNumber - 1);
      QuestionHandler questionHandler = new QuestionHandler(currentQuestion);
      TwiMLResponse twiml = questionHandler.getTwilioResponse();

      response.setContentType("application/xml");
      try {
        response.getWriter().print(twiml.toEscapedXML());
      } catch (IOException e) {
        System.out.println("Couldn't write Twilio's response to XML");
      }
    }
    else {
      TwiMLResponse twiml = new TwiMLResponse();
      Hangup hangup = new Hangup();
      try {
        twiml.append(hangup);
      } catch (TwiMLException e) {
        System.out.println("Couldn't append verb to Twilio's response");
      }
      response.setContentType("application/xml");
      try {
        response.getWriter().print(twiml.toEscapedXML());
      } catch (IOException e) {
        System.out.println("Couldn't write Twilio's response to XML");
      }
    }
  }

  //  @RequestMapping("/")
  //  public String welcome(Map<String, Object> model) {
  //    model.put("time", new Date());
  //    model.put("message", "hola hola");
  //    return "welcome";
  //  }
}
