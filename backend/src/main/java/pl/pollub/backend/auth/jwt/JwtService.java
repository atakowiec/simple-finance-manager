package pl.pollub.backend.auth.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pl.pollub.backend.auth.user.User;

public interface JwtService {
    String createToken(User user);

    /**
     * Parses the specified JWT token and returns its claims.
     *
     * @param token JWT token to parse
     * @return claims from the token
     */
    Claims parseJwtClaims(String token);

    /**
     * Resolves the claims from the specified request.
     *
     * @param req request from which the claims will be resolved
     * @return claims from the request or null if the token is not present or invalid
     */
    Claims resolveClaims(HttpServletRequest req);

    /**
     * Resolves the JWT token from the specified request.
     *
     * @param request request from which the token will be resolved
     * @return JWT token from the request or null if the token is not present
     */
    String resolveToken(HttpServletRequest request);

    /**
     * Adds the specified token to the response.
     *
     * @param response response to which the token will be added
     * @param token    token to add
     */
    void addTokenToResponse(HttpServletResponse response, String token);

    /**
     * Creates a JWT token for the specified user and adds it to the response.
     *
     * @param response response to which the token will be added
     * @param user     user for which the token will be created
     */
    void addTokenToResponse(HttpServletResponse response, User user);
}
