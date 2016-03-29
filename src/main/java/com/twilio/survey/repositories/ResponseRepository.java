package com.twilio.survey.repositories;

import com.twilio.survey.models.Question;
import com.twilio.survey.models.Response;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponseRepository extends JpaRepository<Response, Long> {
    Response getBySessionSidAndQuestion(String sessionSid, Question question);
}
