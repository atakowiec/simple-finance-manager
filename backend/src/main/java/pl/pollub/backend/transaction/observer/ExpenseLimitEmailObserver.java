package pl.pollub.backend.transaction.observer;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import pl.pollub.backend.exception.HttpException;
import pl.pollub.backend.mail.MailService;
import pl.pollub.backend.mail.interfaces.Mail;
import pl.pollub.backend.mail.interfaces.MailSenderImplementation;
import pl.pollub.backend.mail.mails.CloseToLimitMail;
import pl.pollub.backend.mail.mails.LimitExceededMail;
import pl.pollub.backend.notification.NotificationSubscriptionService;
import pl.pollub.backend.notification.NotificationType;

/**
 * Observer that sends warning e-mails when expense limits are close or exceeded.
 */
@Component
public class ExpenseLimitEmailObserver implements ExpenseLimitObserver {
    private final MailService mailService;
    private final ExpenseLimitMailAdapter mailAdapter;
    private final NotificationSubscriptionService subscriptionService;

    public ExpenseLimitEmailObserver(
            MailService mailService,
            MailSenderImplementation mailSenderImplementation,
            NotificationSubscriptionService subscriptionService
    ) {
        this.mailService = mailService;
        this.subscriptionService = subscriptionService;

        // Prototype instances with shared sender configuration
        LimitExceededMail limitExceededPrototype = LimitExceededMail.builder()
                .sender(mailSenderImplementation)
                .build();
        CloseToLimitMail closeToLimitPrototype = CloseToLimitMail.builder()
                .sender(mailSenderImplementation)
                .build();
        this.mailAdapter = new ExpenseLimitMailAdapter(limitExceededPrototype, closeToLimitPrototype);
    }

    @Override
    public void update(ExpenseLimitEvent event) {
        if (!subscriptionService.isSubscribed(event.user(), NotificationType.EXPENSE_LIMIT_EMAIL)) {
            return;
        }

        if (!event.enteredWarning() && !event.enteredExceeded()) {
            return;
        }

        Mail mail = mailAdapter.toMail(event);
        sendMailWithErrorHandling(mail);
    }


    private void sendMailWithErrorHandling(Mail mail) {
        try {
            mailService.sendMail(mail);
        } catch (Exception e) {
            throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, "Błąd podczas wysyłania e-maila");
        }
    }
}
