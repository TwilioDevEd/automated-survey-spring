package com.twilio.survey.services;

import com.twilio.survey.SurveyJavaApplication;
import com.twilio.survey.models.Survey;
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
public class SurveyServiceTest {
    @Autowired
    private SurveyRepository surveyRepository;
    private SurveyService service;

    @Before
    public void before() {
        service = new SurveyService(surveyRepository);
        service.deleteAll();
    }

    @After
    public void after() {
        service.deleteAll();
    }

    @Test
    public void testCreate() {
        assertThat(service.count(), is(0L));

        Survey survey = new Survey("New Title", new Date());
        service.create(survey);

        assertThat(service.count(), is(1L));
    }

    @Test
    public void testDelete() {
        assertThat(service.count(), is(0L));

        Survey survey = new Survey("New Title", new Date());
        service.create(survey);

        assertThat(service.count(), is(1L));

        service.delete(survey.getId());

        assertThat(service.count(), is(0L));
    }

    @Test
    public void testFindAll() {
        assertThat(service.count(), is(0L));

        Survey survey1 = new Survey("New Title", new Date());
        service.create(survey1);
        Survey survey2 = new Survey("New Title 2", new Date());
        service.create(survey2);

        assertThat(service.findAll().size(), is(2));
    }

    @Test
    public void testCount() {
        assertThat(service.count(), is(0L));

        Survey survey1 = new Survey("New Title", new Date());
        service.create(survey1);
        Survey survey2 = new Survey("New Title 2", new Date());
        service.create(survey2);

        assertThat(service.count(), is(2L));
    }

    @Test
    public void testFind() {
        Survey survey = new Survey("Test Title", new Date());
        service.create(survey);

        assertThat(service.find(survey.getId()).getTitle(), is("Test Title"));
    }
}
