package com.abanoj.note.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByEmailShouldReturnUserWhenExists() {
        User user = User.builder()
                .email("test@email.com")
                .password("password")
                .role(Role.USER)
                .build();
        entityManager.persist(user);
        entityManager.flush();

        Optional<User> result = userRepository.findByEmail("test@email.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@email.com");
    }

    @Test
    void findByEmailShouldReturnEmptyWhenNotExists() {
        Optional<User> result = userRepository.findByEmail("nonexistent@email.com");

        assertThat(result).isEmpty();
    }

    @Test
    void saveShouldThrowWhenDuplicateEmail() {
        User user1 = User.builder()
                .email("duplicate@email.com")
                .password("password1")
                .role(Role.USER)
                .build();
        entityManager.persist(user1);
        entityManager.flush();

        User user2 = User.builder()
                .email("duplicate@email.com")
                .password("password2")
                .role(Role.USER)
                .build();

        assertThatThrownBy(() -> {
            entityManager.persist(user2);
            entityManager.flush();
        }).isInstanceOf(Exception.class);
    }
}
