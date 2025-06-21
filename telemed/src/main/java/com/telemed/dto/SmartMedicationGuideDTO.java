package com.telemed.dto;

import java.time.LocalDate;

import com.telemed.model.Patient;

public class SmartMedicationGuideDTO {
	
	// DTO to accept POST body
	
	    public Long patientId;
	    public String medicationName;
	    public String visualDescription;
	    public String imageUrl;
	    public String usageInstruction;
	    public String animationUrl;
	    public int dosesPerDay;
	    public int totalDays;
	    public LocalDate startDate;
	
	    public Long getPatientId() {
	        return patientId;
	    }

	    public void setPatientId(Long patientId) {
	        this.patientId = patientId;
	    }

    public String getMedicationName() { return medicationName; }
    public void setMedicationName(String medicationName) { this.medicationName = medicationName; }

    public String getVisualDescription() { return visualDescription; }
    public void setVisualDescription(String visualDescription) { this.visualDescription = visualDescription; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getUsageInstruction() { return usageInstruction; }
    public void setUsageInstruction(String usageInstruction) { this.usageInstruction = usageInstruction; }

    public String getAnimationUrl() { return animationUrl; }
    public void setAnimationUrl(String animationUrl) { this.animationUrl = animationUrl; }

    public int getDosesPerDay() { return dosesPerDay; }
    public void setDosesPerDay(int dosesPerDay) { this.dosesPerDay = dosesPerDay; }

    public int getTotalDays() { return totalDays; }
    public void setTotalDays(int totalDays) { this.totalDays = totalDays; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }



}
