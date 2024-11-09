package pl.pollub.backend.incomes;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.categories.model.TransactionCategory;
import pl.pollub.backend.conversion.DtoConvertible;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.incomes.dto.IncomeDto;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Income implements DtoConvertible<IncomeDto> {
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
    public IncomeDto toDto() {
        return new IncomeDto(this);
    }
}