package com.queuematrix.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.queuematrix.backend.config.AdminBootstrapRunner;
import com.queuematrix.backend.dto.request.LoginRequest;
import com.queuematrix.backend.dto.request.RegisterRequest;
import com.queuematrix.backend.entity.OrganizationType;
import com.queuematrix.backend.entity.Role;
import com.queuematrix.backend.entity.User;
import com.queuematrix.backend.entity.UserStatus;
import com.queuematrix.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class AuthControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AdminBootstrapRunner adminBootstrapRunner;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /api/public/home - Returns public landing details without auth")
    void testPublicHome() throws Exception {
        mockMvc.perform(get("/api/public/home"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.heading").value("Welcome to QueueMatrix"))
                .andExpect(jsonPath("$.data.tagline").value("Smarter Queues. Better Experiences."))
                .andExpect(jsonPath("$.data.supportedCategories", hasSize(6)));
    }

    @Test
    @DisplayName("POST /api/auth/register - Direct registration without OTP as Customer")
    void testDirectCustomerRegistration() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "Jane Doe",
                "jane@example.com",
                "StrongPassword123!",
                "9876543210",
                OrganizationType.HOSPITAL,
                Role.CUSTOMER
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fullName").value("Jane Doe"))
                .andExpect(jsonPath("$.data.email").value("jane@example.com"))
                .andExpect(jsonPath("$.data.role").value("CUSTOMER"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"))
                .andExpect(jsonPath("$.data.password").doesNotExist());

        User saved = userRepository.findByEmail("jane@example.com").orElseThrow();
        org.junit.jupiter.api.Assertions.assertNotEquals("StrongPassword123!", saved.getPassword());
        org.junit.jupiter.api.Assertions.assertTrue(saved.getPassword().startsWith("$2a$"));
    }

    @Test
    @DisplayName("POST /api/auth/register - Disallows privileged self-registration as SUPER_ADMIN or ORGANIZATION_ADMIN")
    void testDisallowPrivilegedRegistration() throws Exception {
        RegisterRequest adminReq = new RegisterRequest(
                "Admin Wannabe",
                "admin@hacked.com",
                "Password123!",
                "9123456780",
                OrganizationType.BANK,
                Role.ORGANIZATION_ADMIN
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message", containsString("Self-registration as ORGANIZATION_ADMIN is not permitted")));

        RegisterRequest superAdminReq = new RegisterRequest(
                "Super Admin Wannabe",
                "super@hacked.com",
                "Password123!",
                "9123456781",
                OrganizationType.GOVERNMENT_OFFICE,
                Role.SUPER_ADMIN
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(superAdminReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message", containsString("Self-registration as SUPER_ADMIN is not permitted")));
    }

    @Test
    @DisplayName("POST /api/auth/register - Rejects duplicate email and duplicate phone")
    void testDuplicateEmailAndPhoneValidation() throws Exception {
        RegisterRequest first = new RegisterRequest(
                "User One",
                "user1@example.com",
                "Password123!",
                "9876543210",
                OrganizationType.RESTAURANT,
                Role.CUSTOMER
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(first)))
                .andExpect(status().isCreated());

        // Duplicate email
        RegisterRequest duplicateEmail = new RegisterRequest(
                "User Two",
                "user1@example.com",
                "Password456!",
                "9876543211",
                OrganizationType.RESTAURANT,
                Role.CUSTOMER
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateEmail)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message", containsString("already exists")));

        // Duplicate phone
        RegisterRequest duplicatePhone = new RegisterRequest(
                "User Three",
                "user3@example.com",
                "Password789!",
                "9876543210",
                OrganizationType.RESTAURANT,
                Role.CUSTOMER
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicatePhone)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message", containsString("already exists")));
    }

    @Test
    @DisplayName("POST /api/auth/login - Email and password login issues JWT token")
    void testSuccessfulLogin() throws Exception {
        RegisterRequest regReq = new RegisterRequest(
                "Login Tester",
                "tester@example.com",
                "SecretPass123",
                "9198765432",
                OrganizationType.UNIVERSITY,
                Role.CUSTOMER
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regReq)))
                .andExpect(status().isCreated());

        LoginRequest loginReq = new LoginRequest("tester@example.com", "SecretPass123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").isString())
                .andExpect(jsonPath("$.data.user.email").value("tester@example.com"))
                .andExpect(jsonPath("$.data.user.role").value("CUSTOMER"));
    }

    @Test
    @DisplayName("POST /api/auth/login - Rejects incorrect password and non-existent email")
    void testInvalidLoginCredentials() throws Exception {
        RegisterRequest regReq = new RegisterRequest(
                "Credential Tester",
                "credentials@example.com",
                "CorrectPassword123",
                "9871122334",
                OrganizationType.BANK,
                Role.CUSTOMER
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regReq)))
                .andExpect(status().isCreated());

        // Incorrect password
        LoginRequest wrongPass = new LoginRequest("credentials@example.com", "WrongPassword999");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongPass)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", containsString("Invalid email or password")));

        // Non-existent email
        LoginRequest unknownEmail = new LoginRequest("nobody@example.com", "AnyPassword");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(unknownEmail)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", containsString("Invalid email or password")));
    }

    @Test
    @DisplayName("Role security: Staff registers as PENDING_INVITE_APPROVAL and cannot login until approved")
    void testStaffApprovalWorkflow() throws Exception {
        RegisterRequest staffReq = new RegisterRequest(
                "Hospital Staff",
                "staff@hospital.com",
                "StaffPassword123",
                "9911223344",
                OrganizationType.HOSPITAL,
                Role.STAFF
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(staffReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("PENDING_INVITE_APPROVAL"))
                .andExpect(jsonPath("$.data.role").value("STAFF"));

        LoginRequest loginReq = new LoginRequest("staff@hospital.com", "StaffPassword123");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", containsString("pending")));
    }

    @Test
    @DisplayName("Security: Normal registration cannot use reserved admin email")
    void testDisallowRegistrationWithAdminEmail() throws Exception {
        RegisterRequest fakeAdmin = new RegisterRequest(
                "Admin Impostor",
                "adminQM@gmail.com",
                "HackedPass123!",
                "9876500000",
                OrganizationType.HOSPITAL,
                Role.CUSTOMER
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fakeAdmin)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message", containsString("reserved for system administration")));
    }

    @Test
    @DisplayName("Admin account setup: Initial admin logs in via normal login page with SUPER_ADMIN privileges")
    void testAdminLoginViaNormalLoginPage() throws Exception {
        // Provision the initial administrator securely (simulating backend bootstrap / seed)
        User admin = new User();
        admin.setFullName("AdminofQM");
        admin.setEmail("adminqm@gmail.com");
        admin.setPhone("+919800000001");
        admin.setPassword(passwordEncoder.encode("SecureAdminPassword123!"));
        admin.setRole(Role.SUPER_ADMIN);
        admin.setStatus(UserStatus.ACTIVE);
        admin.setPhoneVerified(true);
        userRepository.save(admin);

        // Perform login through the standard authentication endpoint (used by the normal login page)
        LoginRequest loginReq = new LoginRequest("adminQM@gmail.com", "SecureAdminPassword123!");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").isString())
                .andExpect(jsonPath("$.data.user.fullName").value("AdminofQM"))
                .andExpect(jsonPath("$.data.user.email").value("adminqm@gmail.com"))
                .andExpect(jsonPath("$.data.user.role").value("SUPER_ADMIN"))
                .andExpect(jsonPath("$.data.user.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("Admin bootstrap: AdminBootstrapRunner provisions AdminofQM with SUPER_ADMIN privileges when password is provided")
    void testAdminBootstrapRunnerProvisionsAdmin() throws Exception {
        org.springframework.test.util.ReflectionTestUtils.setField(adminBootstrapRunner, "adminPassword", "BootstrappedAdminPass123!");
        adminBootstrapRunner.run();

        User admin = userRepository.findByEmail("adminqm@gmail.com").orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals("AdminofQM", admin.getFullName());
        org.junit.jupiter.api.Assertions.assertEquals("adminqm@gmail.com", admin.getEmail());
        org.junit.jupiter.api.Assertions.assertEquals(Role.SUPER_ADMIN, admin.getRole());
        org.junit.jupiter.api.Assertions.assertEquals(UserStatus.ACTIVE, admin.getStatus());
        org.junit.jupiter.api.Assertions.assertTrue(passwordEncoder.matches("BootstrappedAdminPass123!", admin.getPassword()));

        // Also verify login succeeds with the bootstrapped account
        LoginRequest loginReq = new LoginRequest("adminQM@gmail.com", "BootstrappedAdminPass123!");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.user.role").value("SUPER_ADMIN"));
    }
}
