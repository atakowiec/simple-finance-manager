package pl.pollub.backend.group;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.pollub.backend.auth.dto.LoginDto;
import pl.pollub.backend.auth.user.Role;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.auth.AuthService;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        // Usuwamy wszystkich użytkowników, aby uniknąć konfliktów
        authService.getUsersRepository().deleteAll();

        // Tworzymy użytkownika testowego
        String hashedPassword = authService.hashPassword("testpassword");
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword(hashedPassword);
        user.setRole(Role.USER);
        authService.getUsersRepository().save(user);

        // Tworzymy obiekt DTO do logowania
        LoginDto loginDto = new LoginDto();
        loginDto.setIdentifier("testuser");
        loginDto.setPassword("testpassword");

        // Logujemy się i pobieramy token
        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Pobieramy token z odpowiedzi
        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
        token = (String) responseMap.get("token");

        // Sprawdzamy, czy token został wygenerowany
        assertThat(token).isNotNull();
    }

    @Test
    void shouldAccessProtectedEndpointWithToken() throws Exception {
}
}
