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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.abanoj.note.config.JwtAuthenticationFilter.TOKEN_TYPE;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        User user = User.builder()
                .firstname(request.firstname())
                .lastname(request.lastname())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();
        userRepository.save(user);
        log.info("New user registered: {}", user.getEmail());

        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(jwtToken, user, jwtService.getJwtExpiration());
        saveUserToken(refreshToken, user, jwtService.getRefreshExpiration());
        return new AuthenticationResponse(jwtToken, refreshToken);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        User user = userRepository.findByEmail(request.email()).orElseThrow(() -> new UserNotFoundException("User not found!"));
        log.info("User authenticated: {}", request.email());
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(jwtToken, user, jwtService.getJwtExpiration());
        saveUserToken(refreshToken, user, jwtService.getRefreshExpiration());
        return new AuthenticationResponse(jwtToken, refreshToken);
    }

    @Transactional
    public AuthenticationResponse refreshToken(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String email;

        if (authHeader == null || !authHeader.startsWith(TOKEN_TYPE)) {
            throw new AuthenticationNotFoundException("Authentication not found!");
        }
        refreshToken = authHeader.substring(7);
        email = jwtService.extractUsername(refreshToken);

        if (email == null) {
            throw new AuthenticationNotFoundException("Authentication not found!");
        }

        String tokenType = jwtService.extractTokenType(refreshToken);
        if (!JwtService.REFRESH_TOKEN_TYPE.equals(tokenType)) {
            throw new AuthenticationNotFoundException("Token not valid!");
        }

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found!"));

        boolean isTokenValid = tokenRepository.findByToken(refreshToken)
                .map(token -> !token.isRevoked())
                .orElse(false);

        if (!jwtService.isTokenValid(refreshToken, user) || !isTokenValid) {
            log.warn("Invalid refresh token for user: {}", email);
            throw new AuthenticationNotFoundException("Token not valid!");
        }
        String accessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        revokeAllUserTokens(user);
        saveUserToken(accessToken, user, jwtService.getJwtExpiration());
        saveUserToken(newRefreshToken, user, jwtService.getRefreshExpiration());

        return new AuthenticationResponse(accessToken, newRefreshToken);
    }

    private void revokeAllUserTokens(User user) {
        List<Token> validTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        if (validTokens.isEmpty()) return;
        validTokens.forEach(token -> token.setRevoked(true));
        tokenRepository.saveAll(validTokens);
    }

    private void saveUserToken(String jwtToken, User user, long expirationMillis) {
        Token token = Token.builder()
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .user(user)
                .revoked(false)
                .expiresAt(LocalDateTime.now().plus(expirationMillis, ChronoUnit.MILLIS))
                .build();
        tokenRepository.save(token);
    }

}
