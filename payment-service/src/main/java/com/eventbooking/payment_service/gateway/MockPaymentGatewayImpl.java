package com.eventbooking.payment_service.gateway;




import com.eventbooking.payment_service.exception.PaymentProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
public class MockPaymentGatewayImpl implements PaymentGateway {

    @Override
    public String process(String bookingReference, BigDecimal amount) {
        log.info("Connecting to Mock Payment Gateway for Booking Ref: {}", bookingReference);

        try {
            // ඇත්තම Payment Gateway එකකට කතා කරනවා වගේ පෙන්නන්න පොඩි delay එකක් දානවා (500ms)
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PaymentProcessingException("Payment processing was interrupted");
        }

        // ටෙස්ට් කරන්න ලේසි වෙන්න, රුපියල් 1,000,000 කට වඩා වැඩි ගාණක් ආවොත් payment එක ෆේල් කරනවා
        if (amount.compareTo(new BigDecimal("1000000")) > 0) {
            log.error("Payment declined by gateway. Amount {} exceeds limit.", amount);
            throw new PaymentProcessingException("Payment declined by your bank. Exceeds limit.");
        }

        // Payment එක සාර්ථකයි නම්, අහඹු Transaction ID එකක් (TXN-xxxx) හදලා return කරනවා
        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log.info("Payment successful. Transaction ID: {}", transactionId);

        return transactionId;
    }
}