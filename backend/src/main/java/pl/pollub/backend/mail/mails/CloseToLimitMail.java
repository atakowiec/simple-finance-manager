package pl.pollub.backend.mail.mails;

import jakarta.mail.MessagingException;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.mail.interfaces.FullHtmlMail;
import pl.pollub.backend.mail.interfaces.Mail;
import pl.pollub.backend.mail.interfaces.MailRequest;
import pl.pollub.backend.mail.interfaces.MailSenderImplementation;

/**
 * Refined Abstraction in the Bridge design pattern.
 */
public class CloseToLimitMail extends Mail implements FullHtmlMail {
    private final User user;
    private final Group group;
    private final Double totalExpenses;

    private CloseToLimitMail(Builder builder) {
        super(builder.sender);
        this.user = builder.user;
        this.group = builder.group;
        this.totalExpenses = builder.totalExpenses;
    }

    public static class Builder {
        private MailSenderImplementation sender;
        private User user;
        private Group group;
        private Double totalExpenses;

        public Builder sender(MailSenderImplementation sender) {
            this.sender = sender;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
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

        public CloseToLimitMail build() {
            return new CloseToLimitMail(this);
        }
    }

    // start L1 builder
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void send() throws MessagingException {
        sender.send(new MailRequest(getTo(), getSubject(), getHtml(), true));
    }

    @Override
    public String getHtml() {
        return String.format("<p>Niebezpiecznie blisko limitu w grupie %s. <br><b>Wydano %.2f, a limit wynosi %.2f</b></p>",
                group.getName(),
                totalExpenses,
                group.getExpenseLimit());
    }

    @Override
    public String getSubject() {
        return String.format("Zbliżasz się do limitu w grupie %s", group.getName());
    }

    @Override
    public String getTo() {
        return user.getEmail();
    }
}
