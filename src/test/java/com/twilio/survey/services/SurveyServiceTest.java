package com.twilio.survey.services;

import com.twilio.survey.SurveyJavaApplication;
import com.twilio.survey.models.Survey;
import com.twilio.survey.repositories.SurveyRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Date;

import static org.junit.Assert.assertTrue;

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
  }

  @Test
  public void testCreate() {
    Survey survey = new Survey("New Title", new Date());
    service.create(survey);

    assertTrue(true);
  }
}
