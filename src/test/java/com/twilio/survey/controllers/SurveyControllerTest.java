package com.twilio.survey.controllers;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.twilio.survey.SurveyJavaApplication;
import com.twilio.survey.models.Survey;
import com.twilio.survey.repositories.SurveyRepository;
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
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SurveyJavaApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class SurveyControllerTest {
  @Value("${local.server.port}")
  int port;
  @Autowired
  private SurveyRepository surveyRepository;
  private SurveyService surveyService;

  @After
  public void after() {
    surveyService.deleteAll();
  }

  @Before
  public void before() {
    surveyService = new SurveyService(surveyRepository);
    surveyService.deleteAll();
  }

  @Test
  public void getFirstSurveyOnCall() {
    Survey survey = new Survey("New Title Survey", new Date());
    surveyService.create(survey);

    HttpResponse<String> stringResponse = null;
    try {
      stringResponse = Unirest.get("http://localhost:" + port + "/survey/call").asString();
    } catch (UnirestException e) {
      System.out.println("Unable to create request");
    }
    assertTrue(stringResponse.getBody().contains("New Title Survey"));
    assertTrue(stringResponse.getBody().contains("/question?survey=" + survey.getId()));
  }

  @Test
  public void getFirstSurveyOnSMS() {
    Survey survey = new Survey("New Title Survey", new Date());
    surveyService.create(survey);

    HttpResponse<String> stringResponse = null;
    Map<String, Object> params = new HashMap<>();
    params.put("MessageSid", "SMS225345");
    try {
      stringResponse = Unirest.get("http://localhost:" + port + "/survey/sms").queryString(params).asString();
    } catch (UnirestException e) {
      System.out.println("Unable to create request");
    }
    System.err.println(stringResponse.getBody());
    assertTrue(stringResponse.getBody().contains("<Message>Welcome to the New Title Survey survey</Message>"));
    assertTrue(stringResponse.getBody().contains("/question?survey=" + survey.getId()));
  }

  @Test
  public void getHangupOnNoSurveyCall() {
    HttpResponse<String> stringResponse = null;
    try {
      stringResponse = Unirest.get("http://localhost:" + port + "/survey/call").asString();
    } catch (UnirestException e) {
      System.out.println("Unable to create request");
    }
    assertTrue(stringResponse.getBody()
        .contains("We are sorry, there are no surveys available. Good bye."));
  }
}
