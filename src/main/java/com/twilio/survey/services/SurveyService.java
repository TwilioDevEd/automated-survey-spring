package com.twilio.survey.services;

import com.twilio.survey.models.Survey;
import com.twilio.survey.repositories.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class SurveyService {
    @Autowired
    private SurveyRepository surveyRepository;

    public Survey create(Survey survey) {
        return surveyRepository.save(survey);
    }
}
