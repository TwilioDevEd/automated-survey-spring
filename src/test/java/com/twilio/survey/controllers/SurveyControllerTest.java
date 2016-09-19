package com.twilio.survey.controllers;

import com.twilio.survey.SurveyJavaApplication;
import com.twilio.survey.models.Survey;
import com.twilio.survey.repositories.SurveyRepository;
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
public class SurveyControllerTest extends BaseControllerTest {
    @Autowired
    private SurveyRepository surveyRepository;
    private SurveyService surveyService;

    @Before
    public void before() {
        surveyService = new SurveyService(surveyRepository);
        surveyService.deleteAll();
    }

    @Test
    public void getFirstSurveyOnCall() throws Exception {
        Survey survey = createSurvey();

        String response = getAsCall("/survey/call");

        assertThat(response, CoreMatchers.containsString("New Title Survey"));
        assertThat(response, CoreMatchers.containsString("/question?survey=" + survey.getId()));
    }

    @Test
    public void getFirstSurveyOnSMS() throws Exception {
        Survey survey = createSurvey();

        String response = getAsSMS("/survey/sms");

        assertThat(response, CoreMatchers.containsString("<Message><Body>Welcome to the New Title Survey " +
                "survey</Body></Message>"));
        assertThat(response, CoreMatchers.containsString("/question?survey=" + survey.getId()));
    }

    @Test
    public void getHangupOnNoSurveyCall() throws Exception {
        String response = getAsCall("/survey/call");

        assertThat(response, CoreMatchers.containsString("We are sorry, there are no surveys available. Good bye."));
    }

    private Survey createSurvey() {
        return surveyService.create(new Survey("New Title Survey", new Date()));
    }
}
