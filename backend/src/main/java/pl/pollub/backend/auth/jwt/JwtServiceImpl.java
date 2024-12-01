package pl.pollub.backend.auth.jwt;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.pollub.backend.auth.user.User;

import java.util.Date;

/**
 * Service for managing JWT tokens. It provides methods for creating, parsing and resolving JWT tokens.
 */
@Component
public class JwtServiceImpl implements JwtService {
    public static final String AUTHORIZATION_HEADER = "Authorization";

    private final String secret;
    private final long expiration;
    private final JwtParser jwtParser;

    public JwtServiceImpl(@Value("${security.jwt.secret}") String secret,
                          @Value("${security.jwt.expiration}") long expiration) {
        this.secret = secret;
        this.expiration = expiration;
        this.jwtParser = Jwts.parser().setSigningKey(secret);
    }

    @Override
    public String createToken(User user) {
        Claims claims = Jwts.claims();
        claims.setSubject(String.valueOf(user.getId()));
        claims.put("id", user.getId());
        claims.put("role", user.getRole().name());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    @Override
    public Claims parseJwtClaims(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

    @Override
    public Claims resolveClaims(HttpServletRequest req) {
        return parseJwtClaims(resolveToken(req));
    }

    @Override
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        throw new JwtException("Token not found");
    }

    @Override
    public void addTokenToResponse(HttpServletResponse response, String token) {
        response.setHeader(AUTHORIZATION_HEADER, "Bearer " + token);
    }

    @Override
    public void addTokenToResponse(HttpServletResponse response, User user) {
        addTokenToResponse(response, createToken(user));
    }
}
