package pl.pollub.backend.expenses;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.categories.ExpenseCategory;
import pl.pollub.backend.conversion.DtoConvertible;
import pl.pollub.backend.expenses.dto.ExpenseDto;
import pl.pollub.backend.group.model.Group;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Expense implements DtoConvertible<ExpenseDto> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double amount;

    @ManyToOne
    private ExpenseCategory category;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    private LocalDate date;

    @Override
    public ExpenseDto toDto() {
        return new ExpenseDto(this);
    }
}