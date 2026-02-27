package com.abanoj.note.auth;

import com.abanoj.note.auth.dto.AuthenticationRequest;
import com.abanoj.note.auth.dto.AuthenticationResponse;
import com.abanoj.note.auth.dto.RegisterRequest;
import com.abanoj.note.config.JwtService;
import com.abanoj.note.exception.AuthenticationNotFoundException;
import com.abanoj.note.exception.GlobalExceptionHandler;
import com.abanoj.note.token.TokenRepository;
import com.abanoj.note.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.servlet.http.HttpServletRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationService authenticationService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private TokenRepository tokenRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private LogoutHandler logoutHandler;

    @Test
    void registerShouldReturn201() throws Exception {
        RegisterRequest request = new RegisterRequest("John", "Doe", "john@email.com", "password123");
        AuthenticationResponse response = new AuthenticationResponse("access-token", "refresh-token");

        when(authenticationService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.access_token").value("access-token"))
                .andExpect(jsonPath("$.refresh_token").value("refresh-token"));
    }

    @Test
    void registerShouldReturn400WhenInvalidEmail() throws Exception {
        RegisterRequest request = new RegisterRequest("John", "Doe", "invalid-email", "password123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerShouldReturn400WhenShortPassword() throws Exception {
        RegisterRequest request = new RegisterRequest("John", "Doe", "john@email.com", "short");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void authenticateShouldReturn200() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("john@email.com", "password123");
        AuthenticationResponse response = new AuthenticationResponse("access-token", "refresh-token");

        when(authenticationService.authenticate(any(AuthenticationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("access-token"));
    }

    @Test
    void authenticateShouldReturn400WhenInvalidEmail() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("not-an-email", "password123");

        mockMvc.perform(post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void refreshTokenShouldReturn200() throws Exception {
        AuthenticationResponse response = new AuthenticationResponse("new-access-token", "refresh-token");

        when(authenticationService.refreshToken(any(HttpServletRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/refresh-token")
                        .header("Authorization", "Bearer valid-refresh-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("new-access-token"));
    }

    @Test
    void refreshTokenShouldReturn401WhenNoHeader() throws Exception {
        when(authenticationService.refreshToken(any(HttpServletRequest.class)))
                .thenThrow(new AuthenticationNotFoundException("Authentication not found!"));

        mockMvc.perform(post("/api/v1/auth/refresh-token"))
                .andExpect(status().isUnauthorized());
    }
}
