package com.twilio.survey.controllers;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.twilio.survey.SurveyJavaApplication;
import com.twilio.survey.models.Question;
import com.twilio.survey.models.Survey;
import com.twilio.survey.repositories.QuestionRepository;
import com.twilio.survey.repositories.ResponseRepository;
import com.twilio.survey.repositories.SurveyRepository;
import com.twilio.survey.services.QuestionService;
import com.twilio.survey.services.ResponseService;
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
public class SurveyControllerTest {
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private SurveyRepository surveyRepository;
    @Autowired
    private ResponseRepository responseRepository;
    private QuestionService questionService;
    private SurveyService surveyService;
    private ResponseService responseService;

    @Value("${local.server.port}") int port;

    @After
    public void after() {
        responseService.deleteAll();
        questionService.deleteAll();
        surveyService.deleteAll();
    }

    @Before
    public void before() {
        questionService = new QuestionService(questionRepository);
        surveyService = new SurveyService(surveyRepository);
        responseService = new ResponseService(responseRepository);
        questionService.deleteAll();
        surveyService.deleteAll();
    }

    @Test
    public void getFirstSurvey() {
        assertThat(questionService.count(), is(0L));

        Survey survey = new Survey("New Title Survey", new Date());
        surveyService.create(survey);

        HttpResponse<String> stringResponse = null;
        try {
            stringResponse = Unirest.get("http://localhost:" + port + "/survey").asString();
        } catch (UnirestException e) {
            System.out.println("Unable to create request");
        }
        assertTrue(stringResponse.getBody().contains("New Title Survey"));
        assertTrue(stringResponse.getBody().contains("/question?survey=" + survey.getId()));
    }
}
