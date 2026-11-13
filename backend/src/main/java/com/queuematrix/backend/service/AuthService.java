package com.queuematrix.backend.service;

import com.queuematrix.backend.dto.request.LoginRequest;
import com.queuematrix.backend.dto.request.RegisterRequest;
import com.queuematrix.backend.dto.response.AuthResponse;
import com.queuematrix.backend.dto.response.UserSummaryDto;
import com.queuematrix.backend.entity.*;
import com.queuematrix.backend.exception.BadRequestException;
import com.queuematrix.backend.exception.UnauthorizedException;
import com.queuematrix.backend.repository.OrganizationRepository;
import com.queuematrix.backend.repository.UserRepository;
import com.queuematrix.backend.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${qm.admin.bootstrap.email:adminQM@gmail.com}")
    private String adminEmail;

    public AuthService(UserRepository userRepository,
                       OrganizationRepository organizationRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String normalizePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new BadRequestException("Phone number cannot be empty.");
        }

        String clean = phone.replaceAll("[\\s\\-\\(\\)]", "");

        if (!clean.startsWith("+")) {
            clean = "+91" + clean;
        }

        if (!clean.matches("^\\+[1-9][0-9]{9,14}$")) {
            throw new BadRequestException("Invalid phone number format: " + phone + ". Must include 10-15 digits.");
        }

        return clean;
    }

    @Transactional
    public UserSummaryDto register(RegisterRequest request) {
        // Enforce Security Rule: Never allow self-registration as SUPER_ADMIN or ORGANIZATION_ADMIN
        if (request.getRole() == Role.SUPER_ADMIN || request.getRole() == Role.ORGANIZATION_ADMIN) {
            throw new BadRequestException("Self-registration as " + request.getRole() + " is not permitted. Privileged accounts must be provisioned through administrative authorization.");
        }
        if (request.getRole() != Role.CUSTOMER && request.getRole() != Role.STAFF) {
            throw new BadRequestException("Self-registration is only permitted for Customer and Staff roles.");
        }

        String normalizedEmail = request.getEmail().trim().toLowerCase();

        // Enforce Security Rule: Prevent public registration from claiming the reserved administrator email
        if (adminEmail != null && normalizedEmail.equalsIgnoreCase(adminEmail.trim())) {
            throw new BadRequestException("The email address " + request.getEmail() + " is reserved for system administration.");
        }

        // Email uniqueness validation
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new BadRequestException("An account with email " + request.getEmail() + " already exists.");
        }

        // Phone normalization & uniqueness validation
        String normalizedPhone = normalizePhone(request.getPhone());
        if (userRepository.existsByPhone(normalizedPhone)) {
            throw new BadRequestException("An account with phone number " + normalizedPhone + " already exists.");
        }

        // Determine user status according to security policies
        UserStatus status;
        if (request.getRole() == Role.CUSTOMER) {
            status = UserStatus.ACTIVE;
        } else if (request.getRole() == Role.STAFF) {
            status = UserStatus.PENDING_INVITE_APPROVAL;
        } else {
            status = UserStatus.ACTIVE;
        }

        User user = new User();
        user.setFullName(request.getFullName().trim());
        user.setEmail(normalizedEmail);
        user.setPhone(normalizedPhone);
        // Cryptographically hash password using BCrypt - never store plain text
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setStatus(status);
        user.setPhoneVerified(true);
        user.setPreferredOrgType(request.getOrganizationType() != null ? request.getOrganizationType() : OrganizationType.OTHER);

        User savedUser = userRepository.save(user);

        // Security requirement: do NOT log password or sensitive fields
        log.info("New user registered successfully: id={}, email={}, role={}, status={}",
                savedUser.getId(), savedUser.getEmail(), savedUser.getRole(), savedUser.getStatus());

        return UserSummaryDto.fromEntity(savedUser);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password."));

        // Validate password securely with BCrypt
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password.");
        }

        // Check account authorization status
        if (user.getStatus() == UserStatus.PENDING_APPROVAL) {
            throw new UnauthorizedException("Your Organization Admin account is pending administrative approval.");
        }
        if (user.getStatus() == UserStatus.PENDING_INVITE_APPROVAL) {
            throw new UnauthorizedException("Your Staff account is pending authorized invitation/approval.");
        }
        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new UnauthorizedException("Your account has been suspended. Please contact support.");
        }
        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new UnauthorizedException("Your account is currently inactive.");
        }

        // Issue JWT token with authorized claims
        String token = jwtTokenProvider.generateToken(user);

        log.info("User logged in successfully: id={}, email={}, role={}",
                user.getId(), user.getEmail(), user.getRole());

        return new AuthResponse(token, UserSummaryDto.fromEntity(user));
    }
}
