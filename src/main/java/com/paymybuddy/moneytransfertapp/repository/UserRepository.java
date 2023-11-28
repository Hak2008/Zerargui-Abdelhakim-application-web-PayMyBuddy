package com.paymybuddy.moneytransfertapp.repository;

import com.paymybuddy.moneytransfertapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    @Modifying
    @Query(value = "DELETE FROM friendship WHERE user_id = :userId OR friend_id = :userId", nativeQuery = true)
    void deleteFriendshipsByUserId(@Param("userId") Long userId);
}

