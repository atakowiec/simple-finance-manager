package pl.pollub.backend.transaction.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.transaction.amount.AmountExpression;
import pl.pollub.backend.transaction.amount.AmountExpressionComplexityVisitor;
import pl.pollub.backend.transaction.amount.AmountExpressionInterpreter;
import pl.pollub.backend.transaction.amount.AmountExpressionPrettyPrintVisitor;
import pl.pollub.backend.transaction.dto.TransactionCreateDto;
import pl.pollub.backend.transaction.dto.TransactionUpdateDto;
import pl.pollub.backend.transaction.query.Expression;
import pl.pollub.backend.transaction.query.ExpressionComplexityVisitor;
import pl.pollub.backend.transaction.query.ExpressionPrettyPrintVisitor;
import pl.pollub.backend.transaction.query.TransactionQueryParser;
import pl.pollub.backend.transaction.model.Transaction;
import pl.pollub.backend.transaction.query.TransactionQueryInterpreter;
import pl.pollub.backend.transaction.service.interfaces.TransactionService;

import java.util.List;
import java.util.Map;

public abstract class TransactionController<T extends Transaction> {
    @Autowired
    private TransactionQueryInterpreter queryInterpreter;

    @Autowired
    private TransactionQueryParser queryParser;

    @Autowired
    private AmountExpressionInterpreter amountExpressionInterpreter;

    public abstract TransactionService<T> getTransactionService();

    @Operation(summary = "Pobierz wszystkie transakcje danego typu dla grupy")
    @ApiResponse(responseCode = "200", description = "Lista transacji")
    @GetMapping("/{groupId}")
    public List<T> getTransactionsByGroupId(@PathVariable Long groupId, @AuthenticationPrincipal User user) {
        return getTransactionService().getAllTransactionsForGroup(user, groupId);
    }

    @Operation(summary = "Wyszukaj transakcje za pomocą Transaction Query Language")
    @ApiResponse(responseCode = "200", description = "Przefiltrowane transakcje")
    @GetMapping("/{groupId}/query")
    public List<T> queryTransactions(
            @PathVariable Long groupId,
            @RequestParam String query,
            @AuthenticationPrincipal User user) {
        List<T> allTransactions = getTransactionService().getAllTransactionsForGroup(user, groupId);
        return queryInterpreter.filter(allTransactions, query);
    }

    @Operation(summary = "Zwróć debugowy widok drzewa zapytania")
    @ApiResponse(responseCode = "200", description = "Sformatowane zapytanie i metryki AST")
    @GetMapping("/query/inspect")
    public Map<String, Object> inspectQueryAst(@RequestParam String query) {
        Expression expression = queryParser.parse(query);
        return Map.of(
                "pretty", expression.accept(new ExpressionPrettyPrintVisitor()),
                "complexity", expression.accept(new ExpressionComplexityVisitor())
        );
    }

    @Operation(summary = "Zwróć debugowy widok drzewa wyrażenia kwoty")
    @ApiResponse(responseCode = "200", description = "Sformatowane wyrażenie i metryki AST")
    @GetMapping("/amount/inspect")
    public Map<String, Object> inspectAmountAst(@RequestParam String expression) {
        AmountExpression amountExpression = amountExpressionInterpreter.parse(expression);
        return Map.of(
                "pretty", amountExpression.accept(new AmountExpressionPrettyPrintVisitor()),
                "complexity", amountExpression.accept(new AmountExpressionComplexityVisitor()),
                "result", amountExpression.interpret()
        );
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

    // start L1 prototyp
    @Operation(summary = "Klonuj transakcje danego typu")
    @ApiResponse(responseCode = "201", description = "Sklonowano transakcje")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{id}/clone")
    public T cloneTransaction(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return getTransactionService().cloneTransaction(id, user);
    }
}

