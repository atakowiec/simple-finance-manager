package pl.pollub.backend.mail.mails;

import jakarta.mail.MessagingException;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.mail.adapter.UserContactAdapter;
import pl.pollub.backend.mail.interfaces.Contact;
import pl.pollub.backend.mail.interfaces.FullHtmlMail;
import pl.pollub.backend.mail.interfaces.Mail;
import pl.pollub.backend.mail.interfaces.MailRequest;
import pl.pollub.backend.mail.interfaces.MailSenderImplementation;
import pl.pollub.backend.util.CurrencyFormatter;

// start builder
/**
 * Refined Abstraction in the Bridge design pattern.
 */
public class LimitExceededMail extends Mail implements FullHtmlMail, Cloneable {
    private Contact contact;
    private Group group;
    private Double totalExpenses;

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

    public LimitExceededMail withUser(User user) {
        this.contact = new UserContactAdapter(user);
        return this;
    }

    public LimitExceededMail withGroup(Group group) {
        this.group = group;
        return this;
    }

    public LimitExceededMail withTotalExpenses(Double totalExpenses) {
        this.totalExpenses = totalExpenses;
        return this;
    }

    @Override
    public LimitExceededMail clone() {
        try {
            return (LimitExceededMail) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Cloning mail failed", e);
        }
    }

    @Override
    public void send() throws MessagingException {
        sender.send(new MailRequest(getTo(), getSubject(), getHtml(), true));
    }

    @Override
    public String getHtml() {
        CurrencyFormatter formatter = CurrencyFormatter.getInstance();
        String totalFormatted = formatter.format(totalExpenses);
        String limitFormatted = formatter.format(group.getExpenseLimit());
        return String.format("<p>Witaj %s!</p><p>W grupie %s przekroczono limit. <br><b>Wydano %s, a limit wynosi %s</b></p>",
                contact.getDisplayName(),
                group.getName(),
                totalFormatted,
                limitFormatted);
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
