package com.telemed.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.util.List;

//import com.itextpdf.text.List;
import com.telemed.model.Appointment;
import com.telemed.model.ConsultationSummary;
import com.telemed.model.Doctor;
import com.telemed.model.LearningContent;
import com.telemed.model.MedicationAdherence;
import com.telemed.model.Patient;
import com.telemed.model.PreConsultSymptom;
import com.telemed.model.SmartMedicationGuide;
import com.telemed.model.VideoSession;
import com.telemed.repository.AppointmentRepository;
import com.telemed.repository.ConsultationSummaryRepository;
import com.telemed.repository.DoctorRepository;
import com.telemed.repository.LearningContentRepository;
import com.telemed.repository.MedicationAdherenceRepository;
import com.telemed.repository.PatientRepository;
import com.telemed.repository.PreConsultSymptomRepository;
import com.telemed.repository.SmartMedicationGuideRepository;
import com.telemed.repository.VideoSessionRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VideoService {

	@Value("${daily.api.key}")
	private String apiKey;

	@Value("${openai.api.key}")
	private String openaiKey;

	private final RestTemplate restTemplate = new RestTemplate();
	private final String DAILY_BASE_URL = "https://api.daily.co/v1";
	private final String OPENAI_CHAT_URL = "https://openrouter.ai/api/v1/chat/completions";

	// private final String OPENAI_CHAT_URL =
	// "https://api.openai.com/v1/chat/completions";

	private final VideoSessionRepository sessionRepo;
	private final DoctorRepository doctorRepo;
	private final PatientRepository patientRepo;
	private final LearningContentRepository learningRepo;
	private final PreConsultSymptomRepository preConsultRepo;
	private final SmartMedicationGuideRepository medicationRepo;
	private final MedicationAdherenceRepository adherenceRepo;
	private final AppointmentRepository appointmentRepo;
	private final ConsultationSummaryRepository summaryRepo;

	public VideoService(VideoSessionRepository sessionRepo, DoctorRepository doctorRepo, PatientRepository patientRepo,
			LearningContentRepository learningRepo, PreConsultSymptomRepository preConsultRepo,
			SmartMedicationGuideRepository medicationRepo, MedicationAdherenceRepository adherenceRepo,
			AppointmentRepository appointmentRepo, ConsultationSummaryRepository summaryRepo) {
		this.sessionRepo = sessionRepo;
		this.doctorRepo = doctorRepo;
		this.patientRepo = patientRepo;
		this.learningRepo = learningRepo;
		this.preConsultRepo = preConsultRepo;
		this.medicationRepo = medicationRepo;
		this.adherenceRepo = adherenceRepo;
		this.appointmentRepo = appointmentRepo;
		this.summaryRepo = summaryRepo;
	}

	public List<SmartMedicationGuide> getMedicationGuides(String email) {
		Patient patient = patientRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("Patient not found"));
		return medicationRepo.findByPatient(patient);
	}

	public void confirmMedicationTaken(Long guideId, String email) {
		Patient patient = patientRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("Patient not found"));

		SmartMedicationGuide guide = medicationRepo.findById(guideId)
				.orElseThrow(() -> new RuntimeException("Guide not found"));

		if (!guide.getPatient().getId().equals(patient.getId())) {
			throw new RuntimeException("Unauthorized: This medication is not assigned to you.");
		}

		LocalDate today = LocalDate.now();
		if (today.isBefore(guide.getStartDate())) {
			throw new RuntimeException("This medication starts on " + guide.getStartDate()
					+ ". You cannot confirm a dose before this date.");
		}

		List<MedicationAdherence> recent = adherenceRepo.findByPatientAndGuideOrderByTakenAtDesc(patient, guide);

		int dosesPerDay = Math.max(1, guide.getDosesPerDay());
		int windowSizeMinutes = 1440 / dosesPerDay;

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime midnight = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);
		int currentWindow = (int) ChronoUnit.MINUTES.between(midnight, now) / windowSizeMinutes;

		boolean alreadyTaken = recent.stream().anyMatch(entry -> {
			int entryWindow = (int) ChronoUnit.MINUTES.between(midnight, entry.getTakenAt()) / windowSizeMinutes;
			return entry.getTakenAt().toLocalDate().equals(LocalDate.now()) && entryWindow == currentWindow;
		});

		if (alreadyTaken) {
			throw new RuntimeException("You’ve already confirmed your dose for this time slot.");
		}

		MedicationAdherence adherence = new MedicationAdherence();
		adherence.setGuide(guide);
		adherence.setPatient(patient);
		adherence.setTakenAt(LocalDateTime.now());
		adherenceRepo.save(adherence);
	}

	public List<MedicationAdherence> getAdherenceHistory(String email) {
		Patient patient = patientRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("Patient not found"));
		return adherenceRepo.findByPatient(patient);
	}

	public Map<String, Object> getAdherenceProgress(Long guideId, String email) {
		Patient patient = patientRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("Patient not found"));
		SmartMedicationGuide guide = medicationRepo.findById(guideId)
				.orElseThrow(() -> new RuntimeException("Guide not found"));

		if (!guide.getPatient().getId().equals(patient.getId())) {
			throw new RuntimeException("Unauthorized: This medication is not assigned to you.");
		}

		int totalDays = guide.getTotalDays();
		int dosesPerDay = guide.getDosesPerDay();
		LocalDate startDate = guide.getStartDate();

		int expectedDoses = (int) ChronoUnit.DAYS.between(startDate, LocalDate.now().plusDays(1)) * dosesPerDay;
		int takenDoses = adherenceRepo.findByPatientAndGuide(patient, guide).size();
		double progress = expectedDoses > 0 ? (takenDoses * 100.0 / expectedDoses) : 0.0;

		Map<String, Object> map = new HashMap<>();
		map.put("expectedDoses", expectedDoses);
		map.put("takenDoses", takenDoses);
		map.put("progressPercentage", progress);
		map.put("remainingDoses", Math.max(0, (totalDays * dosesPerDay) - takenDoses));
		map.put("isCompleted", takenDoses >= (totalDays * dosesPerDay));
		return map;
	}

	public List<SmartMedicationGuide> getTodayDueDoses(String email) {
		Patient patient = patientRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("Patient not found"));
		LocalDate today = LocalDate.now();

		return medicationRepo.findByPatient(patient).stream()
				.filter(guide -> guide.getStartDate() != null && !guide.getStartDate().isAfter(today)
						&& ChronoUnit.DAYS.between(guide.getStartDate(), today) < guide.getTotalDays())
				.collect(Collectors.toList());
	}

	public Map<LocalDate, List<String>> getDoseCalendar(String email) {
		Patient patient = patientRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("Patient not found"));
		Map<LocalDate, List<String>> calendar = new HashMap<>();

		for (SmartMedicationGuide guide : medicationRepo.findByPatient(patient)) {
			LocalDate date = guide.getStartDate();
			for (int i = 0; i < guide.getTotalDays(); i++) {
				LocalDate doseDay = date.plusDays(i);
				calendar.computeIfAbsent(doseDay, d -> new ArrayList<>()).add(guide.getMedicationName());
			}
		}
		return calendar;
	}

	public String fetchPillImageFromWikimedia(String drugName) {
		try {
			// Step 1: Use Wikimedia OpenSearch to get image title
			String searchUrl = "https://en.wikipedia.org/w/api.php?action=opensearch&search="
					+ drugName.replace(" ", "%20") + "%20pill&limit=1&namespace=6&format=json";

			ResponseEntity<List> searchResponse = restTemplate.getForEntity(searchUrl, List.class);
			List responseList = searchResponse.getBody();

			if (responseList != null && responseList.size() >= 4) {
				List<String> imageUrls = (List<String>) responseList.get(3);
				if (!imageUrls.isEmpty()) {
					return imageUrls.get(0); // First image URL
				}
			}

			// Fallback: Try generic drug name search (less specific)
			searchUrl = "https://en.wikipedia.org/w/api.php?action=opensearch&search=" + drugName.replace(" ", "%20")
					+ "&limit=1&namespace=6&format=json";
			ResponseEntity<List> fallbackResponse = restTemplate.getForEntity(searchUrl, List.class);
			List fallbackList = fallbackResponse.getBody();

			if (fallbackList != null && fallbackList.size() >= 4) {
				List<String> fallbackImageUrls = (List<String>) fallbackList.get(3);
				if (!fallbackImageUrls.isEmpty()) {
					return fallbackImageUrls.get(0);
				}
			}

		} catch (Exception e) {
			return "Error fetching Wikimedia image: " + e.getMessage();
		}

		return "No Wikimedia image found for: " + drugName;
	}

	public String createRoom(String roomName, String doctorEmail, String patientEmail) {
		String url = DAILY_BASE_URL + "/rooms";

		Map<String, Object> body = new HashMap<>();
		body.put("name", roomName);
		body.put("privacy", "private");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(apiKey);

		HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
		ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

		if (response.getStatusCode().is2xxSuccessful()) {
			String roomUrl = (String) response.getBody().get("url");

			// Save to DB
			Doctor doctor = doctorRepo.findByEmail(doctorEmail).orElse(null);
			Patient patient = patientRepo.findByEmail(patientEmail).orElse(null);

			VideoSession session = new VideoSession();
			session.setRoomName(roomName);
			session.setRoomUrl(roomUrl);
			session.setDoctor(doctor);
			session.setPatient(patient);
			session.setCreatedAt(LocalDateTime.now());
			sessionRepo.save(session);

			return roomUrl;
		} else {
			throw new RuntimeException("Failed to create room: " + response.getStatusCode());
		}
	}

	public String generateToken(String roomName, String userName, String userType) {
		String url = DAILY_BASE_URL + "/meeting-tokens";

		Map<String, Object> payload = new HashMap<>();
		payload.put("properties",
				Map.of("room_name", roomName, "user_name", userName, "is_owner", userType.equals("doctor")));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(apiKey);

		HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
		ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

		if (response.getStatusCode().is2xxSuccessful()) {
			return (String) response.getBody().get("token");
		} else {
			throw new RuntimeException("Failed to generate token: " + response.getStatusCode());
		}
	}

	// ✅ Smart Note Summary Auto-EHR (to be wired into appointment summaries)
	public String generateSummaryFromSmartNote(String symptom, String bodyPart, String severity, String action,
			String followUp, String extraNotes) {
		StringBuilder summary = new StringBuilder();
		summary.append("Observation: ").append(symptom).append(" on ").append(bodyPart).append(". ");
		summary.append("Severity: ").append(severity).append(". ");
		summary.append("Action Taken: ").append(action).append(". ");
		summary.append("Follow-up: ").append(followUp).append(". ");
		if (extraNotes != null && !extraNotes.trim().isEmpty()) {
			summary.append("Additional Notes: ").append(extraNotes).append(".");
		}
		return summary.toString();
	}

	// ✅ Save summary to ConsultationSummary
	public void saveConsultationSummary(Long appointmentId, String generatedSummary) {
		Appointment appointment = appointmentRepo.findById(appointmentId)
				.orElseThrow(() -> new RuntimeException("Appointment not found"));

		ConsultationSummary summary = new ConsultationSummary();
		summary.setAppointment(appointment);
		summary.setSummary(generatedSummary);
		summary.setCreatedAt(LocalDateTime.now());

		summaryRepo.save(summary);
	}

	public List<LearningContent> getLearningContentBySymptom(String symptom) {
		// Simulate AI-expanded matching (in future, use OpenAI or similar)
		List<String> synonyms = getExpandedKeywords(symptom.toLowerCase());
		return learningRepo.findAll().stream()
				.filter(content -> synonyms.contains(content.getSymptomKeyword().toLowerCase()))
				.collect(Collectors.toList());
	}

	private List<String> getExpandedKeywords(String symptom) {
		Map<String, List<String>> map = new HashMap<>();
		map.put("fever", List.of("fever", "temperature", "high temp"));
		map.put("headache", List.of("headache", "head pain", "migraine"));
		map.put("cough", List.of("cough", "sore throat", "cold"));
		map.put("rash", List.of("rash", "skin irritation", "red spots"));
		map.put("pain", List.of("pain", "ache", "discomfort"));
		return map.getOrDefault(symptom, List.of(symptom));
	}

	public void savePreConsultSymptom(String email, String symptom) {
		Patient patient = patientRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("Patient not found"));
		PreConsultSymptom pcs = new PreConsultSymptom();
		pcs.setSymptom(symptom);
		pcs.setPatient(patient);
		pcs.setSubmittedAt(LocalDateTime.now());
		preConsultRepo.save(pcs);
	}

	public List<LearningContent> getLearningContentForPatient(String email) {
		Patient patient = patientRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("Patient not found"));
		return preConsultRepo.findTopByPatientOrderBySubmittedAtDesc(patient)
				.map(pre -> getLearningContentBySymptom(pre.getSymptom())).orElse(List.of());
	}

	public String getChatbotResponse(String userMessage) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(openaiKey);
		headers.set("HTTP-Referer", "https://yourtelemedapp.com");
		headers.set("X-Title", "Telemedicine Chatbot");

		Map<String, Object> requestBody = Map.of("model", "mistralai/mistral-7b-instruct",

				// "model", "mistral-7b-instruct", or "gpt-3.5-turbo" if using a paid model
				"messages",
				List.of(Map.of("role", "system", "content",
						"You are a helpful virtual assistant in a telemedicine app. You do NOT diagnose. Provide only general health tips and education."),
						Map.of("role", "user", "content", userMessage)));

		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
		try {
			ResponseEntity<Map> response = restTemplate.postForEntity(OPENAI_CHAT_URL, entity, Map.class);
			if (response.getStatusCode().is2xxSuccessful()) {
				List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
				Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
				return (String) message.get("content");
			} else {
				return "Sorry, I couldn’t process your message at the moment.";
			}
		} catch (RestClientException e) {
			return "Error contacting AI service: " + e.getMessage();
		}
	}

}
