package com.ecommerce.auth.config;

import com.ecommerce.auth.model.User;
import com.ecommerce.auth.repository.UserRepository;
import com.ecommerce.common.constants.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        createDefaultUsers();
    }
    
    private void createDefaultUsers() {
        createUserIfNotExists("admin@ecommerce.com", "Admin@123", "Admin", "User", UserRole.ADMIN);
        createUserIfNotExists("manager@ecommerce.com", "Manager@123", "Manager", "User", UserRole.MANAGER);
        createUserIfNotExists("employee@ecommerce.com", "Employee@123", "Employee", "User", UserRole.EMPLOYEE);
        createUserIfNotExists("customer@ecommerce.com", "Customer@123", "Customer", "User", UserRole.CUSTOMER);
    }
    
    private void createUserIfNotExists(String email, String password, String firstName, String lastName, UserRole role) {
        if (!userRepository.existsByEmail(email)) {
            User user = User.builder()
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .firstName(firstName)
                    .lastName(lastName)
                    .role(role)
                    .enabled(true)
                    .build();
            
            userRepository.save(user);
            log.info("Created default user: {} with role: {}", email, role);
        }
    }
}
