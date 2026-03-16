package pl.pollub.backend.transaction.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.categories.CategoryService;
import pl.pollub.backend.group.interfaces.GroupService;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.transaction.amount.AmountExpressionInterpreter;
import pl.pollub.backend.transaction.dto.TransactionCreateDto;
import pl.pollub.backend.transaction.dto.TransactionUpdateDto;
import pl.pollub.backend.transaction.factory.TransactionFactory;
import pl.pollub.backend.transaction.model.Expense;
import pl.pollub.backend.transaction.model.Transaction;
import pl.pollub.backend.transaction.repository.TransactionRepository;
import pl.pollub.backend.transaction.service.interfaces.ExpenseService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// start Proxy
/**
 * Proxy for ExpenseService with caching for monthly stats.
 */
@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class ExpenseServiceProxy implements ExpenseService {

    private static final long CACHE_TTL_MS = 30_000;

    private final ExpenseServiceImpl expenseService;

    private final Map<String, CacheEntry<Map<String, Double>>> byDayCache = new ConcurrentHashMap<>();
    private final Map<String, CacheEntry<Map<String, Double>>> categoryCache = new ConcurrentHashMap<>();

    @Override
    public GroupService getGroupService() {
        return expenseService.getGroupService();
    }

    @Override
    public CategoryService getCategoryService() {
        return expenseService.getCategoryService();
    }

    @Override
    public TransactionRepository<Expense> getTransactionRepository() {
        return expenseService.getTransactionRepository();
    }

    @Override
    public TransactionFactory<Expense> getTransactionFactory() {
        return expenseService.getTransactionFactory();
    }

    @Override
    public AmountExpressionInterpreter getAmountExpressionInterpreter() {
        return expenseService.getAmountExpressionInterpreter();
    }

    @Override
    public Expense getTransactionByIdAndUserOrThrow(Long id, User user) {
        return expenseService.getTransactionByIdAndUserOrThrow(id, user);
    }

    @Override
    public List<Expense> getAllTransactionsForGroup(User user, long groupId) {
        return expenseService.getAllTransactionsForGroup(user, groupId);
    }

    @Override
    public Expense save(Expense transaction) {
        return expenseService.save(transaction);
    }

    @Override
    public Expense updateTransaction(Long id, TransactionUpdateDto updateDto, User user) {
        Long groupId = resolveGroupId(id, user);
        Expense updated = expenseService.updateTransaction(id, updateDto, user);
        invalidateGroupCaches(user, groupId);
        return updated;
    }

    @Override
    public void deleteTransaction(Long id, User user) {
        Long groupId = resolveGroupId(id, user);
        expenseService.deleteTransaction(id, user);
        invalidateGroupCaches(user, groupId);
    }

    @Override
    public Map<String, Double> getThisMonthStatsByDay(User user, Long groupId) {
        return getOrLoad(byDayCache, user, groupId, () -> expenseService.getThisMonthStatsByDay(user, groupId));
    }

    @Override
    public Map<String, Double> getThisMonthCategoryStats(User user, Long groupId) {
        return getOrLoad(categoryCache, user, groupId, () -> expenseService.getThisMonthCategoryStats(user, groupId));
    }

    @Override
    public Expense createTransaction(TransactionCreateDto createDto, User user) {
        Expense created = expenseService.createTransaction(createDto, user);
        invalidateGroupCaches(user, createDto.getGroupId());
        return created;
    }

    @Override
    public Expense cloneTransaction(Long id, User user) {
        Long groupId = resolveGroupId(id, user);
        Expense cloned = expenseService.cloneTransaction(id, user);
        invalidateGroupCaches(user, groupId);
        return cloned;
    }

    @Override
    public void trySendLimitWarningMail(User user, Group group) {
        expenseService.trySendLimitWarningMail(user, group);
    }

    private Long resolveGroupId(Long id, User user) {
        Transaction transaction = expenseService.getTransactionByIdAndUserOrThrow(id, user);
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
            log.debug("Expense stats cache hit key={}", key);
            return entry.value;
        }

        Map<String, Double> fresh = loader.load();
        cache.put(key, new CacheEntry<>(fresh, now + CACHE_TTL_MS));
        log.debug("Expense stats cache miss -> refreshed key={}", key);
        return fresh;
    }

    private void invalidateGroupCaches(User user, Long groupId) {
        String key = buildCacheKey(user, groupId);
        byDayCache.remove(key);
        categoryCache.remove(key);
        log.info("Expense stats cache invalidated key={}", key);
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
