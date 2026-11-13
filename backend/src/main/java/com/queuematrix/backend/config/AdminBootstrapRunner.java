package com.queuematrix.backend.config;

import com.queuematrix.backend.entity.Role;
import com.queuematrix.backend.entity.User;
import com.queuematrix.backend.entity.UserStatus;
import com.queuematrix.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Order(1)
public class AdminBootstrapRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrapRunner.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${qm.admin.bootstrap.enabled:true}")
    private boolean bootstrapEnabled;

    @Value("${qm.admin.bootstrap.name:AdminofQM}")
    private String adminName;

    @Value("${qm.admin.bootstrap.email:adminQM@gmail.com}")
    private String adminEmail;

    @Value("${qm.admin.bootstrap.phone:+919800000001}")
    private String adminPhone;

    @Value("${qm.admin.bootstrap.password:}")
    private String adminPassword;

    public AdminBootstrapRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!bootstrapEnabled) {
            return;
        }

        String normalizedEmail = adminEmail.trim().toLowerCase();
        Optional<User> existingUserOpt = userRepository.findByEmail(normalizedEmail);

        if (existingUserOpt.isPresent()) {
            User existing = existingUserOpt.get();
            boolean updated = false;

            if (!adminName.equals(existing.getFullName())) {
                existing.setFullName(adminName);
                updated = true;
            }
            if (existing.getRole() != Role.SUPER_ADMIN) {
                existing.setRole(Role.SUPER_ADMIN);
                updated = true;
            }
            if (existing.getStatus() != UserStatus.ACTIVE) {
                existing.setStatus(UserStatus.ACTIVE);
                updated = true;
            }
            if (adminPassword != null && !adminPassword.isBlank()) {
                existing.setPassword(passwordEncoder.encode(adminPassword));
                updated = true;
            }

            if (updated) {
                userRepository.save(existing);
                log.info("QueueMatrix administrator account updated for email: {}", normalizedEmail);
            } else {
                log.info("QueueMatrix administrator account is verified and active for email: {}", normalizedEmail);
            }
        } else {
            if (adminPassword == null || adminPassword.isBlank()) {
                log.info("QueueMatrix initial administrator ({}) not provisioned: QM_ADMIN_PASSWORD environment variable is not set.", normalizedEmail);
                return;
            }

            User admin = new User();
            admin.setFullName(adminName);
            admin.setEmail(normalizedEmail);
            admin.setPhone(adminPhone);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.SUPER_ADMIN);
            admin.setStatus(UserStatus.ACTIVE);
            admin.setPhoneVerified(true);

            userRepository.save(admin);
            log.info("Initial QueueMatrix administrator provisioned successfully with SUPER_ADMIN privileges for email: {}", normalizedEmail);
        }
    }
}
