package pl.pollub.backend.admin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.pollub.backend.auth.user.Role;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.auth.AuthService;
import pl.pollub.backend.auth.jwt.JwtService;
import pl.pollub.backend.categories.dto.UserDto;
import pl.pollub.backend.auth.dto.UserRoleDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private Long userId;

    @BeforeEach
    void setUp() {
        // Czyszczenie bazy i dodanie przykładowych użytkowników
        authService.getUsersRepository().deleteAll();

        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@example.com");
        admin.setPassword(authService.hashPassword("adminpassword"));
        admin.setRole(Role.ADMIN);
        authService.getUsersRepository().save(admin);

        User user = new User();
        user.setUsername("user");
        user.setEmail("user@example.com");
        user.setPassword(authService.hashPassword("userpassword"));
        user.setRole(Role.USER);
        authService.getUsersRepository().save(user);

        userId = user.getId();
    }

    @Test
    void shouldReturnListOfUsers() throws Exception {
        // Wykonanie żądania GET do endpointu /api/admin/users
        String response = mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + generateAdminToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2)) // Oczekujemy dwóch użytkowników
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Parsowanie odpowiedzi JSON do listy obiektów UserDto
        List<UserDto> users = objectMapper.readValue(response, new TypeReference<List<UserDto>>() {});

        // Sprawdzanie zawartości listy
        assertThat(users).hasSize(2);
        assertThat(users).extracting(UserDto::getUsername).containsExactlyInAnyOrder("admin", "user");
        assertThat(users).extracting(UserDto::getEmail).containsExactlyInAnyOrder("admin@example.com", "user@example.com");
    }

    @Test
    void shouldUpdateUserRoleSuccessfully() throws Exception {
        // Przygotowanie danych zmiany roli
        UserRoleDto roleDto = new UserRoleDto();
        roleDto.setRole(Role.ADMIN.name());

        // Wykonanie żądania PUT do zmiany roli
        mockMvc.perform(put("/api/admin/" + userId + "/role")
                        .header("Authorization", "Bearer " + generateAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Rola użytkownika została zaktualizowana."));

        // Weryfikacja, że rola użytkownika została zmieniona
        User updatedUser = authService.getUsersRepository().findById(userId).orElseThrow();
        assertThat(updatedUser.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void shouldReturnForbiddenWithoutToken() throws Exception {
        // Przygotowanie danych zmiany roli
        UserRoleDto roleDto = new UserRoleDto();
        roleDto.setRole(Role.ADMIN.name());

        // Wykonanie żądania PUT bez tokenu
        mockMvc.perform(put("/api/admin/1/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleDto)))
                .andExpect(status().isForbidden()); // Spodziewamy się 403
    }


    private String generateAdminToken() {
        User admin = authService.loadUserByUsername("admin");
        return jwtService.createToken(admin);
    }
}
