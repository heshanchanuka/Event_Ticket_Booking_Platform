package com.eventbooking.payment_service.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Payment එකේ අවුලක් ගියොත් (HTTP 400)
    @ExceptionHandler(PaymentProcessingException.class)
    public ProblemDetail handlePaymentException(PaymentProcessingException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Payment Processing Failed");
        problemDetail.setType(URI.create("https://api.paymentservice.com/errors/payment-failed"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    // 2. DTO Validation ෆේල් වුණොත් (HTTP 400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
        // මොන field එකද වැරදි කියලා ලස්සනට අරන් එක string එකකට දාගන්නවා
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errors);
        problemDetail.setTitle("Validation Failed");
        problemDetail.setType(URI.create("https://api.paymentservice.com/errors/validation-failed"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    // 3. වෙනත් ඕනෑම Unhandled Error එකක් ආවොත් (HTTP 500)
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        log.error("Unhandled exception caught in Payment Service: ", ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred in Payment Service");
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setType(URI.create("https://api.paymentservice.com/errors/internal-error"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}