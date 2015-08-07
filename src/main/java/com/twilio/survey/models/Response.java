package com.twilio.survey.models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "responses")
public class Response {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "response")
  private String response;

  @Column(name = "call_sid")
  private String callSid;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "question_id")
  private Question question;

  @Column(name = "date")
  private Date date;

  public Response() {}

  public Response(String response, String callSid, Question question, Date date) {
    this.response = response;
    this.callSid = callSid;
    this.question = question;
    this.date = date;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getResponse() {
    return response;
  }

  public void setResponse(String response) {
    this.response = response;
  }

  public String getCallSid() {
    return callSid;
  }

  public void setCallSid(String callSid) {
    this.callSid = callSid;
  }

  public Question getQuestion() {
    return question;
  }

  public void setQuestion(Question question) {
    this.question = question;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }
}
