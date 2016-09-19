package com.twilio.survey.controllers;

import com.twilio.survey.SurveyJavaApplication;
import com.twilio.survey.models.Question;
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
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SurveyJavaApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class ResponseControllerTest extends BaseControllerTest {
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
        responseService.deleteAll();
        questionService.deleteAll();
        surveyService.deleteAll();
    }

    @Test
    public void thankYouAndHangupCallOnLastAnswerTest() throws Exception {
        Question question = createQuestion();

        String requestPath = "/save_response?qid=" + question.getId();

        Map<String, Object> params = new HashMap<>();
        params.put("RecordingUrl", "http://recording_url.com");
        String response = postCallWithParameters(requestPath, params);

        assertThat(response, CoreMatchers.containsString("<Say>Tank you for taking the"));
        assertThat(response, CoreMatchers.containsString("Hangup"));
    }

    @Test
    public void thankYouSMSOnLastAnswerTest() throws Exception {
        Question question = createQuestion();

        String requestPath = "/save_response?qid=" + question.getId();

        Map<String, Object> params = new HashMap<>();
        params.put("Body", "Last answer");
        String response = postSMSWithParameters(requestPath, params);

        assertThat(response, CoreMatchers.containsString("<Message><Body>Tank you for taking the"));
    }

    @Test
    public void saveTextWhenTranscriptionSucceedTest() throws Exception {
        Question question = createQuestion();

        String requestPath = "/save_response?qid=" + question.getId();

        Map<String, Object> params = new HashMap<>();
        params.put("RecordingUrl", "http://recording_url.com");
        postCallWithParameters(requestPath, params);

        // Transcription callback is called with TranscriptionText
        params.put("TranscriptionText", "The Answer is 42");
        String response = postCallWithParameters(requestPath, params);

        assertThat(responseService.count(), is(1L));
        assertThat(responseService.findAll().get(0).getResponse(), is("The Answer is 42"));
    }

    @Test
    public void saveRecordWhenTranscriptionFailsTest() throws Exception {
        Question question = createQuestion();

        String requestPath = "/save_response?qid=" + question.getId();

        Map<String, Object> params = new HashMap<>();
        params.put("RecordingUrl", "http://recording_url.com");
        postCallWithParameters(requestPath, params);

        // Transcription callback is called with a failure
        params.put("TranscriptionStatus", "failed");
        String response = postCallWithParameters(requestPath, params);

        assertThat(responseService.count(), is(1L));
        assertThat(responseService.findAll().get(0).getResponse(), is("http://recording_url.com"));
    }

    private Question createQuestion() {
        Survey survey = surveyService.create(new Survey("New Title Survey", new Date()));
        return questionService.save(new Question("Question Body", "text", survey, new Date()));
    }
}
