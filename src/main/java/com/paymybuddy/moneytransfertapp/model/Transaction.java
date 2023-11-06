package com.paymybuddy.moneytransfertapp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "sender", nullable = false)
    private BankAccount sender;

    @OneToOne
    @JoinColumn(name = "receiver", nullable = false)
    private BankAccount receiver;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private double fee;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date date;

    @Column(nullable = false)
    private String status;

    private String paymentReason; // Reason for payment

}