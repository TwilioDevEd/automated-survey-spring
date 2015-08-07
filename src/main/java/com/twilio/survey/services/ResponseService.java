package com.twilio.survey.services;

import com.twilio.survey.models.Response;
import com.twilio.survey.repositories.ResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResponseService {
  private ResponseRepository responseRepository;

  @Autowired
  public ResponseService(ResponseRepository responseRepository) {
    this.responseRepository = responseRepository;
  }

  public void create(Response response) {
    responseRepository.save(response);
  }

  public void delete(Long id) {
    responseRepository.delete(id);
  }

  public void deleteAll() {
    responseRepository.deleteAll();
  }

  public Long count() {
    return responseRepository.count();
  }

  public List<Response> findAll() {
    return responseRepository.findAll();
  }

  public Response find(Long id) {
    return responseRepository.findOne(id);
  }
}
