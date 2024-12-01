package pl.pollub.backend.transaction.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pollub.backend.categories.dto.CategoryDto;
import pl.pollub.backend.transaction.model.Transaction;

import java.time.LocalDate;

/**
 * DTO for income. It holds information about income id, name, amount, category, user, group and date.
 */
@Data
@NoArgsConstructor
public class TransactionDto {
    private Long id;
    private String name;
    private Double amount;
    private CategoryDto category;
    private long userId;
    private String username;
    private long groupId;
    private LocalDate date;

    public TransactionDto(Transaction transaction) {
        this.id = transaction.getId();
        this.name = transaction.getName();
        this.amount = transaction.getAmount();
        this.category = transaction.getCategory().toDto();
        this.userId = transaction.getUser().getId();
        this.username = transaction.getUser().getUsername();
        this.groupId = transaction.getGroup().getId();
        this.date = transaction.getDate();
    }
}
