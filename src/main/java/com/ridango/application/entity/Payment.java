package com.ridango.application.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

@Data
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Account senderAccount;

    @ManyToOne
    private Account receiverAccount;

    private Instant timestamp;
}
