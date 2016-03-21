package com.twilio.survey.controllers;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.twilio.survey.SurveyJavaApplication;
import com.twilio.survey.models.Survey;
import com.twilio.survey.repositories.QuestionRepository;
import com.twilio.survey.repositories.SurveyRepository;
import com.twilio.survey.services.QuestionService;
import com.twilio.survey.services.SurveyService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SurveyJavaApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class QuestionControllerTest {
  @Value("${local.server.port}")
  int port;
  @Autowired
  private QuestionRepository questionRepository;
  @Autowired
  private SurveyRepository surveyRepository;
  private QuestionService questionService;
  private SurveyService surveyService;

  @After
  public void after() {
    questionService.deleteAll();
    surveyService.deleteAll();
  }

  @Before
  public void before() {
    questionService = new QuestionService(questionRepository);
    surveyService = new SurveyService(surveyRepository);
    questionService.deleteAll();
    surveyService.deleteAll();
  }

  @Test
  public void getNoQuestionTest() {
    Survey survey1 = new Survey("New Title Question", new Date());
    surveyService.create(survey1);

    HttpResponse<String> stringResponse = null;
    String requestPath =
        "http://localhost:" + port + "/question?survey=" + survey1.getId() + "&question=1";
    try {
      stringResponse = Unirest.get(requestPath).asString();
    } catch (UnirestException e) {
      System.out.println("Unable to create request");
    }
    assertTrue(stringResponse.getBody().contains(
        "We are sorry, there are no more questions available for this survey. Good bye."));
  }
}
