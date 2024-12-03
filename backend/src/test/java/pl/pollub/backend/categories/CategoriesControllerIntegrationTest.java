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
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void shouldAddCategorySuccessfully() throws Exception {
        CategoryCreateDto categoryDto = new CategoryCreateDto();
        categoryDto.setName("Test Category");
        categoryDto.setCategoryType(CategoryType.EXPENSE);
        categoryDto.setIcon(new byte[]{1, 2, 3});

        when(categoryService.addCategory(any(CategoryCreateDto.class))).thenReturn("Kategoria została dodana pomyślnie");

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Kategoria została dodana pomyślnie"));

        verify(categoryService, times(1)).addCategory(any(CategoryCreateDto.class));
    }

    @Test
    void shouldAddAndDeleteCategorySuccessfully() throws Exception {
        CategoryCreateDto categoryDto = new CategoryCreateDto();
        categoryDto.setName("Test Category");
        categoryDto.setCategoryType(CategoryType.EXPENSE);
        categoryDto.setIcon(new byte[]{1, 2, 3});

        when(categoryService.addCategory(any(CategoryCreateDto.class)))
                .thenReturn("Kategoria została dodana pomyślnie");

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Kategoria została dodana pomyślnie"));
        verify(categoryService, times(1)).addCategory(any(CategoryCreateDto.class));

        TransactionCategory addedCategory = new TransactionCategory();
        addedCategory.setId(1L);
        addedCategory.setName("Test Category");
        when(categoryService.getCategoryByIdOrThrow(1L)).thenReturn(addedCategory);

        Long categoryId = addedCategory.getId();

        mockMvc.perform(delete("/categories/" + categoryId))
                .andExpect(status().isOk());

        boolean exists = categoryRepository.existsById(categoryId);
        assert !exists : "Kategoria nadal istnieje w bazie danych, mimo że powinna zostać usunięta.";
    }
}