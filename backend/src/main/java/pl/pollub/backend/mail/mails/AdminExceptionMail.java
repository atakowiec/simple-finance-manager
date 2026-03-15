package pl.pollub.backend.mail.mails;

import jakarta.mail.MessagingException;
import pl.pollub.backend.exception.reporting.ExceptionReportEvent;
import pl.pollub.backend.mail.interfaces.FullHtmlMail;
import pl.pollub.backend.mail.interfaces.Mail;
import pl.pollub.backend.mail.interfaces.MailRequest;
import pl.pollub.backend.mail.interfaces.MailSenderImplementation;

/**
 * Mail sent to the administrator when a server-side exception is reported.
 */
public class AdminExceptionMail extends Mail implements FullHtmlMail {
    private final String adminEmail;
    private final ExceptionReportEvent event;

    public AdminExceptionMail(MailSenderImplementation sender, String adminEmail, ExceptionReportEvent event) {
        super(sender);
        this.adminEmail = adminEmail;
        this.event = event;
    }

    @Override
    public void send() throws MessagingException {
        sender.send(new MailRequest(getTo(), getSubject(), getHtml(), true));
    }

    @Override
    public String getHtml() {
        String exceptionMessage = event.exception().getMessage() == null ? "<no message>" : event.exception().getMessage();

        return """
                <h2>Server exception reported</h2>
                <p><b>Status:</b> %s</p>
                <p><b>Method:</b> %s</p>
                <p><b>Path:</b> %s</p>
                <p><b>Timestamp:</b> %s</p>
                <p><b>Exception type:</b> %s</p>
                <p><b>Message:</b> %s</p>
                """.formatted(
                event.status().value(),
                event.method(),
                event.path(),
                event.occurredAt(),
                event.exception().getClass().getName(),
                exceptionMessage
        );
    }

    @Override
    public String getSubject() {
        return "[FinanceManager] Server exception %s on %s %s".formatted(
                event.status().value(),
                event.method(),
                event.path()
        );
    }

    @Override
    public String getTo() {
        return adminEmail;
    }
}

