package com.eventbooking.payment_service.repository;




import com.eventbooking.payment_service.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Booking Reference එක පාවිච්චි කරලා Payment එක හොයාගන්න method එකක්
    Optional<Payment> findByBookingReference(String bookingReference);
}