package com.paymybuddy.moneytransfertapp.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class BankAccount {

    @Id
    private String accountNumber;

    @Column(nullable = false)
    private BigDecimal balance;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

}
