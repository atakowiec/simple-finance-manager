package pl.pollub.backend.auth.jwt;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.pollub.backend.auth.user.User;

import java.util.Date;

@Component
public class JwtService {
    public static final String AUTHORIZATION_HEADER = "Authorization";

    private final String secret;
    private final long expiration;
    private final JwtParser jwtParser;

    public JwtService(@Value("${security.jwt.secret}") String secret,
                      @Value("${security.jwt.expiration}") long expiration) {
        this.secret = secret;
        this.expiration = expiration;
        this.jwtParser = Jwts.parser().setSigningKey(secret);
    }

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

    public Claims parseJwtClaims(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

    public Claims resolveClaims(HttpServletRequest req) {
        try {
            String token = resolveToken(req);
            if (token != null) {
                return parseJwtClaims(token);
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    public void addTokenToResponse(HttpServletResponse response, String token) {
        response.setHeader(AUTHORIZATION_HEADER, "Bearer " + token);
    }

    public void addTokenToResponse(HttpServletResponse response, User user) {
        addTokenToResponse(response, createToken(user));
    }

    public void invalidateToken(HttpServletResponse response) {
        response.setHeader(AUTHORIZATION_HEADER, "");
    }
}
