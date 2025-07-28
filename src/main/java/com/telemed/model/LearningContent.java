package com.telemed.model;

import jakarta.persistence.*;

@Entity
public class LearningContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String type; // "video", "tip", "quiz"
    private String contentUrl; // URL or text
    private String symptomKeyword;
    
    // ✅ REQUIRED no-args constructor
    public LearningContent() {
    }
    
    public LearningContent(String title, String contentUrl, String symptomKeyword) {
        this.title = title;
        this.contentUrl = contentUrl;
        this.symptomKeyword = symptomKeyword;
    }


    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getContentUrl() { return contentUrl; }
    public void setContentUrl(String contentUrl) { this.contentUrl = contentUrl; }

    public String getSymptomKeyword() { return symptomKeyword; }
    public void setSymptomKeyword(String symptomKeyword) { this.symptomKeyword = symptomKeyword; }
}
