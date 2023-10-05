package com.ridango.application.integration.controller;

import com.ridango.application.dto.PaymentRequest;
import com.ridango.application.entity.Account;
import com.ridango.application.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AccountRepository accountRepository;
    private Account sender;
    private Account receiver;

    @Test
    public void whenMakingPayment_thenReturnsSuccessResponse() {
        sender = accountRepository.save(new Account(null, "sender", new BigDecimal(500), 0));
        receiver = accountRepository.save(new Account(null, "receiver", new BigDecimal(600), 0));

        PaymentRequest request = new PaymentRequest(sender.getId(), receiver.getId(), new BigDecimal(100));
        ResponseEntity<String> response = restTemplate.postForEntity("/payment", request, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Payment processed successfully", response.getBody());
    }

    @Test
    public void whenMakingPaymentWithInsufficientFunds_thenReturnsInsufficientFundsResponse() {
        sender = accountRepository.save(new Account(null, "sender", new BigDecimal(500), 0));
        receiver = accountRepository.save(new Account(null, "receiver", new BigDecimal(600), 0));

        PaymentRequest request = new PaymentRequest(sender.getId(), receiver.getId(), new BigDecimal(1000)); // Amount greater than balance
        ResponseEntity<String> response = restTemplate.postForEntity("/payment", request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Sender does not have enough funds", response.getBody());

        accountRepository.deleteById(sender.getId());
        accountRepository.deleteById(receiver.getId());
    }

    @Test
    public void whenMakingPaymentToSameAccount_thenReturnsError() {
        sender = accountRepository.save(new Account(null, "sender", new BigDecimal(500), 0));
        receiver = accountRepository.save(new Account(null, "receiver", new BigDecimal(600), 0));

        PaymentRequest request = new PaymentRequest(sender.getId(), sender.getId(), new BigDecimal(100)); // Sender and Receiver are the same
        ResponseEntity<String> response = restTemplate.postForEntity("/payment", request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Cannot transfer funds to the same account.", response.getBody());

        accountRepository.deleteById(sender.getId());
        accountRepository.deleteById(receiver.getId());
    }

    @Test
    public void whenMakingPaymentToNonExistentAccount_thenReturnsError() {
        sender = accountRepository.save(new Account(null, "sender", new BigDecimal(500), 0));
        receiver = accountRepository.save(new Account(null, "receiver", new BigDecimal(600), 0));

        PaymentRequest request = new PaymentRequest(sender.getId(), 500L, new BigDecimal(100));
        ResponseEntity<String> response = restTemplate.postForEntity("/payment", request, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Invalid account ID: 500", response.getBody()); // Assuming this is the message your API returns

        accountRepository.deleteById(sender.getId());
        accountRepository.deleteById(receiver.getId());
    }

}
