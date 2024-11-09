package pl.pollub.backend.categories;

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

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoriesController {

    private final CategoryRepository categoryRepository;
    private final AdminService adminService;

    @GetMapping
    public List<TransactionCategory> getCategories() {
        return categoryRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<String> addCategory(@RequestBody CategoryCreateDto categoryDto) {
        String message = adminService.addCategory(categoryDto);
        return ResponseEntity.ok(message);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateCategory(@PathVariable Long id, @RequestBody CategoryUpdateDto categoryUpdateDto) {
        String message = adminService.updateCategory(id, categoryUpdateDto);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        String message = adminService.deleteCategory(id);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/icon/{id}")
    public ResponseEntity<byte[]> getIcon(@PathVariable Long id) {
        TransactionCategory category = categoryRepository.findById(id).orElseThrow(() -> new HttpException(404, "Category not found"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(category.getIcon().length);

        return new ResponseEntity<>(category.getIcon(), headers, HttpStatus.OK);
    }
}
