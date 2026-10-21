package com.eventbooking.payment_service.gateway;



import java.math.BigDecimal;

public interface PaymentGateway {

    // Payment එක process කරලා Transaction ID එකක් return කරන method එක
    String process(String bookingReference, BigDecimal amount);

}