package pl.pollub.backend.auth.jwt;


import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.pollub.backend.auth.AuthService;
import pl.pollub.backend.auth.user.User;

import java.io.IOException;
import java.util.Optional;

/**
 * Filter for JWT authentication. It extracts the JWT token from the request, validates it and sets the user in the security context.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final AuthService authService;

    @Override
    public void doFilterInternal(@NonNull HttpServletRequest request,
                                 @NonNull HttpServletResponse response,
                                 @NonNull FilterChain filterChain) throws ServletException, IOException {
        Optional<Claims> tokenClaims = extractTokenClaims(request);
        if (tokenClaims.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        Optional<String> userId = extractUserIdFromClaims(tokenClaims.get());
        if (userId.isEmpty() || isAlreadyAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }

        Optional<User> user = retrieveUser(userId.get());
        if (user.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        setAuthenticationContext(user.get(), request);
        jwtService.addTokenToResponse(response, user.get());

        filterChain.doFilter(request, response);
    }

    /**
     * Extracts and validates JWT token claims from the request.
     *
     * @param request the HTTP request
     * @return Optional containing the claims if valid, empty otherwise
     */
    private Optional<Claims> extractTokenClaims(HttpServletRequest request) {
        try {
            Claims claims = jwtService.resolveClaims(request);
            return Optional.of(claims);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Extracts the user ID from the token claims.
     *
     * @param claims the JWT token claims
     * @return Optional containing the user ID string if present, empty otherwise
     */
    private Optional<String> extractUserIdFromClaims(Claims claims) {
        String userId = claims.getSubject();
        return Optional.ofNullable(userId);
    }

    /**
     * Checks if the security context already has an authenticated user.
     *
     * @return true if already authenticated, false otherwise
     */
    private boolean isAlreadyAuthenticated() {
        return SecurityContextHolder.getContext().getAuthentication() != null;
    }

    /**
     * Retrieves the user from the database by user ID.
     *
     * @param stringUserId the user ID as a string
     * @return Optional containing the user if found, empty otherwise
     */
    private Optional<User> retrieveUser(String stringUserId) {
        try {
            long userId = Long.parseLong(stringUserId);
            User user = this.authService.getUserById(userId);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Creates an authentication token and sets it in the security context.
     *
     * @param user the authenticated user
     * @param request the HTTP request
     */
    private void setAuthenticationContext(User user, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                user,
                null,
                user.getAuthorities()
        );

        authToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
