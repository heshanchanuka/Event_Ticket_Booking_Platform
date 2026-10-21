package com.eventbooking.payment_service.service;


import com.eventbooking.payment_service.domain.Payment;
import com.eventbooking.payment_service.domain.PaymentStatus;
import com.eventbooking.payment_service.dto.PaymentRequest;
import com.eventbooking.payment_service.dto.PaymentResponse;
import com.eventbooking.payment_service.exception.PaymentProcessingException;
import com.eventbooking.payment_service.gateway.PaymentGateway;
import com.eventbooking.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    // මේකෙන් Database එකත් එක්ක කතා කරනවා
    private final PaymentRepository paymentRepository;
    // මේකෙන් සල්ලි කපන Gateway එකත් එක්ක කතා කරනවා
    private final PaymentGateway paymentGateway;

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("Starting payment process for booking: {}", request.bookingReference());

        // --- පියවර 1: ඩබල් පේමන්ට් නවත්තන එක (Idempotency) ---
        // කවුරුහරි කලින් මේ Booking Reference එකට සල්ලි ගෙවලද කියලා DB එකෙන් බලනවා.
        paymentRepository.findByBookingReference(request.bookingReference())
                .ifPresent(payment -> {
                    // කලින් ගෙවලා නම්, අපි ආයේ සල්ලි කපන්නේ නෑ. Error එකක් යවනවා!
                    log.warn("Payment already processed for booking: {}", request.bookingReference());
                    throw new PaymentProcessingException("Payment already exists for this booking reference");
                });


        // --- පියවර 2: Gateway එක හරහා සල්ලි කපන එක ---
        String transactionId;
        try {
            // අපි Section 7 වල හදපු Gateway එකට ගාණයි රෙෆරන්ස් එකයි දීලා සල්ලි කපන්න කියනවා.
            // සාර්ථක වුණොත් එයා අපිට TXN-xxxx කියලා අංකයක් දෙනවා.
            transactionId = paymentGateway.process(request.bookingReference(), request.amount());

        } catch (PaymentProcessingException ex) {
            // Gateway එකෙන් සල්ලි කපන්න බැරුව ෆේල් වුණොත් (උදා: ලක්ෂ 10ට වැඩි නම්)
            // අපි ඒක Database එකේ 'FAILED' කියලා සේව් කරලා, error එක එළියට විසි කරනවා.
            saveFailedPayment(request);
            throw ex;
        }


        // --- පියවර 3: ගෙවීම සාර්ථකයි නම් Database එකේ සේව් කිරීම ---
        Payment payment = new Payment();
        payment.setBookingReference(request.bookingReference());
        payment.setAmount(request.amount());
        payment.setTransactionId(transactionId);
        payment.setStatus(PaymentStatus.SUCCESS); // තත්ත්වය SUCCESS කරනවා
        payment.setPaymentDate(LocalDateTime.now());

        // Entity එක ඩේටාබේස් එකට save කරනවා
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment saved successfully to DB with ID: {}", savedPayment.getId());


        // --- පියවර 4: රිසිට් එක (Response) ආපහු යැවීම ---
        return new PaymentResponse(
                savedPayment.getBookingReference(),
                savedPayment.getTransactionId(),
                savedPayment.getStatus().name(),
                "Payment Processed Successfully",
                savedPayment.getPaymentDate()
        );
    }


    // සල්ලි කපන එක ෆේල් වුණොත්, ඒකත් DB එකේ සටහන් කරලා තියන්න වෙනම ලියපු පොඩි method එකක්
    private void saveFailedPayment(PaymentRequest request) {
        Payment failedPayment = new Payment();
        failedPayment.setBookingReference(request.bookingReference());
        failedPayment.setAmount(request.amount());
        failedPayment.setStatus(PaymentStatus.FAILED); // තත්ත්වය FAILED
        failedPayment.setPaymentDate(LocalDateTime.now());
        paymentRepository.save(failedPayment);
        log.error("Saved FAILED payment record to DB for booking: {}", request.bookingReference());
    }
}