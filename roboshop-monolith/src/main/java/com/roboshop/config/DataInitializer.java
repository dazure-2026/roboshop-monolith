package com.roboshop.config;

import com.roboshop.model.User;
import com.roboshop.repository.UserRepository;
import at.favre.lib.crypto.bcrypt.BCrypt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    public DataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@roboshop.com");
            admin.setPassword(BCrypt.withDefaults().hashToString(10, "RoboShop@1".toCharArray()));
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setPhone("555-0100");
            userRepository.save(admin);
            System.out.println("Default admin user created (admin / RoboShop@1)");
        }
    }
}
