package com.roboshop.service;

import com.roboshop.model.Order;
import com.roboshop.model.Payment;
import com.roboshop.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment processPayment(Order order) {
        return processPayment(order, null);
    }

    public Payment processPayment(Order order, String maskedCard) {
        // Mock payment processing - always succeeds
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotal().add(order.getShippingCost()));
        payment.setMethod(maskedCard != null ? "CARD " + maskedCard : "MOCK_CARD");
        payment.setStatus("SUCCESS");
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        return paymentRepository.save(payment);
    }
}
