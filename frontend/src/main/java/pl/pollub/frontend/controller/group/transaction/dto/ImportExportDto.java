package pl.pollub.frontend.controller.group.transaction.dto;

import lombok.Data;
import pl.pollub.frontend.model.transaction.Expense;
import pl.pollub.frontend.model.transaction.Income;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ImportExportDto {
    private String createdAt = LocalDateTime.now().toString();
    private String exportedBy;

    private List<Income> incomes;
    private List<Expense> expenses;
}
