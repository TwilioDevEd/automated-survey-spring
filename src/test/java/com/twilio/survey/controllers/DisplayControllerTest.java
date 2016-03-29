package com.twilio.survey.controllers;

import com.twilio.survey.SurveyJavaApplication;
import com.twilio.survey.models.Question;
import com.twilio.survey.models.Response;
import com.twilio.survey.models.Survey;
import com.twilio.survey.repositories.QuestionRepository;
import com.twilio.survey.repositories.ResponseRepository;
import com.twilio.survey.repositories.SurveyRepository;
import com.twilio.survey.services.QuestionService;
import com.twilio.survey.services.ResponseService;
import com.twilio.survey.services.SurveyService;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Date;

import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SurveyJavaApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class DisplayControllerTest extends BaseControllerTest {
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private SurveyRepository surveyRepository;
    @Autowired
    private ResponseRepository responseRepository;
    private QuestionService questionService;
    private SurveyService surveyService;
    private ResponseService responseService;

    @Before
    public void before() {
        questionService = new QuestionService(questionRepository);
        surveyService = new SurveyService(surveyRepository);
        responseService = new ResponseService(responseRepository);
        questionService.deleteAll();
        surveyService.deleteAll();
        responseService.deleteAll();
    }

    @Test
    public void showQuestions() throws Exception {
        Survey survey = surveyService.create(new Survey("New Title Survey", new Date()));
        questionService.save(new Question("Question Body", "Q_TYPE", survey, new Date()));

        String httpResponse = get("/");

        assertThat(httpResponse, CoreMatchers.containsString("New Title Survey"));
        assertThat(httpResponse, CoreMatchers.containsString("Question Body"));
    }

    @Test
    public void showResponses() throws Exception {
        Survey survey = surveyService.create(new Survey("New Title Survey", new Date()));
        Question question = questionService.save(new Question("Numeric Question", "numeric", survey, new Date()));
        responseService.save(new Response("test number", "SESSION_SID", question, new Date()));

        String httpResponse = get("/");

        assertThat(httpResponse, CoreMatchers.containsString("Response: test number"));
    }

    @Test
    public void showTextResponses() throws Exception {
        Survey survey = surveyService.create(new Survey("New Title Survey", new Date()));
        Question question = questionService.save(new Question("Text Question", "text", survey, new Date()));
        responseService.save(new Response("http://recording_url", "SESSION_SID", question, new Date()));

        String httpResponse = get("/");

        assertThat(httpResponse, CoreMatchers.containsString("http://recording_url"));
    }

    @Test
    public void showYesNoResponses() throws Exception {
        Survey survey = surveyService.create(new Survey("New Title Survey", new Date()));
        Question question = questionService.save(new Question("YesNo Question", "yes-no", survey, new Date()));
        responseService.save(new Response("0", "SESSION_SID", question, new Date()));

        String httpResponse = get("/");

        assertThat(httpResponse, CoreMatchers.containsString("(1: YES, 0: NO) Response: 0"));
    }
}
