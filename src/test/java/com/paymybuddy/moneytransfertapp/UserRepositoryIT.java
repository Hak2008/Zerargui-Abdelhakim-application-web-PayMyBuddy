package com.paymybuddy.moneytransfertapp;

import com.paymybuddy.moneytransfertapp.model.User;
import com.paymybuddy.moneytransfertapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testDefaultColumnValues() {

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setFirstName("Tom");
        user.setLastName("Henri");
        user.setDateOfBirth(new Date());

        userRepository.save(user);

        User savedUser = userRepository.findByEmail("test@example.com");

        assertThat(savedUser.getFirstName()).isEqualTo("Tom");
        assertThat(savedUser.getLastName()).isEqualTo("Henri");

    }

    @Test
    public void testFindByEmail() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setFirstName("Tom");
        user.setLastName("Henri");
        user.setDateOfBirth(new Date());

        userRepository.save(user);

        User foundUser = userRepository.findByEmail("test@example.com");

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo("test@example.com");

    }

    @Test
    public void testDeleteUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setFirstName("Tom");
        user.setLastName("Henri");
        user.setDateOfBirth(new Date());

        userRepository.save(user);

        userRepository.delete(user);

        User deletedUser = userRepository.findByEmail("test@example.com");

        assertThat(deletedUser).isNull();
    }
    @Test
    public void testUpdateUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setFirstName("Tom");
        user.setLastName("Henri");
        user.setDateOfBirth(new Date());

        userRepository.save(user);


        user.setAddress("3 place");
        user.setPhoneNumber("333 333 333");
        userRepository.save(user);

        User updatedUser = userRepository.findByEmail("test@example.com");

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getAddress()).isEqualTo("3 place");
        assertThat(updatedUser.getPhoneNumber()).isEqualTo("333 333 333");

    }
    @After
    public void cleanup() {
        userRepository.deleteAll();
    }
}
