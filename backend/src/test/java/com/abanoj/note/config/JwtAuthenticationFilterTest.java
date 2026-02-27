package com.abanoj.note.config;

import com.abanoj.note.token.Token;
import com.abanoj.note.token.TokenRepository;
import com.abanoj.note.token.TokenType;
import com.abanoj.note.user.Role;
import com.abanoj.note.user.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

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

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternalShouldContinueChainWhenNoAuthHeader() throws ServletException, IOException {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternalShouldContinueChainWhenNotBearerToken() throws ServletException, IOException {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic credentials");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternalShouldAuthenticateWhenValidToken() throws ServletException, IOException {
        Token token = Token.builder()
                .id(1L).token("valid-jwt").tokenType(TokenType.BEARER)
                .revoked(false).user(user).build();

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer valid-jwt");
        when(jwtService.extractUsername("valid-jwt")).thenReturn("john@email.com");
        when(jwtService.extractTokenType("valid-jwt")).thenReturn("access");
        when(userDetailsService.loadUserByUsername("john@email.com")).thenReturn(user);
        when(tokenRepository.findByToken("valid-jwt")).thenReturn(Optional.of(token));
        when(jwtService.isTokenValid("valid-jwt", user)).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("john@email.com");
    }

    @Test
    void doFilterInternalShouldNotAuthenticateWhenJwtServiceSaysInvalid() throws ServletException, IOException {
        Token token = Token.builder()
                .id(1L).token("invalid-jwt").tokenType(TokenType.BEARER)
                .revoked(false).user(user).build();

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer invalid-jwt");
        when(jwtService.extractUsername("invalid-jwt")).thenReturn("john@email.com");
        when(jwtService.extractTokenType("invalid-jwt")).thenReturn("access");
        when(userDetailsService.loadUserByUsername("john@email.com")).thenReturn(user);
        when(tokenRepository.findByToken("invalid-jwt")).thenReturn(Optional.of(token));
        when(jwtService.isTokenValid("invalid-jwt", user)).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternalShouldNotAuthenticateWhenRefreshTokenUsedAsAccess() throws ServletException, IOException {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer refresh-jwt");
        when(jwtService.extractUsername("refresh-jwt")).thenReturn("john@email.com");
        when(jwtService.extractTokenType("refresh-jwt")).thenReturn("refresh");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternalShouldContinueChainWhenExpiredJwtException() throws ServletException, IOException {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer expired-jwt");
        when(request.getRequestURI()).thenReturn("/api/v1/test");
        when(jwtService.extractUsername("expired-jwt")).thenThrow(new io.jsonwebtoken.ExpiredJwtException(null, null, "Token expired"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternalShouldContinueChainWhenInvalidJwtException() throws ServletException, IOException {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer malformed-jwt");
        when(request.getRequestURI()).thenReturn("/api/v1/test");
        when(jwtService.extractUsername("malformed-jwt")).thenThrow(new io.jsonwebtoken.JwtException("Invalid"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
