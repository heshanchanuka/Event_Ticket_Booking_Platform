package com.eventbooking.payment_service.dto;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PaymentRequest(

        @NotBlank(message = "Booking reference is mandatory")
        String bookingReference,

        @NotNull(message = "Amount is mandatory")
        @Positive(message = "Amount must be strictly greater than zero")
        BigDecimal amount
) {
}