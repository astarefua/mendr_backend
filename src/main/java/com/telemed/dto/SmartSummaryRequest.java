package com.telemed.dto;

public class SmartSummaryRequest {
    private String symptom;
    private String bodyPart;
    private String severity;
    private String action;
    private String followUp;
    private String extraNotes;

    // Getters and setters
    public String getSymptom() { return symptom; }
    public void setSymptom(String symptom) { this.symptom = symptom; }

    public String getBodyPart() { return bodyPart; }
    public void setBodyPart(String bodyPart) { this.bodyPart = bodyPart; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getFollowUp() { return followUp; }
    public void setFollowUp(String followUp) { this.followUp = followUp; }

    public String getExtraNotes() { return extraNotes; }
    public void setExtraNotes(String extraNotes) { this.extraNotes = extraNotes; }
}
