package pl.pollub.backend.mail.mails;

import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.mail.interfaces.FullHtmlMail;

public class CloseToLimitMail implements FullHtmlMail {
    private final User user;
    private final Group group;
    private final Double totalExpenses;

    private CloseToLimitMail(Builder builder) {
        this.user = builder.user;
        this.group = builder.group;
        this.totalExpenses = builder.totalExpenses;
    }

    public static class Builder {
        private User user;
        private Group group;
        private Double totalExpenses;

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
