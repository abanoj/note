package com.abanoj.note.auth;

import com.abanoj.note.auth.dto.AuthenticationRequest;
import com.abanoj.note.auth.dto.AuthenticationResponse;
import com.abanoj.note.auth.dto.RegisterRequest;
import com.abanoj.note.config.JwtService;
import com.abanoj.note.exception.AuthenticationNotFoundException;
import com.abanoj.note.exception.UserNotFoundException;
import com.abanoj.note.token.Token;
import com.abanoj.note.token.TokenRepository;
import com.abanoj.note.token.TokenType;
import com.abanoj.note.user.Role;
import com.abanoj.note.user.User;
import com.abanoj.note.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .firstname("John")
                .lastname("Doe")
                .email("john@email.com")
                .password("encoded")
                .role(Role.USER)
                .build();
    }

    @Test
    void registerShouldReturnTokens() {
        RegisterRequest request = new RegisterRequest("John", "Doe", "john@email.com", "password123");

        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refresh-token");

        AuthenticationResponse response = authenticationService.register(request);

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
    }

    @Test
    void registerShouldSaveBothTokens() {
        RegisterRequest request = new RegisterRequest("John", "Doe", "john@email.com", "password123");

        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refresh-token");

        authenticationService.register(request);

        ArgumentCaptor<Token> tokenCaptor = ArgumentCaptor.forClass(Token.class);
        verify(tokenRepository, times(2)).save(tokenCaptor.capture());
        List<Token> savedTokens = tokenCaptor.getAllValues();
        assertThat(savedTokens.get(0).getToken()).isEqualTo("access-token");
        assertThat(savedTokens.get(0).getTokenType()).isEqualTo(TokenType.BEARER);
        assertThat(savedTokens.get(0).isRevoked()).isFalse();
        assertThat(savedTokens.get(1).getToken()).isEqualTo("refresh-token");
        assertThat(savedTokens.get(1).getTokenType()).isEqualTo(TokenType.BEARER);
        assertThat(savedTokens.get(1).isRevoked()).isFalse();
    }

    @Test
    void authenticateShouldReturnTokens() {
        AuthenticationRequest request = new AuthenticationRequest("john@email.com", "password123");

        when(userRepository.findByEmail("john@email.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("new-access-token");
        when(jwtService.generateRefreshToken(user)).thenReturn("new-refresh-token");
        when(tokenRepository.findAllValidTokensByUser(1L)).thenReturn(List.of());

        AuthenticationResponse response = authenticationService.authenticate(request);

        assertThat(response.accessToken()).isEqualTo("new-access-token");
        assertThat(response.refreshToken()).isEqualTo("new-refresh-token");
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void authenticateShouldRevokeOldTokens() {
        AuthenticationRequest request = new AuthenticationRequest("john@email.com", "password123");
        Token oldToken = Token.builder().id(1L).token("old").revoked(false).user(user).build();

        when(userRepository.findByEmail("john@email.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("new-token");
        when(jwtService.generateRefreshToken(user)).thenReturn("new-refresh");
        when(tokenRepository.findAllValidTokensByUser(1L)).thenReturn(List.of(oldToken));

        authenticationService.authenticate(request);

        assertThat(oldToken.isRevoked()).isTrue();
        verify(tokenRepository).saveAll(List.of(oldToken));
    }

    @Test
    void authenticateShouldThrowWhenUserNotFound() {
        AuthenticationRequest request = new AuthenticationRequest("unknown@email.com", "password");

        when(userRepository.findByEmail("unknown@email.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.authenticate(request))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void refreshTokenShouldReturnNewTokens() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Token storedRefreshToken = Token.builder()
                .id(2L).token("valid-refresh-token").revoked(false).user(user).build();

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer valid-refresh-token");
        when(jwtService.extractUsername("valid-refresh-token")).thenReturn("john@email.com");
        when(jwtService.extractTokenType("valid-refresh-token")).thenReturn("refresh");
        when(userRepository.findByEmail("john@email.com")).thenReturn(Optional.of(user));
        when(tokenRepository.findByToken("valid-refresh-token")).thenReturn(Optional.of(storedRefreshToken));
        when(jwtService.isTokenValid("valid-refresh-token", user)).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("new-access-token");
        when(jwtService.generateRefreshToken(user)).thenReturn("new-refresh-token");
        when(tokenRepository.findAllValidTokensByUser(1L)).thenReturn(List.of());

        AuthenticationResponse response = authenticationService.refreshToken(request);

        assertThat(response.accessToken()).isEqualTo("new-access-token");
        assertThat(response.refreshToken()).isEqualTo("new-refresh-token");
    }

    @Test
    void refreshTokenShouldThrowWhenNoAuthHeader() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        assertThatThrownBy(() -> authenticationService.refreshToken(request))
                .isInstanceOf(AuthenticationNotFoundException.class);
    }

    @Test
    void refreshTokenShouldThrowWhenInvalidToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer invalid-token");
        when(jwtService.extractUsername("invalid-token")).thenReturn("john@email.com");
        when(jwtService.extractTokenType("invalid-token")).thenReturn("refresh");
        when(userRepository.findByEmail("john@email.com")).thenReturn(Optional.of(user));
        when(tokenRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.refreshToken(request))
                .isInstanceOf(AuthenticationNotFoundException.class);
    }

    @Test
    void refreshTokenShouldThrowWhenAccessTokenUsedAsRefresh() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer access-token");
        when(jwtService.extractUsername("access-token")).thenReturn("john@email.com");
        when(jwtService.extractTokenType("access-token")).thenReturn("access");

        assertThatThrownBy(() -> authenticationService.refreshToken(request))
                .isInstanceOf(AuthenticationNotFoundException.class);
    }

    @Test
    void refreshTokenShouldThrowWhenHeaderDoesNotStartWithBearer() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic token");

        assertThatThrownBy(() -> authenticationService.refreshToken(request))
                .isInstanceOf(AuthenticationNotFoundException.class);
    }
}
