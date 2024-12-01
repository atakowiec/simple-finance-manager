package pl.pollub.backend.group.dto;

import lombok.Data;
import pl.pollub.backend.transaction.dto.TransactionDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for import/export. It holds all incomes and expenses needef for import/export.
 */
@Data
public class ImportExportDto {
    private LocalDateTime createdAt;
    private String exportedBy;

    private List<TransactionDto> incomes;
    private List<TransactionDto> expenses;
}
