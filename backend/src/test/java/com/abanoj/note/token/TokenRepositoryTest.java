package com.abanoj.note.token;

import com.abanoj.note.user.Role;
import com.abanoj.note.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TokenRepositoryTest {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test@email.com")
                .password("password")
                .role(Role.USER)
                .build();
        entityManager.persist(user);
        entityManager.flush();
    }

    private Token createToken(String tokenValue, boolean revoked) {
        Token token = Token.builder()
                .token(tokenValue)
                .tokenType(TokenType.BEARER)
                .revoked(revoked)
                .user(user)
                .build();
        return entityManager.persist(token);
    }

    @Test
    void findAllValidTokensByUserShouldReturnValidTokens() {
        createToken("valid-token", false);
        createToken("expired-token", true);
        entityManager.flush();

        List<Token> result = tokenRepository.findAllValidTokensByUser(user.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getToken()).isEqualTo("valid-token");
    }

    @Test
    void findAllValidTokensByUserShouldReturnEmptyWhenAllInvalid() {
        createToken("expired", true);
        entityManager.flush();

        List<Token> result = tokenRepository.findAllValidTokensByUser(user.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void findAllValidTokensByUserShouldReturnEmptyWhenNoTokens() {
        List<Token> result = tokenRepository.findAllValidTokensByUser(user.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void findByTokenShouldReturnTokenWhenExists() {
        createToken("my-token", false);
        entityManager.flush();

        Optional<Token> result = tokenRepository.findByToken("my-token");

        assertThat(result).isPresent();
        assertThat(result.get().getUser().getEmail()).isEqualTo("test@email.com");
    }

    @Test
    void findByTokenShouldReturnEmptyWhenNotExists() {
        Optional<Token> result = tokenRepository.findByToken("non-existent");

        assertThat(result).isEmpty();
    }
}
