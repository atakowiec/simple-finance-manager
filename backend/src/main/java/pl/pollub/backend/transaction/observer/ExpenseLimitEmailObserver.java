package pl.pollub.backend.transaction.observer;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import pl.pollub.backend.exception.HttpException;
import pl.pollub.backend.mail.MailService;
import pl.pollub.backend.mail.interfaces.Mail;
import pl.pollub.backend.mail.interfaces.MailSenderImplementation;
import pl.pollub.backend.mail.mails.CloseToLimitMail;
import pl.pollub.backend.mail.mails.ExpenseRuleTriggeredMail;
import pl.pollub.backend.mail.mails.LimitExceededMail;
import pl.pollub.backend.notification.NotificationSubscriptionService;
import pl.pollub.backend.notification.NotificationType;
import pl.pollub.backend.transaction.observer.rule.ExpenseLimitRuleAction;
import pl.pollub.backend.transaction.observer.rule.ExpenseLimitRuleInterpreter;
import pl.pollub.backend.transaction.observer.rule.ParsedExpenseLimitRule;
import pl.pollub.backend.transaction.observer.rule.RuleEvaluationContext;

/**
 * Observer that sends warning e-mails when expense limits are close or exceeded.
 */
@Component
public class ExpenseLimitEmailObserver implements ExpenseLimitObserver {
    private final MailService mailService;
    private final ExpenseLimitMailAdapter mailAdapter;
    private final NotificationSubscriptionService subscriptionService;
    private final ExpenseLimitRuleInterpreter expenseLimitRuleInterpreter;
    private final ExpenseRuleTriggeredMail interpretedRulePrototype;

    public ExpenseLimitEmailObserver(
            MailService mailService,
            MailSenderImplementation mailSenderImplementation,
            NotificationSubscriptionService subscriptionService,
            ExpenseLimitRuleInterpreter expenseLimitRuleInterpreter
    ) {
        this.mailService = mailService;
        this.subscriptionService = subscriptionService;
        this.expenseLimitRuleInterpreter = expenseLimitRuleInterpreter;

        // Prototype instances with shared sender configuration
        LimitExceededMail limitExceededPrototype = LimitExceededMail.builder()
                .sender(mailSenderImplementation)
                .build();
        CloseToLimitMail closeToLimitPrototype = CloseToLimitMail.builder()
                .sender(mailSenderImplementation)
                .build();
        this.mailAdapter = new ExpenseLimitMailAdapter(limitExceededPrototype, closeToLimitPrototype);

        this.interpretedRulePrototype = ExpenseRuleTriggeredMail.builder()
                .sender(mailSenderImplementation)
                .build();
    }

    @Override
    public void update(ExpenseLimitEvent event) {
        if (subscriptionService.isSubscribed(event.user(), NotificationType.EXPENSE_LIMIT_EMAIL)
                && (event.enteredWarning() || event.enteredExceeded())) {
            Mail mail = mailAdapter.toMail(event);
            sendMailWithErrorHandling(mail);
        }

        evaluateInterpretedRule(event);
    }

    private void evaluateInterpretedRule(ExpenseLimitEvent event) {
        String ruleText = event.group().getExpenseLimitRule();
        if (ruleText == null || ruleText.isBlank()) {
            return;
        }

        ParsedExpenseLimitRule parsedRule;
        try {
            parsedRule = expenseLimitRuleInterpreter.parse(ruleText);
        } catch (HttpException exception) {
            return;
        }

        RuleEvaluationContext context = new RuleEvaluationContext(event.totalExpenses(), event.totalIncomes());
        if (!parsedRule.matches(context)) {
            return;
        }

        pl.pollub.backend.auth.user.User recipient = resolveRecipient(event, parsedRule.action());
        if (recipient == null || !subscriptionService.isSubscribed(recipient, NotificationType.EXPENSE_LIMIT_EMAIL)) {
            return;
        }

        Mail interpretedMail = interpretedRulePrototype.clone()
                .withRecipient(recipient)
                .withGroup(event.group())
                .withRule(ruleText)
                .withTotalExpenses(event.totalExpenses())
                .withTotalIncomes(event.totalIncomes());

        sendMailWithErrorHandling(interpretedMail);
    }

    private pl.pollub.backend.auth.user.User resolveRecipient(ExpenseLimitEvent event, ExpenseLimitRuleAction action) {
        if (action == ExpenseLimitRuleAction.EMAIL_OWNER) {
            return event.group().getOwner();
        }

        if (action == ExpenseLimitRuleAction.EMAIL_MEMBER
                && event.triggerSource() == ExpenseLimitTriggerSource.EXPENSE_CREATED) {
            return event.user();
        }

        return null;
    }


    private void sendMailWithErrorHandling(Mail mail) {
        try {
            mailService.sendMail(mail);
        } catch (Exception e) {
            throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, "Błąd podczas wysyłania e-maila");
        }
    }
}
