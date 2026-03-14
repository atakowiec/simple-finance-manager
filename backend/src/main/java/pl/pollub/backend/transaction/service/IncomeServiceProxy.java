package pl.pollub.backend.transaction.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.categories.CategoryService;
import pl.pollub.backend.group.interfaces.GroupService;
import pl.pollub.backend.transaction.amount.AmountExpressionInterpreter;
import pl.pollub.backend.transaction.dto.TransactionCreateDto;
import pl.pollub.backend.transaction.dto.TransactionUpdateDto;
import pl.pollub.backend.transaction.factory.TransactionFactory;
import pl.pollub.backend.transaction.model.Income;
import pl.pollub.backend.transaction.model.Transaction;
import pl.pollub.backend.transaction.repository.TransactionRepository;
import pl.pollub.backend.transaction.service.interfaces.IncomeService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Proxy for IncomeService with caching for monthly stats.
 */
@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class IncomeServiceProxy implements IncomeService {

    private static final long CACHE_TTL_MS = 30_000;

    private final IncomeServiceImpl incomeService;

    private final Map<String, CacheEntry<Map<String, Double>>> byDayCache = new ConcurrentHashMap<>();
    private final Map<String, CacheEntry<Map<String, Double>>> categoryCache = new ConcurrentHashMap<>();

    @Override
    public GroupService getGroupService() {
        return incomeService.getGroupService();
    }

    @Override
    public CategoryService getCategoryService() {
        return incomeService.getCategoryService();
    }

    @Override
    public TransactionRepository<Income> getTransactionRepository() {
        return incomeService.getTransactionRepository();
    }

    @Override
    public TransactionFactory<Income> getTransactionFactory() {
        return incomeService.getTransactionFactory();
    }

    @Override
    public AmountExpressionInterpreter getAmountExpressionInterpreter() {
        return incomeService.getAmountExpressionInterpreter();
    }

    @Override
    public Income getTransactionByIdAndUserOrThrow(Long id, User user) {
        return incomeService.getTransactionByIdAndUserOrThrow(id, user);
    }

    @Override
    public List<Income> getAllTransactionsForGroup(User user, long groupId) {
        return incomeService.getAllTransactionsForGroup(user, groupId);
    }

    @Override
    public Income save(Income transaction) {
        return incomeService.save(transaction);
    }

    @Override
    public Income updateTransaction(Long id, TransactionUpdateDto updateDto, User user) {
        Long groupId = resolveGroupId(id, user);
        Income updated = incomeService.updateTransaction(id, updateDto, user);
        invalidateGroupCaches(user, groupId);
        return updated;
    }

    @Override
    public void deleteTransaction(Long id, User user) {
        Long groupId = resolveGroupId(id, user);
        incomeService.deleteTransaction(id, user);
        invalidateGroupCaches(user, groupId);
    }

    @Override
    public Map<String, Double> getThisMonthStatsByDay(User user, Long groupId) {
        return getOrLoad(byDayCache, user, groupId, () -> incomeService.getThisMonthStatsByDay(user, groupId));
    }

    @Override
    public Map<String, Double> getThisMonthCategoryStats(User user, Long groupId) {
        return getOrLoad(categoryCache, user, groupId, () -> incomeService.getThisMonthCategoryStats(user, groupId));
    }

    @Override
    public Income createTransaction(TransactionCreateDto createDto, User user) {
        Income created = incomeService.createTransaction(createDto, user);
        invalidateGroupCaches(user, createDto.getGroupId());
        return created;
    }

    @Override
    public Income cloneTransaction(Long id, User user) {
        Long groupId = resolveGroupId(id, user);
        Income cloned = incomeService.cloneTransaction(id, user);
        invalidateGroupCaches(user, groupId);
        return cloned;
    }

    private Long resolveGroupId(Long id, User user) {
        Transaction transaction = incomeService.getTransactionByIdAndUserOrThrow(id, user);
        return transaction.getGroup().getId();
    }

    private Map<String, Double> getOrLoad(
            Map<String, CacheEntry<Map<String, Double>>> cache,
            User user,
            Long groupId,
            Loader<Map<String, Double>> loader
    ) {
        String key = buildCacheKey(user, groupId);
        long now = System.currentTimeMillis();
        CacheEntry<Map<String, Double>> entry = cache.get(key);
        if (entry != null && entry.isValid(now)) {
            log.debug("Income stats cache hit key={}", key);
            return entry.value;
        }

        Map<String, Double> fresh = loader.load();
        cache.put(key, new CacheEntry<>(fresh, now + CACHE_TTL_MS));
        log.debug("Income stats cache miss -> refreshed key={}", key);
        return fresh;
    }

    private void invalidateGroupCaches(User user, Long groupId) {
        String key = buildCacheKey(user, groupId);
        byDayCache.remove(key);
        categoryCache.remove(key);
        log.info("Income stats cache invalidated key={}", key);
    }

    private String buildCacheKey(User user, Long groupId) {
        LocalDate now = LocalDate.now();
        return user.getId() + ":" + groupId + ":" + now.getYear() + "-" + now.getMonthValue();
    }

    private record CacheEntry<T>(T value, long expiresAtMs) {
        boolean isValid(long now) {
            return now < expiresAtMs;
        }
    }

    @FunctionalInterface
    private interface Loader<T> {
        T load();
    }
}
