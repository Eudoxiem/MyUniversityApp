package com.myuniversity.app.security;

import com.myuniversity.app.entity.TokenInvalide;
import com.myuniversity.app.repository.TokenInvalideRepository;
import com.myuniversity.app.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final TokenInvalideRepository tokenInvalideRepository;

    public JwtAuthFilter(JwtService jwtService, UserRepository userRepository, TokenInvalideRepository tokenInvalideRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.tokenInvalideRepository = tokenInvalideRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (!jwtService.isTokenValid(token)) {
                log.warn("Tentative d'accès avec token invalide/expiré - email: {}", email);
                filterChain.doFilter(request, response);
                return;
            }

            String jti = jwtService.extractTokenId(token);
            if (tokenInvalideRepository.findByJti(jti).isPresent()) {
                log.warn("Tentative d'accès avec token révoqué - email: {}, jti: {}", email, jti);
                filterChain.doFilter(request, response);
                return;
            }

            userRepository.findByEmail(email).ifPresentOrElse(user -> {
                List<SimpleGrantedAuthority> authorities = List.of(
                        new SimpleGrantedAuthority(user.getRole().name())
                );
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(user, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.debug("Authentification réussie - email: {}, rôle: {}", email, user.getRole());
            }, () -> log.warn("Token valide mais utilisateur introuvable - email: {}", email));
        }

        filterChain.doFilter(request, response);
    }
}
