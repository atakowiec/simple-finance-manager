package pl.pollub.backend.categories;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import pl.pollub.backend.admin.AdminService;
import pl.pollub.backend.categories.dto.CategoryCreateDto;
import pl.pollub.backend.categories.model.CategoryType;
import pl.pollub.backend.categories.model.TransactionCategory;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CategoriesControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminService adminService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void shouldAddCategorySuccessfully() throws Exception {
        CategoryCreateDto categoryDto = new CategoryCreateDto();
        categoryDto.setName("Test Category");
        categoryDto.setCategoryType(CategoryType.EXPENSE);
        categoryDto.setIcon(new byte[]{1, 2, 3});

        when(adminService.addCategory(any(CategoryCreateDto.class))).thenReturn("Kategoria została dodana pomyślnie");

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Kategoria została dodana pomyślnie"));

        verify(adminService, times(1)).addCategory(any(CategoryCreateDto.class));
    }

    @Test
    void shouldAddAndDeleteCategorySuccessfully() throws Exception {
        CategoryCreateDto categoryDto = new CategoryCreateDto();
        categoryDto.setName("Test Category");
        categoryDto.setCategoryType(CategoryType.EXPENSE);
        categoryDto.setIcon(new byte[]{1, 2, 3});

        // Ręczne dodanie do bazy
        TransactionCategory newCategory = new TransactionCategory();
        newCategory.setName("Test Category");
        newCategory.setCategoryType(CategoryType.EXPENSE);
        newCategory.setIcon(new byte[]{1, 2, 3});
        categoryRepository.save(newCategory); // Zapisanie do bazy

        TransactionCategory addedCategory = categoryRepository.getByNameAndCategoryType("Test Category", CategoryType.EXPENSE);
        assert addedCategory != null : "Kategoria nie została poprawnie dodana";

        Long categoryId = addedCategory.getId();

        mockMvc.perform(delete("/categories/" + categoryId))
                .andExpect(status().isOk())
                .andExpect(content().string("Kategoria została usunięta pomyślnie"));

        boolean exists = categoryRepository.existsById(categoryId);
        assert !exists : "Kategoria nadal istnieje w bazie danych, mimo że powinna zostać usunięta.";
    }
}

