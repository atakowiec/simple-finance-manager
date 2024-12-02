package pl.pollub.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.pollub.backend.auth.AuthService;
import pl.pollub.backend.auth.AuthServiceImpl;
import pl.pollub.backend.auth.jwt.JwtService;
import pl.pollub.backend.auth.user.UsersRepository;

@Configuration
public class ServiceConfig {
    @Bean
    public AuthService authService(UsersRepository usersRepository, JwtService jwtService) {
        return new AuthServiceImpl(usersRepository, jwtService);
    }
}
