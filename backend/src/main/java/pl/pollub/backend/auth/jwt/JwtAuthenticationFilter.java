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

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final AuthService authService;

    @Override
    public void doFilterInternal(@NonNull HttpServletRequest request,
                                 @NonNull HttpServletResponse response,
                                 @NonNull FilterChain filterChain) throws ServletException, IOException {
        Claims tokenClaims = jwtService.resolveClaims(request);

        if (tokenClaims == null) {
            filterChain.doFilter(request, response);
            return;
        }

        final String stringUserId = tokenClaims.getSubject();

        if (stringUserId == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }


        User user;
        try {
            long userId = Long.parseLong(stringUserId);
            user = this.authService.getUserById(userId);
        } catch (Exception e) {
            jwtService.invalidateToken(response);
            filterChain.doFilter(request, response);
            return;
        }

        if(user == null) {
            jwtService.invalidateToken(response);
            filterChain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                user,
                null,
                user.getAuthorities()
        );

        authToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authToken);

        // generate new token and set it in response
        jwtService.addTokenToResponse(response, user);

        filterChain.doFilter(request, response);
    }
}
