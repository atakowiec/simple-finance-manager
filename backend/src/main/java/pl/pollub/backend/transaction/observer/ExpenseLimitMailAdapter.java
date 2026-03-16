package pl.pollub.backend.transaction.observer;

import pl.pollub.backend.mail.interfaces.Mail;
import pl.pollub.backend.mail.mails.CloseToLimitMail;
import pl.pollub.backend.mail.mails.LimitExceededMail;

// start adapter
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

    public Mail toMail(ExpenseLimitEvent event) {
        if (event.enteredExceeded()) {
            return limitExceededPrototype.clone()
                    .withUser(event.user())
                    .withGroup(event.group())
                    .withTotalExpenses(event.totalExpenses());
        }
        return closeToLimitPrototype.clone()
                .withUser(event.user())
                .withGroup(event.group())
                .withTotalExpenses(event.totalExpenses());
    }
}
