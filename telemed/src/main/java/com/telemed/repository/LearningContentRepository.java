package com.telemed.repository;

import com.telemed.model.LearningContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LearningContentRepository extends JpaRepository<LearningContent, Long> {
    List<LearningContent> findBySymptomKeywordIgnoreCase(String symptomKeyword);
}
