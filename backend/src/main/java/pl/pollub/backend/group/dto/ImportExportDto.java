package pl.pollub.backend.group.dto;

import lombok.Data;
import pl.pollub.backend.expenses.dto.ExpenseDto;
import pl.pollub.backend.incomes.dto.IncomeDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ImportExportDto {
    private LocalDateTime createdAt;
    private String exportedBy;

    private List<IncomeDto> incomes;
    private List<ExpenseDto> expenses;
}
