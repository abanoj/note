package com.abanoj.note.config;

import com.abanoj.note.user.Role;
import com.abanoj.note.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private JwtService jwtService;
    private User user;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "SECRET_KEY", "dGVzdHNlY3JldGtleXRoYXRpc2xvbmdlbm91Z2hmb3JoczI1Ng==");
        ReflectionTestUtils.setField(jwtService, "JWT_EXPIRATION", 3600000L);
        ReflectionTestUtils.setField(jwtService, "REFRESH_EXPIRATION", 86400000L);

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
    void generateTokenShouldReturnNonNullToken() {
        String token = jwtService.generateToken(user);

        assertThat(token).isNotNull().isNotBlank();
    }

    @Test
    void extractUsernameShouldReturnEmail() {
        String token = jwtService.generateToken(user);

        String username = jwtService.extractUsername(token);

        assertThat(username).isEqualTo("john@email.com");
    }

    @Test
    void isTokenValidShouldReturnTrueForValidToken() {
        String token = jwtService.generateToken(user);

        boolean valid = jwtService.isTokenValid(token, user);

        assertThat(valid).isTrue();
    }

    @Test
    void isTokenValidShouldReturnFalseForDifferentUser() {
        String token = jwtService.generateToken(user);

        User otherUser = User.builder()
                .id(2L)
                .email("other@email.com")
                .password("encoded")
                .role(Role.USER)
                .build();

        boolean valid = jwtService.isTokenValid(token, otherUser);

        assertThat(valid).isFalse();
    }

    @Test
    void isTokenValidShouldReturnFalseForExpiredToken() {
        ReflectionTestUtils.setField(jwtService, "JWT_EXPIRATION", -1000L);

        String token = jwtService.generateToken(user);

        assertThatThrownBy(() -> jwtService.isTokenValid(token, user))
                .isInstanceOf(io.jsonwebtoken.ExpiredJwtException.class);
    }

    @Test
    void generateRefreshTokenShouldReturnValidToken() {
        String refreshToken = jwtService.generateRefreshToken(user);

        assertThat(refreshToken).isNotNull().isNotBlank();

        String username = jwtService.extractUsername(refreshToken);
        assertThat(username).isEqualTo("john@email.com");
    }

    @Test
    void generateTokenAndRefreshTokenShouldProduceDifferentTokens() {
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        assertThat(accessToken).isNotEqualTo(refreshToken);
    }
}
