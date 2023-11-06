package com.paymybuddy.moneytransfertapp.repository;

import com.paymybuddy.moneytransfertapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

}

