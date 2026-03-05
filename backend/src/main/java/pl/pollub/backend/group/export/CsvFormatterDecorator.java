package pl.pollub.backend.group.export;

import pl.pollub.backend.group.dto.ImportExportDto;
import pl.pollub.backend.transaction.dto.TransactionDto;

import java.nio.charset.StandardCharsets;

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
        
        for (TransactionDto expense : data.getExpenses()) {
            csv.append(String.format("%s,%s,%.2f,EXPENSE\n", 
                expense.getDate(), expense.getName(), expense.getAmount()));
        }

        for (TransactionDto income : data.getIncomes()) {
            csv.append(String.format("%s,%s,%.2f,INCOME\n", 
                income.getDate(), income.getName(), income.getAmount()));
        }
        
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }
}
