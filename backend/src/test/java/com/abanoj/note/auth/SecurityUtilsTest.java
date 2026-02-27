package com.abanoj.note.auth;

import com.abanoj.note.exception.AuthenticationNotFoundException;
import com.abanoj.note.exception.UserNotFoundException;
import com.abanoj.note.user.Role;
import com.abanoj.note.user.User;
import com.abanoj.note.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityUtilsTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SecurityUtils securityUtils;

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

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUsernameShouldReturnUsernameWhenAuthenticated() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        String username = securityUtils.getCurrentUsername();

        assertThat(username).isEqualTo("john@email.com");
    }

    @Test
    void getCurrentUsernameShouldThrowWhenNoAuthentication() {
        SecurityContextHolder.clearContext();

        assertThatThrownBy(() -> securityUtils.getCurrentUsername())
                .isInstanceOf(AuthenticationNotFoundException.class);
    }

    @Test
    void getCurrentUserShouldReturnUserWhenFound() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userRepository.findByEmail("john@email.com")).thenReturn(Optional.of(user));

        User result = securityUtils.getCurrentUser();

        assertThat(result.getEmail()).isEqualTo("john@email.com");
    }

    @Test
    void getCurrentUserShouldThrowWhenUserNotFound() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userRepository.findByEmail("john@email.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> securityUtils.getCurrentUser())
                .isInstanceOf(UserNotFoundException.class);
    }
}
