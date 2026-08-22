package com.finova.controller;

import com.finova.dto.RegisterRequest;
import com.finova.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    void register_shouldReturnTokenAndUserDetails_whenRequestIsValid() throws Exception {

        RegisterRequest request = RegisterRequest.builder()
                .fullName("Test User")
                .email("integrationtest@example.com")
                .password("password123")
                .phone("9999999999")
                .build();

        // Clean up in case a previous test run left this user
        userRepository.findByEmail(request.getEmail())
                .ifPresent(userRepository::delete);

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email")
                        .value("integrationtest@example.com"))
                .andExpect(jsonPath("$.role")
                        .value("ROLE_CUSTOMER"));
    }

    @Test
    void register_shouldReturnBadRequest_whenEmailAlreadyExists() throws Exception {

        RegisterRequest request = RegisterRequest.builder()
                .fullName("Duplicate User")
                .email("duplicate@example.com")
                .password("password123")
                .phone("9999999999")
                .build();

        // Clean up before the test
        userRepository.findByEmail(request.getEmail())
                .ifPresent(userRepository::delete);

        // First registration should succeed
        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());

        // Second registration should fail
        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Email is already registered"));
    }

    @Test
    void login_shouldReturnUnauthorized_whenCredentialsAreInvalid() throws Exception {

        String badLogin = """
                {
                    "email": "nonexistent@example.com",
                    "password": "wrongpassword"
                }
                """;

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(badLogin)
                )
                .andExpect(status().isUnauthorized());
    }
}