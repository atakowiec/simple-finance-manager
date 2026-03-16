package pl.pollub.backend.transaction.observer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.group.repository.GroupRepository;
import pl.pollub.backend.transaction.observer.state.BudgetStatusContext;
import pl.pollub.backend.transaction.observer.state.ExpenseLimitLifecycleStatus;
import pl.pollub.backend.transaction.observer.state.ExpenseLimitMetrics;
import pl.pollub.backend.transaction.observer.state.ExpenseLimitState;
import pl.pollub.backend.transaction.observer.state.ExpenseLimitTransition;
import pl.pollub.backend.transaction.observer.state.WithinLimitState;
import pl.pollub.backend.transaction.repository.ExpenseRepository;
import pl.pollub.backend.transaction.repository.IncomeRepository;

import java.time.LocalDate;

/**
 * Evaluates the monthly budget lifecycle using the State pattern and publishes rich observer events.
 */
@Service
@RequiredArgsConstructor
public class ExpenseLimitLifecycleService {
    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;
    private final GroupRepository groupRepository;
    private final ExpenseLimitSubject expenseLimitSubject;

    public ExpenseLimitEvent evaluateAndNotify(User user, Group group, ExpenseLimitTriggerSource triggerSource) {
        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        ExpenseLimitState initialState = resolveInitialState(group, monthStart);
        ExpenseLimitMetrics metrics = new ExpenseLimitMetrics(resolveTotalExpenses(group, monthStart), group.getExpenseLimit());
        BudgetStatusContext context = new BudgetStatusContext(initialState);
        ExpenseLimitTransition transition = context.transition(metrics);

        persistGroupLifecycle(group, transition.currentState(), monthStart);

        ExpenseLimitEvent event = new ExpenseLimitEvent(
                user,
                group,
                transition.previousState(),
                transition.currentState(),
                transition.transitioned(),
                metrics.totalExpenses(),
                resolveTotalIncomes(group, monthStart),
                monthStart,
                triggerSource
        );
        expenseLimitSubject.notifyObservers(event);
        return event;
    }

    private ExpenseLimitState resolveInitialState(Group group, LocalDate monthStart) {
        if (group.getExpenseLimitStateMonthStart() == null || !monthStart.equals(group.getExpenseLimitStateMonthStart())) {
            return WithinLimitState.INSTANCE.determineNextState(new ExpenseLimitMetrics(0.0, group.getExpenseLimit()));
        }

        return ExpenseLimitState.fromStatus(group.getExpenseLimitLifecycleStatus());
    }

    private double resolveTotalExpenses(Group group, LocalDate monthStart) {
        Double totalExpenses = expenseRepository.getTotalByGroupAndMinDate(group, monthStart);
        return totalExpenses != null ? totalExpenses : 0.0;
    }

    private double resolveTotalIncomes(Group group, LocalDate monthStart) {
        Double totalIncomes = incomeRepository.getTotalByGroupAndMinDate(group, monthStart);
        return totalIncomes != null ? totalIncomes : 0.0;
    }

    private void persistGroupLifecycle(Group group, ExpenseLimitLifecycleStatus currentState, LocalDate monthStart) {
        boolean requiresSave = group.getExpenseLimitLifecycleStatus() != currentState
                || group.getExpenseLimitStateMonthStart() == null
                || !monthStart.equals(group.getExpenseLimitStateMonthStart());

        if (!requiresSave) {
            return;
        }

        group.setExpenseLimitLifecycleStatus(currentState);
        group.setExpenseLimitStateMonthStart(monthStart);
        groupRepository.save(group);
    }
}

