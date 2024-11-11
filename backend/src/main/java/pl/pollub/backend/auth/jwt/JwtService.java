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

    /**
     * Creates a JWT token for the specified user with its id and role in the claims.
     *
     * @param user user for which the token will be created
     * @return JWT token
     */
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

    /**
     * Parses the specified JWT token and returns its claims.
     *
     * @param token JWT token to parse
     * @return claims from the token
     */
    public Claims parseJwtClaims(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

    /**
     * Resolves the claims from the specified request.
     *
     * @param req request from which the claims will be resolved
     * @return claims from the request or null if the token is not present or invalid
     */
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

    /**
     * Resolves the JWT token from the specified request.
     *
     * @param request request from which the token will be resolved
     * @return JWT token from the request or null if the token is not present
     */
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    /**
     * Adds the specified token to the response.
     *
     * @param response response to which the token will be added
     * @param token    token to add
     */
    public void addTokenToResponse(HttpServletResponse response, String token) {
        response.setHeader(AUTHORIZATION_HEADER, "Bearer " + token);
    }

    /**
     * Creates a JWT token for the specified user and adds it to the response.
     *
     * @param response response to which the token will be added
     * @param user     user for which the token will be created
     */
    public void addTokenToResponse(HttpServletResponse response, User user) {
        addTokenToResponse(response, createToken(user));
    }
}
