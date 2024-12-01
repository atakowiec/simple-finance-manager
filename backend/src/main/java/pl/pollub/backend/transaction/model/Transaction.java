package pl.pollub.backend.transaction.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.categories.model.TransactionCategory;
import pl.pollub.backend.conversion.DtoConvertible;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.transaction.dto.TransactionDto;

import java.time.LocalDate;

/**
 * Base class for all transactions.
 */
@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction implements DtoConvertible<TransactionDto> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double amount;

    @ManyToOne
    private TransactionCategory category;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    private LocalDate date;

    @Override
    public TransactionDto toDto() {
        return new TransactionDto(this);
    }
}