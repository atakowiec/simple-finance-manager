package pl.pollub.backend.transaction.observer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.pollub.backend.activity.ActivityEventData;
import pl.pollub.backend.activity.ActivityEventType;
import pl.pollub.backend.activity.ActivityMediator;

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
    private final ActivityMediator activityMediator;

    @Override
    public void update(ExpenseLimitEvent event) {
        if (!event.enteredExceeded()) {
            return;
        }

        String details = String.format("total: $%.2f, limit: $%.2f", event.totalExpenses(), event.group().getExpenseLimit());

        activityMediator.notify(
                ActivityEventType.BUDGET_EXCEEDED,
                ActivityEventData.newBuilder()
                        .user(event.user())
                        .group(event.group())
                        .amount(event.totalExpenses())
                        .additionalInfo(details)
                        .build()
        );
    }
}

