package com.abanoj.note.config;

import com.abanoj.note.token.Token;
import com.abanoj.note.token.TokenRepository;
import com.abanoj.note.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.abanoj.note.config.JwtAuthenticationFilter.TOKEN_TYPE;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String jwt;

        if(authHeader == null || !authHeader.startsWith(TOKEN_TYPE)){
            return;
        }
        jwt = authHeader.substring(7);

        String email;
        try {
            email = jwtService.extractUsername(jwt);
        } catch (Exception ex) {
            log.warn("Invalid JWT on logout: {}", ex.getMessage());
            return;
        }

        if (email == null) {
            return;
        }

        userRepository.findByEmail(email).ifPresent(user -> {
            List<Token> validTokens = tokenRepository.findAllValidTokensByUser(user.getId());
            if (validTokens.isEmpty()) return;
            validTokens.forEach(token -> token.setRevoked(true));
            tokenRepository.saveAll(validTokens);
            log.info("All tokens revoked for user: {}", email);
        });
    }
}
