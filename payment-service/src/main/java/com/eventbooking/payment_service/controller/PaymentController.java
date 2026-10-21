package com.eventbooking.payment_service.controller;




import com.eventbooking.payment_service.dto.PaymentRequest;
import com.eventbooking.payment_service.dto.PaymentResponse;
import com.eventbooking.payment_service.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // සල්ලි ගෙවන්න (Process Payment) එවන POST request එක බාරගන්න Endpoint එක
    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest request) {

        // Service එකට request එක යවලා, ඒකෙන් එන රිසිට් එක (PaymentResponse) අරගන්නවා
        PaymentResponse response = paymentService.processPayment(request);

        // ඒ රිසිට් එක HTTP 200 OK status එකක් එක්ක ආපහු යවනවා
        return ResponseEntity.ok(response);
    }
}