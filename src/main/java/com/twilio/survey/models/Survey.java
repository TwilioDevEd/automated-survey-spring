package com.twilio.survey.models;

import javax.persistence.*;
import java.util.Date;

@Entity @Table(name = "surveys") public class Survey {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id") private Long id;

    @Column(name = "title") private String title;

    @Column(name = "date") private Date date;

    public Survey() {

    }

    public Survey(String title, Date date) {
        this.title = title;
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
