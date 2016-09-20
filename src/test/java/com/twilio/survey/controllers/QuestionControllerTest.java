package com.twilio.survey.controllers;

import com.twilio.survey.SurveyJavaApplication;
import com.twilio.survey.models.Question;
import com.twilio.survey.models.Survey;
import com.twilio.survey.repositories.QuestionRepository;
import com.twilio.survey.repositories.SurveyRepository;
import com.twilio.survey.services.QuestionService;
import com.twilio.survey.services.SurveyService;
import org.hamcrest.CoreMatchers;
import org.jdom2.Element;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Date;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SurveyJavaApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class QuestionControllerTest extends BaseControllerTest {
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private SurveyRepository surveyRepository;
    private QuestionService questionService;
    private SurveyService surveyService;

    @Before
    public void before() {
        questionService = new QuestionService(questionRepository);
        surveyService = new SurveyService(surveyRepository);
        questionService.deleteAll();
        surveyService.deleteAll();
    }

    @Test
    public void getNoQuestionCallTest() throws Exception {
        Survey survey1 = surveyService.create(new Survey("New Title", new Date()));

        String response = getAsCall("/question?survey=" + survey1.getId() + "&question=1");

        assertThat(response, containsString(
                "<Say>We are sorry, there are no more questions available for this survey. Good bye.</Say>"));
    }

    @Test
    public void getNoQuestionSMSTest() throws Exception {
        Survey survey1 = surveyService.create(new Survey("New Title", new Date()));

        String response = getAsSMS("/question?survey=" + survey1.getId() + "&question=1");

        assertThat(response, containsString(
                "<Message><Body>We are sorry, there are no more questions available for this survey. Good bye" +
                        ".</Body></Message>"));
    }

    @Test
    public void getSMSQuestionTest() throws Exception {
        Survey survey1 = surveyService.create(new Survey("New Title", new Date()));
        questionService.save(new Question("Question Body", "Q_TYPE", survey1, new Date()));

        String response = getAsSMS("/question?survey=" + survey1.getId() + "&question=1");

        assertThat(response, containsString(
                "<Message><Body>Question Body</Body></Message>"));
    }

    @Test
    public void getTextQuestionOnACallWillRecordTest() throws Exception {
        Survey survey1 = surveyService.create(new Survey("Curious Survey", new Date()));
        Question question = questionService.save(new Question("Who are you?", "text", survey1, new Date()));

        String response = getAsCall("/question?survey=" + survey1.getId() + "&question=1");
        Element recordVerb = getElement(getDocument(response), "Record");
        String actionURL = "/save_response?qid=" + question.getId();

        assertThat(recordVerb, is(CoreMatchers.<Element>notNullValue()));
        assertThat(recordVerb.getAttribute("action"), is(CoreMatchers.<Element>notNullValue()));
        assertThat(recordVerb.getAttribute("action").getValue(), containsString(actionURL));
        assertThat(recordVerb.getAttribute("method"), is(CoreMatchers.<Element>notNullValue()));
        assertThat(recordVerb.getAttribute("method").getValue(), containsString("POST"));
    }

    @Test
    public void getYesNoQuestionSMSTest() throws Exception {
        Survey survey1 = surveyService.create(new Survey("New Title", new Date()));
        Question question = questionService.save(new Question("Question Body", "yes-no", survey1, new Date()));

        String response = getAsSMS("/question?survey=" + survey1.getId() + "&question=1");

        assertThat(response, containsString(
                "<Message><Body>For the next question, type 1 for yes, and 0 for no. " + question.getBody() +
                        "</Body></Message>"));
    }

    @Test
    public void getYesNoQuestionCallTest() throws Exception {
        Survey survey1 = surveyService.create(new Survey("New Title", new Date()));
        Question question = questionService.save(new Question("Question Body", "yes-no", survey1, new Date()));

        String response = getAsCall("/question?survey=" + survey1.getId() + "&question=1");

        assertThat(response, containsString(
                "<Say>For the next question, press 1 for yes, and 0 for no. Then press the pound key.</Say>"));
        assertThat(response, containsString(
                "<Say>" + question.getBody() + "</Say>"));
        assertThat(response, containsString(
                "<Gather action=\"/save_response?qid=" + question.getId()));
    }
}