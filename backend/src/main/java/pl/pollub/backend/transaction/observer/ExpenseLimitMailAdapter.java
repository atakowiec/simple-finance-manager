package pl.pollub.backend.transaction.observer;

import pl.pollub.backend.mail.interfaces.Mail;
import pl.pollub.backend.mail.mails.CloseToLimitMail;
import pl.pollub.backend.mail.mails.LimitExceededMail;

/**
 * Adapter that turns ExpenseLimitEvent data into a concrete Mail instance.
 */
public class ExpenseLimitMailAdapter {
    private final LimitExceededMail limitExceededPrototype;
    private final CloseToLimitMail closeToLimitPrototype;

    public ExpenseLimitMailAdapter(
            LimitExceededMail limitExceededPrototype,
            CloseToLimitMail closeToLimitPrototype
    ) {
        this.limitExceededPrototype = limitExceededPrototype;
        this.closeToLimitPrototype = closeToLimitPrototype;
    }

    public Mail toMail(ExpenseLimitEvent event, Double totalExpenses, double remainingPart) {
        if (remainingPart < 0) {
            return limitExceededPrototype.clone()
                    .withUser(event.user())
                    .withGroup(event.group())
                    .withTotalExpenses(totalExpenses);
        }
        return closeToLimitPrototype.clone()
                .withUser(event.user())
                .withGroup(event.group())
                .withTotalExpenses(totalExpenses);
    }
}
