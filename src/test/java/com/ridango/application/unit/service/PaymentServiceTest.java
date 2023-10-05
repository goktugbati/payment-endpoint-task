package com.ridango.application.unit.service;

import com.ridango.application.dto.PaymentRequest;
import com.ridango.application.entity.Account;
import com.ridango.application.entity.Payment;
import com.ridango.application.exception.InsufficientFundsException;
import com.ridango.application.exception.SameAccountTransferException;
import com.ridango.application.repository.AccountRepository;
import com.ridango.application.repository.PaymentRepository;
import com.ridango.application.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void shouldCompletePaymentWhenSufficientFunds() {
        PaymentRequest request = new PaymentRequest(1L, 2L, new BigDecimal(100));
        Account sender = new Account(1L, "Ridango", new BigDecimal(200), 0);
        Account receiver = new Account(2L, "Test", new BigDecimal(100), 0);
        Payment mockPayment = new Payment();

        when(accountRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(paymentRepository.save(any(Payment.class))).thenReturn(mockPayment);

        Payment payment = paymentService.makePayment(request);

        assertNotNull(payment);
        verify(accountRepository, times(2)).findById(anyLong());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void shouldThrowInsufficientFundsExceptionWhenNotEnoughFunds() {
        PaymentRequest request = new PaymentRequest(1L, 2L, new BigDecimal(300));
        Account sender = new Account(1L, "Ridango", new BigDecimal(200), 0);
        Account receiver = new Account(2L, "Test", new BigDecimal(300), 0);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(receiver));

        assertThrows(InsufficientFundsException.class, () -> paymentService.makePayment(request));
    }

    @Test
    void shouldThrowSameAccountTransferExceptionWhenSenderAndReceiverAreSame() {
        PaymentRequest request = new PaymentRequest(1L, 1L, new BigDecimal(100));
        Account account = new Account(1L, "Test", new BigDecimal(200), 0);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        assertThrows(SameAccountTransferException.class, () -> paymentService.makePayment(request));
    }
}

