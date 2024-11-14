package pl.pollub.backend.group;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.pollub.backend.auth.dto.LoginDto;
import pl.pollub.backend.auth.dto.RegisterDto;
import pl.pollub.backend.auth.user.Role;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.auth.AuthService;
import pl.pollub.backend.exception.HttpException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthLoginIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Usuwamy istniejących użytkowników przed każdym testem
        authService.getUsersRepository().deleteAll();

        // Tworzymy użytkownika testowego
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword(authService.hashPassword("testpassword"));
        user.setRole(Role.USER);
        authService.getUsersRepository().save(user);
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        // Przygotowanie danych logowania
        LoginDto loginDto = new LoginDto();
        loginDto.setIdentifier("testuser");
        loginDto.setPassword("testpassword");

        // Wykonanie żądania logowania
        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Parsowanie odpowiedzi
        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);

        // Sprawdzenie zawartości odpowiedzi
        String token = (String) responseMap.get("token");
        assertThat(token).isNotEmpty();
        assertThat(responseMap.get("username")).isEqualTo("testuser");
        assertThat(responseMap.get("role")).isEqualTo("USER");

        // Printowanie tokenu
        System.out.println("Wygenerowany token JWT: " + token);
    }

    @Test
    void shouldFailLoginWithIncorrectPassword() throws Exception {
        // Przygotowanie danych logowania z niepoprawnym hasłem
        LoginDto loginDto = new LoginDto();
        loginDto.setIdentifier("testuser");
        loginDto.setPassword("wrongpassword");

        // Wykonanie żądania logowania z niepoprawnym hasłem
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldFailLoginWithNonExistingUser() throws Exception {
        // Przygotowanie danych logowania dla nieistniejącego użytkownika
        LoginDto loginDto = new LoginDto();
        loginDto.setIdentifier("nonexistinguser");
        loginDto.setPassword("testpassword");

        // Wykonanie żądania logowania dla nieistniejącego użytkownika
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized());
    }
}
