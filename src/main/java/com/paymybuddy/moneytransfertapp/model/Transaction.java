package com.paymybuddy.moneytransfertapp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_accountNumber", nullable = false)
    private BankAccount sender;

    @ManyToOne
    @JoinColumn(name = "receiver_accountNumber", nullable = false)
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