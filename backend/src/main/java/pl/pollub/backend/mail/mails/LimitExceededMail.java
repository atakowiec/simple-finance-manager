package pl.pollub.backend.mail.mails;

import jakarta.mail.MessagingException;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.mail.adapter.UserContactAdapter;
import pl.pollub.backend.mail.interfaces.Contact;
import pl.pollub.backend.mail.interfaces.FullHtmlMail;
import pl.pollub.backend.mail.interfaces.Mail;
import pl.pollub.backend.mail.interfaces.MailSenderImplementation;

/**
 * Refined Abstraction in the Bridge design pattern.
 */
public class LimitExceededMail extends Mail implements FullHtmlMail {
    private final Contact contact;
    private final Group group;
    private final Double totalExpenses;

    private LimitExceededMail(Builder builder) {
        super(builder.sender);
        this.contact = builder.contact;
        this.group = builder.group;
        this.totalExpenses = builder.totalExpenses;
    }

    public static class Builder {
        private MailSenderImplementation sender;
        private Contact contact;
        private Group group;
        private Double totalExpenses;

        public Builder sender(MailSenderImplementation sender) {
            this.sender = sender;
            return this;
        }

        public Builder user(User user) {
            this.contact = new UserContactAdapter(user);
            return this;
        }

        public Builder group(Group group) {
            this.group = group;
            return this;
        }

        public Builder totalExpenses(Double totalExpenses) {
            this.totalExpenses = totalExpenses;
            return this;
        }

        public LimitExceededMail build() {
            return new LimitExceededMail(this);
        }
    }

    // start L1 builder
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void send() throws MessagingException {
        sender.send(getTo(), getSubject(), getHtml(), true);
    }

    @Override
    public String getHtml() {
        return String.format("<p>Witaj %s!</p><p>W grupie %s przekroczono limit. <br><b>Wydano %.2f, a limit wynosi %.2f</b></p>",
                contact.getDisplayName(),
                group.getName(),
                totalExpenses,
                group.getExpenseLimit());
    }

    @Override
    public String getSubject() {
        return String.format("Przekroczono limit w grupie %s", group.getName());
    }

    @Override
    public String getTo() {
        return contact.getEmailAddress();
    }
}
