package com.abanoj.note.config;

import com.abanoj.note.token.Token;
import com.abanoj.note.token.TokenRepository;
import com.abanoj.note.token.TokenType;
import com.abanoj.note.user.Role;
import com.abanoj.note.user.User;
import com.abanoj.note.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LogoutService logoutService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("john@email.com")
                .password("encoded")
                .role(Role.USER)
                .build();
    }

    @Test
    void logoutShouldRevokeAllUserTokensWhenValidBearerToken() {
        Token accessToken = Token.builder()
                .id(1L).token("access-jwt").tokenType(TokenType.BEARER)
                .revoked(false).user(user).build();
        Token refreshToken = Token.builder()
                .id(2L).token("refresh-jwt").tokenType(TokenType.BEARER)
                .revoked(false).user(user).build();

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer access-jwt");
        when(jwtService.extractUsername("access-jwt")).thenReturn("john@email.com");
        when(userRepository.findByEmail("john@email.com")).thenReturn(Optional.of(user));
        when(tokenRepository.findAllValidTokensByUser(1L)).thenReturn(List.of(accessToken, refreshToken));

        logoutService.logout(request, response, authentication);

        assertThat(accessToken.isRevoked()).isTrue();
        assertThat(refreshToken.isRevoked()).isTrue();
        verify(tokenRepository).saveAll(List.of(accessToken, refreshToken));
    }

    @Test
    void logoutShouldDoNothingWhenNoAuthHeader() {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        logoutService.logout(request, response, authentication);

        verifyNoInteractions(tokenRepository);
    }

    @Test
    void logoutShouldDoNothingWhenNotBearerToken() {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic credentials");

        logoutService.logout(request, response, authentication);

        verifyNoInteractions(tokenRepository);
    }

    @Test
    void logoutShouldDoNothingWhenUserNotFound() {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer some-jwt");
        when(jwtService.extractUsername("some-jwt")).thenReturn("unknown@email.com");
        when(userRepository.findByEmail("unknown@email.com")).thenReturn(Optional.empty());

        logoutService.logout(request, response, authentication);

        verify(tokenRepository, never()).saveAll(any());
    }
}
