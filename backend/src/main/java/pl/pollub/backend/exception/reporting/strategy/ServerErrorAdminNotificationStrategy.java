package pl.pollub.backend.exception.reporting.strategy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pl.pollub.backend.exception.reporting.ExceptionReportEvent;
import pl.pollub.backend.mail.MailService;
import pl.pollub.backend.mail.interfaces.Mail;
import pl.pollub.backend.mail.interfaces.MailSenderImplementation;
import pl.pollub.backend.mail.mails.AdminExceptionMail;

/**
 * Notification strategy that emails the administrator when a server error occurs.
 */
@Component
@Profile("prod")
public class ServerErrorAdminNotificationStrategy implements ExceptionNotificationStrategy {
    private final MailService mailService;
    private final MailSenderImplementation mailSenderImplementation;
    private final String adminEmail;

    public ServerErrorAdminNotificationStrategy(
            MailService mailService,
            MailSenderImplementation mailSenderImplementation,
            @Value("${exception.reporting.admin-email:admin@cieszczyk.pl}") String adminEmail
    ) {
        this.mailService = mailService;
        this.mailSenderImplementation = mailSenderImplementation;
        this.adminEmail = adminEmail;
    }

    @Override
    public boolean supports(ExceptionReportEvent event) {
        return event.isServerError();
    }

    @Override
    public void notify(ExceptionReportEvent event) {
        Mail mail = new AdminExceptionMail(mailSenderImplementation, adminEmail, event);

        try {
            mailService.sendMail(mail);
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to send admin exception mail", exception);
        }
    }
}

