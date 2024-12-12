package pl.pollub.backend.mail.mails;

import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.mail.interfaces.FullHtmlMail;

public class CloseToLimitMail implements FullHtmlMail {
    private final User user;
    private final Group group;
    private final Double totalExpenses;

    private CloseToLimitMail(User user, Group group, Double totalExpenses) {
        this.user = user;
        this.group = group;
        this.totalExpenses = totalExpenses;
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

    public static CloseToLimitMail createFor(User user, Group group, Double totalExpenses) {
        return new CloseToLimitMail(user, group, totalExpenses);
    }
}
