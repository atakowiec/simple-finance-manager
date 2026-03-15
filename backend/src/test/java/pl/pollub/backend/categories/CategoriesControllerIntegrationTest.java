package pl.pollub.backend.categories;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.pollub.backend.categories.dto.CategoryCreateDto;
import pl.pollub.backend.categories.dto.CategoryDto;
import pl.pollub.backend.categories.dto.CategoryUpdateDto;
import pl.pollub.backend.categories.model.CategoryType;
import pl.pollub.backend.categories.model.TransactionCategory;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @MockBean
    private CategoryRepository categoryRepository;

    @Test
    void addCategory_everythingCorrect_returnOk() throws Exception {
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
    void addAndDeleteCategory_everythingCorrect_deletesCategoryAfterAdd() throws Exception {
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

    @Test
    void saveAndGetCategories_shouldReturnSavedCategories() throws Exception {
        List<TransactionCategory> categories = Arrays.asList(
                new TransactionCategory(
                        new TransactionCategory.BasicData(1L, "Food", CategoryType.INCOME),
                        new byte[]{1}
                ),
                new TransactionCategory(
                        new TransactionCategory.BasicData(2L, "Transport", CategoryType.EXPENSE),
                        new byte[]{2}
                )
        );

        List<CategoryDto> categoryDtos = categories.stream().map(TransactionCategory::toDto).toList();

        when(categoryRepository.findAll()).thenReturn(categories);

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(categoryDtos)));
    }

    @Test
    void updateThenUndoCategory_everythingCorrect_returnsUndoMessage() throws Exception {
        CategoryUpdateDto updateDto = new CategoryUpdateDto();
        updateDto.setName("Updated Category");
        updateDto.setCategoryType(CategoryType.EXPENSE);

        when(categoryService.updateCategory(eq(1L), any(CategoryUpdateDto.class)))
                .thenReturn("Kategoria została zaktualizowana.");
        when(categoryService.undoCategoryChange(1L))
                .thenReturn("Cofnięto ostatnią zmianę kategorii.");

        mockMvc.perform(put("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Kategoria została zaktualizowana."));

        mockMvc.perform(post("/categories/1/undo"))
                .andExpect(status().isOk())
                .andExpect(content().string("Cofnięto ostatnią zmianę kategorii."));

        verify(categoryService, times(1)).updateCategory(eq(1L), any(CategoryUpdateDto.class));
        verify(categoryService, times(1)).undoCategoryChange(1L);
    }

    @Test
    void canUndoCategory_returnsBooleanStatus() throws Exception {
        when(categoryService.canUndoCategory(1L)).thenReturn(true);

        mockMvc.perform(get("/categories/1/can-undo"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(categoryService, times(1)).canUndoCategory(1L);
    }
}