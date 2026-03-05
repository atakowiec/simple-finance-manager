package pl.pollub.backend.group.export;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.pollub.backend.group.dto.ImportExportDto;
import pl.pollub.backend.transaction.dto.TransactionDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class DataExporterDecoratorTest {

    private ImportExportDto data;

    @BeforeEach
    void setUp() {
        data = new ImportExportDto();
        data.setCreatedAt(LocalDateTime.now());
        data.setExportedBy("testUser");

        TransactionDto expense = new TransactionDto();
        expense.setName("Lunch");
        expense.setAmount(25.50);
        expense.setDate(LocalDate.of(2023, 10, 1));
        
        TransactionDto income = new TransactionDto();
        income.setName("Salary");
        income.setAmount(5000.00);
        income.setDate(LocalDate.of(2023, 10, 1));

        data.setExpenses(new ArrayList<>(List.of(expense)));
        data.setIncomes(new ArrayList<>(List.of(income)));
    }

    @Test
    void testBaseExporter() {
        DataExporter exporter = new BaseDataExporter();
        byte[] result = exporter.export(data);
        String resultString = new String(result, StandardCharsets.UTF_8);
        
        Assertions.assertTrue(resultString.contains("Lunch"));
        Assertions.assertTrue(resultString.contains("5000.0"));
    }

    @Test
    void testCsvDecorator() {
        DataExporter exporter = new CsvFormatterDecorator(new BaseDataExporter());
        byte[] result = exporter.export(data);
        String resultString = new String(result, StandardCharsets.UTF_8);

        Assertions.assertTrue(resultString.startsWith("Date,Name,Amount,Type"));
        Assertions.assertTrue(resultString.contains("2023-10-01,Lunch,25.50,EXPENSE"));
        Assertions.assertTrue(resultString.contains("2023-10-01,Salary,5000.00,INCOME"));
    }

    @Test
    void testJsonDecorator() {
        DataExporter exporter = new JsonFormatterDecorator(new BaseDataExporter());
        byte[] result = exporter.export(data);
        String resultString = new String(result, StandardCharsets.UTF_8);

        Assertions.assertTrue(resultString.contains("\"exportedBy\":\"testUser\""));
        Assertions.assertTrue(resultString.contains("\"name\":\"Lunch\""));
        Assertions.assertTrue(resultString.contains("\"amount\":25.5"));
    }

    @Test
    void testLayeredDecorators() {
        // Just demonstrating that we can layer them, 
        // although in this case JsonFormatterDecorator ignores whatever the base exporter did.
        DataExporter exporter = new JsonFormatterDecorator(new CsvFormatterDecorator(new BaseDataExporter()));
        byte[] result = exporter.export(data);
        String resultString = new String(result, StandardCharsets.UTF_8);

        // JsonFormatterDecorator should win as it overwrites the export
        Assertions.assertTrue(resultString.contains("\"exportedBy\":\"testUser\""));
    }
}
