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

    @Column(name = "session_sid")
    private String sessionSid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(name = "date")
    private Date date;

    /**
     * The next three field are necessary for Mustache templating engine
     */
    @Column(name = "isText")
    private boolean isText;

    @Column(name = "isNumeric")
    private boolean isNumeric;

    @Column(name = "isYesNo")
    private boolean isYesNo;

    public Response() {
    }

    public Response(String response, String sessionSid, Question question, Date date) {
        this.response = response;
        this.sessionSid = sessionSid;
        this.question = question;
        this.date = date;
        this.isText = false;
        this.isNumeric = false;
        this.isYesNo = false;
        if (question.getType().compareTo("text") == 0) {
            this.isText = true;
        } else if (question.getType().compareTo("numeric") == 0) {
            this.isNumeric = true;
        } else if (question.getType().compareTo("yes-no") == 0) {
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

    public String getSessionSid() {
        return sessionSid;
    }

    public void setSessionSid(String sessionSid) {
        this.sessionSid = sessionSid;
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

    public boolean isText() {
        return isText;
    }

    public void setIsText(boolean isText) {
        this.isText = isText;
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

    public boolean isRecording() {
        return this.getResponse().contains("/Recordings/") && this.getResponse().startsWith("https");
    }
}
