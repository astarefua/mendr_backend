package com.telemed.service;

import com.telemed.dto.PaymentRequest;
import com.telemed.dto.PaymentResponse;
import com.telemed.model.Appointment;
import com.telemed.repository.AppointmentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentService {

    @Value("${paystack.secret.key}")
    private String secretKey;

    @Value("${paystack.initialize.url}")
    private String initializeUrl;

    @Value("${paystack.verify.url}")
    private String verifyUrl;
    
    @Autowired
    private AppointmentRepository appointmentRepo;

    private final RestTemplate restTemplate = new RestTemplate();

    public PaymentResponse initializePayment(PaymentRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(secretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> payload = new HashMap<>();
        payload.put("email", request.getEmail());
        payload.put("amount", request.getAmount());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(initializeUrl, entity, Map.class);

        Map data = (Map) response.getBody().get("data");

        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setAuthorizationUrl((String) data.get("authorization_url"));
        paymentResponse.setAccessCode((String) data.get("access_code"));
        paymentResponse.setReference((String) data.get("reference"));

        return paymentResponse;
    }

    public boolean verifyPayment(String reference) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(secretKey);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
            verifyUrl + reference,
            HttpMethod.GET,
            entity,
            Map.class
        );

        Map data = (Map) response.getBody().get("data");
        boolean success = "success".equals(data.get("status"));

        //return "success".equals(data.get("status"));
        
        if (success) {
            String email = ((Map<String, Object>) data.get("customer")).get("email").toString();
            List<Appointment> appointments = appointmentRepo.findByPatientEmailAndStatus(email, "PENDING");
            for (Appointment appointment : appointments) {
                appointment.setStatus("CONFIRMED");
                appointment.setPaid(true);
                appointmentRepo.save(appointment);
            }
        }

        return success;
    
    }
}
