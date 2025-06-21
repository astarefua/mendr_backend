package com.telemed.service;

import com.telemed.model.SmartMedicationGuide;
import com.telemed.repository.SmartMedicationGuideRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SmartMedicationService {

    private final SmartMedicationGuideRepository medicationRepo;

    public SmartMedicationService(SmartMedicationGuideRepository medicationRepo) {
        this.medicationRepo = medicationRepo;
    }

    public List<SmartMedicationGuide> getAllGuides() {
        return medicationRepo.findAll();
    }

    public void addGuide(SmartMedicationGuide guide) {
        medicationRepo.save(guide);
    }
}
