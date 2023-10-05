package com.ridango.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    @NotNull(message = "Sender account ID cannot be null")
    private Long senderAccountId;

    @NotNull(message = "Receiver account ID cannot be null")
    private Long receiverAccountId;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must positive")
    private BigDecimal amount;
}
