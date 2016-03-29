package com.twilio.survey.services;

import com.twilio.survey.models.Question;
import com.twilio.survey.models.Survey;
import com.twilio.survey.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {
    private QuestionRepository questionRepository;

    @Autowired
    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public Question save(Question question) {
        questionRepository.save(question);
        return question;
    }

    public void delete(Long id) {
        questionRepository.delete(id);
    }

    public void deleteAll() {
        questionRepository.deleteAll();
    }

    public Long count() {
        return questionRepository.count();
    }

    public List<Question> findAll() {
        return questionRepository.findAll();
    }

    public Question find(Long id) {
        return questionRepository.findOne(id);
    }

    public Question findFirst(Survey survey) throws IndexOutOfBoundsException {
        return questionRepository.findAll(new Sort(Sort.Direction.ASC, "id")).get(0);
    }
}
