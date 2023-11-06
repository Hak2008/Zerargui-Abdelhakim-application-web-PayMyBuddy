package com.paymybuddy.moneytransfertapp.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.Date;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private Date dateOfBirth;

    private String address;

    private String phoneNumber;

    @OneToOne(mappedBy = "user")
    private BankAccount bankAccount;

    @ManyToMany
    @JoinTable(
            name = "Friendship",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private List<User> friends;

}