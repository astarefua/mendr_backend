package com.telemed.controller;

import com.telemed.dto.PaymentRequest;
import com.telemed.dto.PaymentResponse;
import com.telemed.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/initialize")
    public ResponseEntity<PaymentResponse> initializePayment(@RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.initializePayment(request));
    }

    @GetMapping("/verify/{reference}")
    public ResponseEntity<Boolean> verifyPayment(@PathVariable String reference) {
        return ResponseEntity.ok(paymentService.verifyPayment(reference));
    }
}
