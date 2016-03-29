package com.twilio.survey.services;

import com.twilio.survey.SurveyJavaApplication;
import com.twilio.survey.models.Question;
import com.twilio.survey.models.Response;
import com.twilio.survey.models.Survey;
import com.twilio.survey.repositories.QuestionRepository;
import com.twilio.survey.repositories.ResponseRepository;
import com.twilio.survey.repositories.SurveyRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SurveyJavaApplication.class)
@WebAppConfiguration
public class ResponseServiceTest {
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private SurveyRepository surveyRepository;
    @Autowired
    private ResponseRepository responseRepository;
    private QuestionService questionService;
    private SurveyService surveyService;
    private ResponseService responseService;

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
        responseService.deleteAll();
        questionService.deleteAll();
        surveyService.deleteAll();
    }

    @Test
    public void testCreate() {
        assertThat(responseService.count(), is(0L));

        Survey survey = new Survey("New Title Question", new Date());
        surveyService.create(survey);
        Question question = new Question("Question Body", "Q_TYPE", survey, new Date());
        questionService.save(question);
        Response response =
                new Response("This responds the question", "CALL_SID", question, new Date());
        responseService.save(response);

        assertThat(questionService.count(), is(1L));
    }

    @Test
    public void testDelete() {
        assertThat(questionService.count(), is(0L));

        Survey survey = new Survey("New Title Question", new Date());
        surveyService.create(survey);
        Question question = new Question("Question Body", "Q_TYPE", survey, new Date());
        questionService.save(question);
        Response response =
                new Response("This responds the question", "CALL_SID", question, new Date());
        responseService.save(response);

        assertThat(responseService.count(), is(1L));

        responseService.delete(response.getId());

        assertThat(responseService.count(), is(0L));
    }

    @Test
    public void testFindAll() {
        assertThat(questionService.count(), is(0L));

        Survey survey1 = new Survey("New Title Question", new Date());
        surveyService.create(survey1);
        Question question1 = new Question("Question Body", "Q_TYPE", survey1, new Date());
        questionService.save(question1);
        Response response1 =
                new Response("This responds the question", "CALL_SID", question1, new Date());
        responseService.save(response1);
        Survey survey2 = new Survey("New Title Question2", new Date());
        surveyService.create(survey2);
        Question question2 = new Question("Question Body2", "Q_TYPE", survey1, new Date());
        questionService.save(question2);
        Response response2 =
                new Response("This responds the question", "CALL_SID", question2, new Date());
        responseService.save(response2);

        assertThat(responseService.findAll().size(), is(2));
    }

    @Test
    public void testCount() {
        assertThat(questionService.count(), is(0L));

        Survey survey1 = new Survey("New Title Question", new Date());
        surveyService.create(survey1);
        Question question1 = new Question("Question Body", "Q_TYPE", survey1, new Date());
        questionService.save(question1);
        Response response1 =
                new Response("This responds the question", "CALL_SID", question1, new Date());
        responseService.save(response1);
        Survey survey2 = new Survey("New Title Question2", new Date());
        surveyService.create(survey2);
        Question question2 = new Question("Question Body2", "Q_TYPE", survey1, new Date());
        questionService.save(question2);
        Response response2 =
                new Response("This responds the question", "CALL_SID", question2, new Date());
        responseService.save(response2);

        assertThat(responseService.count(), is(2L));
    }

    @Test
    public void testFind() {
        Survey survey = new Survey("New Title Question", new Date());
        surveyService.create(survey);
        Question question = new Question("Question Body", "Q_TYPE", survey, new Date());
        questionService.save(question);
        Response response =
                new Response("This responds the question", "CALL_SID", question, new Date());
        responseService.save(response);

        assertThat(responseService.find(response.getId()).getResponse(),
                is("This responds the question"));
    }
}
