package pl.pollub.backend.transaction.observer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.pollub.backend.activity.ActivityEventData;
import pl.pollub.backend.activity.ActivityEventType;
import pl.pollub.backend.activity.ActivityMediator;
import pl.pollub.backend.config.constants.ExpenseLimitConstants;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.transaction.repository.ExpenseRepository;

import java.time.LocalDate;

/**
 * Observer that fires a {@link ActivityEventType#BUDGET_EXCEEDED} event through the
 * {@link ActivityMediator} whenever the monthly expense total crosses the group limit.
 *
 * <p>This class bridges the existing Observer pattern (expense-limit notifications)
 * with the Mediator pattern (centralised activity logging), keeping both patterns
 * independent and composable.</p>
 */
@Component
@RequiredArgsConstructor
public class ExpenseLimitActivityObserver implements ExpenseLimitObserver {

    private final ExpenseRepository expenseRepository;
    private final ActivityMediator activityMediator;

    @Override
    public void update(ExpenseLimitEvent event) {
        Group group = event.group();

        if (group.getExpenseLimit() <= ExpenseLimitConstants.NO_EXPENSE_LIMIT) {
            return; // no limit configured
        }

        Double totalExpenses = fetchMonthlyTotal(group);
        double limit = group.getExpenseLimit();

        if (totalExpenses <= limit) {
            return; // within budget — nothing to log
        }

        String details = String.format("total: $%.2f, limit: $%.2f", totalExpenses, limit);

        activityMediator.notify(
                ActivityEventType.BUDGET_EXCEEDED,
                ActivityEventData.builder()
                        .user(event.user())
                        .group(group)
                        .amount(totalExpenses)
                        .additionalInfo(details)
                        .build()
        );
    }

    private Double fetchMonthlyTotal(Group group) {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        Double total = expenseRepository.getTotalByGroupAndMinDate(group, startOfMonth);
        return total != null ? total : 0.0;
    }
}

