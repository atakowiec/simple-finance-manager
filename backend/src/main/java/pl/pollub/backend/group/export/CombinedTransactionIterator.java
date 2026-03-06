package pl.pollub.backend.group.export;

import pl.pollub.backend.transaction.dto.TransactionDto;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

// start iterator
/**
 * Iterates over two transaction lists in sequence: expenses first, then incomes.
 */
public class CombinedTransactionIterator implements Iterator<TransactionDto> {
    private final List<TransactionDto> expenses;
    private final List<TransactionDto> incomes;
    private int expenseIndex = 0;
    private int incomeIndex = 0;

    public CombinedTransactionIterator(List<TransactionDto> expenses, List<TransactionDto> incomes) {
        this.expenses = expenses != null ? expenses : Collections.emptyList();
        this.incomes = incomes != null ? incomes : Collections.emptyList();
    }

    @Override
    public boolean hasNext() {
        return expenseIndex < expenses.size() || incomeIndex < incomes.size();
    }

    @Override
    public TransactionDto next() {
        if (expenseIndex < expenses.size()) {
            return expenses.get(expenseIndex++);
        }

        if (incomeIndex < incomes.size()) {
            return incomes.get(incomeIndex++);
        }

        throw new NoSuchElementException("No more transactions available");
    }
}

