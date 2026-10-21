package com.eventbooking.payment_service.dto;



import java.time.LocalDateTime;

public record PaymentResponse(
        String bookingReference,
        String transactionId,
        String status,
        String message,
        LocalDateTime paymentDate
) {
}