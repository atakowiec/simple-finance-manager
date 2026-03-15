package pl.pollub.backend.transaction.observer;

import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.transaction.observer.state.ExpenseLimitLifecycleStatus;

import java.time.LocalDate;

/**
 * Event payload published when expense-limit notifications should be evaluated.
 */
public record ExpenseLimitEvent(
		User user,
		Group group,
		ExpenseLimitLifecycleStatus previousState,
		ExpenseLimitLifecycleStatus currentState,
		boolean transitioned,
		double totalExpenses,
		LocalDate monthStart,
		ExpenseLimitTriggerSource triggerSource
) {
	public boolean enteredWarning() {
		return transitioned && currentState == ExpenseLimitLifecycleStatus.NEAR_LIMIT;
	}

	public boolean enteredExceeded() {
		return transitioned && currentState == ExpenseLimitLifecycleStatus.EXCEEDED;
	}

	public boolean recoveredToSafeState() {
		return transitioned
				&& (previousState == ExpenseLimitLifecycleStatus.NEAR_LIMIT || previousState == ExpenseLimitLifecycleStatus.EXCEEDED)
				&& currentState == ExpenseLimitLifecycleStatus.WITHIN_LIMIT;
	}
}

