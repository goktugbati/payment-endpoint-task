package com.ridango.application.controller;

import com.ridango.application.dto.PaymentRequest;
import com.ridango.application.entity.Payment;
import com.ridango.application.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payment")
    public ResponseEntity<String> processPayment(@Valid @RequestBody PaymentRequest request){
        Payment payment = paymentService.makePayment(request);
        logger.info("Payment processed successfully with ID: {}", payment.getId());
        return ResponseEntity.status(HttpStatus.OK).body("Payment processed successfully");
    }
}
