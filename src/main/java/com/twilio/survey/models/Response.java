package com.twilio.survey.models;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "question_id")
  private Question question;

  @Column(name = "date")
  private Date date;

  @Column(name = "isVoice")
  private boolean isVoice;

  @Column(name = "isNumeric")
  private boolean isNumeric;

  @Column(name = "isYesNo")
  private boolean isYesNo;

  public Response() {}

  public Response(String response, String callSid, Question question, Date date) {
    this.response = response;
    this.callSid = callSid;
    this.question = question;
    this.date = date;
    this.isVoice = false;
    this.isNumeric = false;
    this.isYesNo = false;
    if (question.getType().compareTo("voice") == 0) {
      this.isVoice = true;
    }
    else if(question.getType().compareTo("numeric") == 0) {
      this.isNumeric = true;
    }
    else if(question.getType().compareTo("yes-no") == 0) {
      this.isYesNo = true;
    }
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

  public boolean isVoice() {
    return isVoice;
  }

  public void setIsVoice(boolean isVoice) {
    this.isVoice = isVoice;
  }

  public boolean isNumeric() {
    return isNumeric;
  }

  public void setIsNumeric(boolean isNumeric) {
    this.isNumeric = isNumeric;
  }

  public boolean isYesNo() {
    return isYesNo;
  }

  public void setIsYesNo(boolean isYesNo) {
    this.isYesNo = isYesNo;
  }
}
