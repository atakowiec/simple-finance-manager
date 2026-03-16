package pl.pollub.backend.mail.mails;

import jakarta.mail.MessagingException;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.mail.interfaces.FullHtmlMail;
import pl.pollub.backend.mail.interfaces.Mail;
import pl.pollub.backend.mail.interfaces.MailRequest;
import pl.pollub.backend.mail.interfaces.MailSenderImplementation;
import pl.pollub.backend.util.CurrencyFormatter;

// start prototype
/**
 * Mail sent when interpreted group expense rule is matched.
 */
public class ExpenseRuleTriggeredMail extends Mail implements FullHtmlMail, Cloneable {
    private User recipient;
    private Group group;
    private String rule;
    private Double totalExpenses;
    private Double totalIncomes;

    private ExpenseRuleTriggeredMail(Builder builder) {
        super(builder.sender);
        this.recipient = builder.recipient;
        this.group = builder.group;
        this.rule = builder.rule;
        this.totalExpenses = builder.totalExpenses;
        this.totalIncomes = builder.totalIncomes;
    }

    // start builder
    public static class Builder {
        private MailSenderImplementation sender;
        private User recipient;
        private Group group;
        private String rule;
        private Double totalExpenses;
        private Double totalIncomes;

        public Builder sender(MailSenderImplementation sender) {
            this.sender = sender;
            return this;
        }

        public Builder recipient(User recipient) {
            this.recipient = recipient;
            return this;
        }

        public Builder group(Group group) {
            this.group = group;
            return this;
        }

        public Builder rule(String rule) {
            this.rule = rule;
            return this;
        }

        public Builder totalExpenses(Double totalExpenses) {
            this.totalExpenses = totalExpenses;
            return this;
        }

        public Builder totalIncomes(Double totalIncomes) {
            this.totalIncomes = totalIncomes;
            return this;
        }

        public ExpenseRuleTriggeredMail build() {
            return new ExpenseRuleTriggeredMail(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public ExpenseRuleTriggeredMail withRecipient(User recipient) {
        this.recipient = recipient;
        return this;
    }

    public ExpenseRuleTriggeredMail withGroup(Group group) {
        this.group = group;
        return this;
    }

    public ExpenseRuleTriggeredMail withRule(String rule) {
        this.rule = rule;
        return this;
    }

    public ExpenseRuleTriggeredMail withTotalExpenses(Double totalExpenses) {
        this.totalExpenses = totalExpenses;
        return this;
    }

    public ExpenseRuleTriggeredMail withTotalIncomes(Double totalIncomes) {
        this.totalIncomes = totalIncomes;
        return this;
    }

    @Override
    public ExpenseRuleTriggeredMail clone() {
        try {
            return (ExpenseRuleTriggeredMail) super.clone();
        } catch (CloneNotSupportedException exception) {
            throw new RuntimeException("Cloning mail failed", exception);
        }
    }

    @Override
    public void send() throws MessagingException {
        sender.send(new MailRequest(getTo(), getSubject(), getHtml(), true));
    }

    @Override
    public String getHtml() {
        CurrencyFormatter formatter = CurrencyFormatter.getInstance();
        return String.format(
                "<p>Witaj %s!</p><p>W grupie <b>%s</b> została uruchomiona reguła:</p><p><code>%s</code></p><p>Wydatki: <b>%s</b><br>Dochody: <b>%s</b></p>",
                recipient.getUsername(),
                group.getName(),
                rule,
                formatter.format(totalExpenses),
                formatter.format(totalIncomes)
        );
    }

    @Override
    public String getSubject() {
        return String.format("Reguła limitu w grupie %s została uruchomiona", group.getName());
    }

    @Override
    public String getTo() {
        return recipient.getEmail();
    }
}

