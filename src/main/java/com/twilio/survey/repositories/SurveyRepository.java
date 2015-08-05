package com.twilio.survey.repositories;

import com.twilio.survey.models.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository public interface SurveyRepository extends JpaRepository<Survey, Long> {
    List<Survey> findByStatus(Long id);
}
