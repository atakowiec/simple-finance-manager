package pl.pollub.backend.categories;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pollub.backend.admin.AdminService;
import pl.pollub.backend.categories.dto.CategoryCreateDto;
import pl.pollub.backend.categories.dto.CategoryUpdateDto;
import pl.pollub.backend.categories.model.TransactionCategory;
import pl.pollub.backend.exception.HttpException;

import java.util.List;

/**
 * Controller for managing categories.
 */
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Tag(name = "Kategorie", description = "Zarządzanie kategoriami")
public class CategoriesController {

    private final CategoryRepository categoryRepository;
    private final AdminService adminService;

    @Operation(summary = "Pobierz wszystkie kategorie")
    @ApiResponse(responseCode = "200", description = "Lista kategorii")
    @GetMapping
    public List<TransactionCategory> getCategories() {
        return categoryRepository.findAll();
    }

    @Operation(summary = "Dodaj nową kategorię")
    @ApiResponse(responseCode = "200", description = "Dodano kategorię")
    @PostMapping
    public ResponseEntity<String> addCategory(@RequestBody CategoryCreateDto categoryDto) {
        String message = adminService.addCategory(categoryDto);
        return ResponseEntity.ok(message);
    }

    @Operation(summary = "Aktualizuj kategorię")
    @ApiResponse(responseCode = "200", description = "Zaktualizowano kategorię")
    @PutMapping("/{id}")
    public ResponseEntity<String> updateCategory(@PathVariable Long id, @RequestBody CategoryUpdateDto categoryUpdateDto) {
        String message = adminService.updateCategory(id, categoryUpdateDto);
        return ResponseEntity.ok(message);
    }

    @Operation(summary = "Usuń kategorię")
    @ApiResponse(responseCode = "200", description = "Usunięto kategorię")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        String message = adminService.deleteCategory(id);
        return ResponseEntity.ok(message);
    }

    @Operation(summary = "Pobierz ikonę kategorii")
    @ApiResponse(responseCode = "200", description = "Pobrano ikonę kategorii")
    @GetMapping("/icon/{id}")
    public ResponseEntity<byte[]> getIcon(@PathVariable Long id) {
        TransactionCategory category = categoryRepository.findById(id).orElseThrow(() -> new HttpException(404, "Category not found"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(category.getIcon().length);

        return new ResponseEntity<>(category.getIcon(), headers, HttpStatus.OK);
    }
}
