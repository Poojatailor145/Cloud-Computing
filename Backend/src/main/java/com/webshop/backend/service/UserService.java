package com.webshop.backend.service;

import com.webshop.backend.model.User;
import com.webshop.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public User createUser(User user) {
        // Clear the ID to ensure a new user is created.
        user.setUserId(null);
        User createdUser = userRepository.save(user);
        logger.info("Created User: {}", createdUser);
        return createdUser;
    }

    /**
     * Retrieves all users.
     */
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        logger.info("Retrieved {} users", users.size());
        return users;
    }

    /**
     * Retrieves a user by ID.
     */
    public Optional<User> getUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        logger.info("Retrieved User with ID {}: {}", userId, user);
        return user;
    }

    /**
     * Updates a user's details.
     */
    public User updateUser(Long userId, User updatedUser) {
        return userRepository.findById(userId).map(user -> {
            user.setEmail(updatedUser.getEmail());
            user.setFullName(updatedUser.getFullName());
            user.setPhoneNumber(updatedUser.getPhoneNumber());
            user.setAddress(updatedUser.getAddress());
            User savedUser = userRepository.save(user);
            logger.info("Updated User with ID {}: {}", userId, savedUser);
            return savedUser;
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Deletes a user by ID.
     */
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
        logger.info("Deleted User with ID {}", userId);
    }
}
