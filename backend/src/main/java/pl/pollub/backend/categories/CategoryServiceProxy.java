package pl.pollub.backend.categories;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import pl.pollub.backend.categories.dto.CategoryCreateDto;
import pl.pollub.backend.categories.dto.CategoryUpdateDto;
import pl.pollub.backend.categories.model.TransactionCategory;

import java.util.List;

// start Proxy
/**
 * Proxy for CategoryService that adds a simple in-memory cache.
 */
@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceProxy implements CategoryService {

    private static final long CACHE_TTL_MS = 30_000;

    private final CategoryServiceImpl categoryService;
    private final Object cacheLock = new Object();

    private volatile List<TransactionCategory> cachedCategories;
    private volatile long cacheExpiresAtMs;

    @Override
    public TransactionCategory getCategoryByIdOrThrow(Long id) {
        return categoryService.getCategoryByIdOrThrow(id);
    }

    @Override
    public String addCategory(CategoryCreateDto categoryDto) {
        String result = categoryService.addCategory(categoryDto);
        invalidateCache("add");
        return result;
    }

    @Override
    public String updateCategory(Long id, CategoryUpdateDto categoryUpdateDto) {
        String result = categoryService.updateCategory(id, categoryUpdateDto);
        invalidateCache("update");
        return result;
    }

    @Override
    public String undoCategoryChange(Long id) {
        String result = categoryService.undoCategoryChange(id);
        invalidateCache("undo");
        return result;
    }

    @Override
    public boolean canUndoCategory(Long id) {
        return categoryService.canUndoCategory(id);
    }

    @Override
    public String deleteCategory(Long id) {
        String result = categoryService.deleteCategory(id);
        invalidateCache("delete");
        return result;
    }

    @Override
    public List<TransactionCategory> getAllCategories() {
        long now = System.currentTimeMillis();
        List<TransactionCategory> current = cachedCategories;
        if (current != null && now < cacheExpiresAtMs) {
            log.debug("Category cache hit");
            return current;
        }

        synchronized (cacheLock) {
            now = System.currentTimeMillis();
            current = cachedCategories;
            if (current != null && now < cacheExpiresAtMs) {
                log.debug("Category cache hit (post-lock)");
                return current;
            }

            List<TransactionCategory> fresh = categoryService.getAllCategories();
            cachedCategories = fresh;
            cacheExpiresAtMs = now + CACHE_TTL_MS;
            log.debug("Category cache miss -> refreshed");
            return fresh;
        }
    }

    private void invalidateCache(String reason) {
        cachedCategories = null;
        cacheExpiresAtMs = 0;
        log.info("Category cache invalidated (reason={})", reason);
    }
}
