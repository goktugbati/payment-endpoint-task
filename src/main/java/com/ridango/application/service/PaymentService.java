package com.ridango.application.service;

import com.ridango.application.dto.PaymentRequest;
import com.ridango.application.entity.Account;
import com.ridango.application.entity.Payment;
import com.ridango.application.exception.AccountNotFoundException;
import com.ridango.application.exception.InsufficientFundsException;
import com.ridango.application.exception.SameAccountTransferException;
import com.ridango.application.repository.AccountRepository;
import com.ridango.application.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;

@Service
public class PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    private final AccountRepository accountRepository;
    private final PaymentRepository paymentRepository;

    public PaymentService(AccountRepository accountRepository, PaymentRepository paymentRepository) {
        this.accountRepository = accountRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Payment makePayment(PaymentRequest request) {
        logger.info("Initiating payment from account {} to account {} for amount {}",
                request.getSenderAccountId(), request.getReceiverAccountId(), request.getAmount());
        Account sender = validateAndGetAccount(request.getSenderAccountId());
        Account receiver = validateAndGetAccount(request.getReceiverAccountId());
        validateTransfer(sender, receiver);
        validateSufficientFunds(sender, request.getAmount());
        transferFunds(sender, receiver, request.getAmount());
        Payment payment = recordPayment(sender, receiver);
        logger.info("Payment completed with ID: {}", payment.getId());
        return payment;
    }

    private Account validateAndGetAccount(Long accountId) {
        logger.debug("Validating account ID: {}", accountId);
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Invalid account ID: " + accountId));
    }

    //Validate that sender and receiver are not the same accounts
    private void validateTransfer(Account sender, Account receiver) {
        if (sender.getId().equals(receiver.getId())) {
            throw new SameAccountTransferException();
        }
    }

    private void validateSufficientFunds(Account sender, BigDecimal amount) {
        if (sender.getBalance().compareTo(amount) < 0) {
            logger.error("Insufficient funds for account {}. Required: {}, Available: {}",
                    sender.getId(), amount, sender.getBalance());
            throw new InsufficientFundsException("Sender does not have enough funds");
        }
    }

    private void transferFunds(Account sender, Account receiver, BigDecimal amount) {
        logger.debug("Transferring funds. Deducting {} from account {}. Adding to account {}.",
                amount, sender.getId(), receiver.getId());
        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));
        accountRepository.saveAll(Arrays.asList(sender, receiver));
    }

    private Payment recordPayment(Account sender, Account receiver) {
        logger.debug("Recording payment from account {} to account {}", sender.getId(), receiver.getId());
        Payment payment = new Payment();
        payment.setSenderAccount(sender);
        payment.setReceiverAccount(receiver);
        payment.setTimestamp(Instant.now());
        logger.debug("Payment timestamp set as {}", payment.getTimestamp());
        return paymentRepository.save(payment);
    }
}
