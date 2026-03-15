package pl.pollub.backend.group.export;

import pl.pollub.backend.group.dto.ImportExportDto;
import pl.pollub.backend.transaction.dto.TransactionDto;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

// start open close principle
/**
 * Concrete Decorator for CSV formatting.
 */
public class CsvFormatterDecorator extends DataFormatterDecorator {
    public CsvFormatterDecorator(DataExporter exporter) {
        super(exporter);
    }

    @Override
    public byte[] export(ImportExportDto data) {
        StringBuilder csv = new StringBuilder("Date,Name,Amount,Type\n");
        List<TransactionDto> expenses = data.getExpenses() != null ? data.getExpenses() : Collections.emptyList();
        List<TransactionDto> incomes = data.getIncomes() != null ? data.getIncomes() : Collections.emptyList();
        CombinedTransactionIterator iterator = new CombinedTransactionIterator(expenses, incomes);
        int expenseCount = expenses.size();
        int currentIndex = 0;

        while (iterator.hasNext()) {
            TransactionDto transaction = iterator.next();
            String type = currentIndex < expenseCount ? "EXPENSE" : "INCOME";

            csv.append(String.format("%s,%s,%.2f,%s\n",
                transaction.getDate(), transaction.getName(), transaction.getAmount(), type));
            currentIndex++;
        }
        
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }
}
