package com.eventbooking.payment_service.domain;



import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Booking Service එකෙන් එවන Booking Reference එක මෙතන සේව් කරගන්නවා
    @Column(nullable = false)
    private String bookingReference;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    // Payment Gateway එකෙන් දෙන අංකය (අපි මේක mock කරනවා)
    private String transactionId;

    private LocalDateTime paymentDate;
}
