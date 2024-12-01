package pl.pollub.backend.transaction.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.transaction.dto.TransactionCreateDto;
import pl.pollub.backend.transaction.dto.TransactionUpdateDto;
import pl.pollub.backend.transaction.model.Transaction;
import pl.pollub.backend.transaction.service.interfaces.TransactionService;

import java.util.List;
import java.util.Map;

public abstract class TransactionController<T extends Transaction> {
    public abstract TransactionService<T> getTransactionService();

    @Operation(summary = "Pobierz wszystkie transakcje danego typu dla grupy")
    @ApiResponse(responseCode = "200", description = "Lista transacji")
    @GetMapping("/{groupId}")
    public List<T> getTransactionsByGroupId(@PathVariable Long groupId, @AuthenticationPrincipal User user) {
        return getTransactionService().getAllTransactionsForGroup(user, groupId);
    }

    @Operation(summary = "Stwórz nową transakcje danego typu")
    @ApiResponse(responseCode = "201", description = "Stworzono transackcje")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public T createTransaction(@Valid @RequestBody TransactionCreateDto createDto, @AuthenticationPrincipal User user) {
        return getTransactionService().createTransaction(createDto, user);
    }

    @Operation(summary = "Aktualizuj transakcje danego typu")
    @ApiResponse(responseCode = "200", description = "Zaktualizowano transakcje")
    @PutMapping("/{id}")
    public T updateExpense(@PathVariable Long id, @Valid @RequestBody TransactionUpdateDto transactionUpdateDto, @AuthenticationPrincipal User user) {
        return getTransactionService().updateTransaction(id, transactionUpdateDto, user);
    }

    @Operation(summary = "Usuń transakcje danego typu")
    @ApiResponse(responseCode = "204", description = "Usunięto transakcje")
    @DeleteMapping("/{id}")
    public void deleteExpense(@PathVariable Long id, @AuthenticationPrincipal User user) {
        getTransactionService().deleteTransaction(id, user);
    }

    @Operation(summary = "Pobierz statystyki transakcji danego typu dla grupy podzielone na dni")
    @ApiResponse(responseCode = "200", description = "Statystyki transakcji")
    @GetMapping("/{groupId}/stats/by-day")
    public Map<String, Double> getThisMonthStatsByDay(@AuthenticationPrincipal User user, @PathVariable Long groupId) {
        return getTransactionService().getThisMonthStatsByDay(user, groupId);
    }

    @Operation(summary = "Pobierz statystyki transakcji danego typu dla grupy podzielone na kategorie")
    @ApiResponse(responseCode = "200", description = "Statystyki transakcji")
    @GetMapping("/{groupId}/stats/categories")
    public Map<String, Double> getThisMonthCategoryStats(@AuthenticationPrincipal User user, @PathVariable Long groupId) {
        return getTransactionService().getThisMonthCategoryStats(user, groupId);
    }
}

