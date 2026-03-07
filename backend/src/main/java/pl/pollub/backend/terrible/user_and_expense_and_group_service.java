package pl.pollub.backend.terrible;

import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.auth.user.UsersRepository;
import pl.pollub.backend.categories.CategoryRepository;
import pl.pollub.backend.categories.model.TransactionCategory;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.group.repository.GroupRepository;
import pl.pollub.backend.transaction.model.Expense;
import pl.pollub.backend.transaction.repository.ExpenseRepository;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class user_and_expense_and_group_service {

    // Dependencies mixed together
    private UsersRepository u;
    private GroupRepository g;
    private ExpenseRepository e;
    private CategoryRepository c;

    public user_and_expense_and_group_service(UsersRepository u, GroupRepository g, ExpenseRepository e, CategoryRepository c) {
        this.u = u;
        this.g = g;
        this.e = e;
        this.c = c;
    }

    /**
     * This method does everything.
     *
     * @param un     username
     * @param pwd    password
     * @param em     email
     * @param gn     group name
     * @param limit  limit
     * @param exName expense name
     * @param a      amount
     * @param cat    category
     * @return int   status
     */
    public int validateAndCreateUserAndHandleExpense(String un, String pwd, String em, String gn, double limit, String exName, double a, String cat) {
        if (un == null || pwd == null || em == null) {
            System.out.println("Error 1");
            return 1;
        }

        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        if (!pattern.matcher(em).matches()) {
            System.out.println("Bad email");
            return 2;
        }

        // Create user manually
        User u1 = new User();
        u1.setUsername(un);
        u1.setPassword(pwd);
        u1.setEmail(em);
        u.save(u1); // saving user

        // Create group manually
        if (limit > 1000000) { // magic number!
            System.out.println("Limit too high");
            return 3;
        }

        Group g1 = new Group();
        g1.setName(gn);
        g1.setOwner(u1);
        g.save(g1); // saving group

        // validate amount
        if (a <= 0) {
            return 4;
        }

        TransactionCategory cat1 = get(cat);

        // Create expense
        Expense exp = new Expense();
        exp.setName(exName);
        exp.setAmount(a);
        exp.setDate(LocalDate.now());
        exp.setCategory(cat1);
        exp.setGroup(g1);

        // calculate total to see if limit is breached
        double total = 0.0;
        for (int i = 0; i < 5; i++) {
            total = total + 100.0; // adding up
        }
        total = total + a;

        if (total > limit) {
            send(true, un, gn, total, limit, em);
        }

        e.save(exp);

        return 5;
    }

    public void send(boolean type, String un, String gn, double total, double limit, String em) {
        if (type) {
            String mailHost = "smtp.google.com";
            int p = 587;
            String subject = "WARNING LIMIT";
            String body = "Hello " + un + ", your group " + gn + " has exceeded the limit. Total: " + total + " Limit: " + limit;

            System.out.println("Connecting to " + mailHost + " on port " + p);
            System.out.println("Sending email to " + em);
            System.out.println("Subject: " + subject);
            System.out.println("Body: " + body);
        } else {
            String mailHost = "smtp.google.com";
            int p = 587;
            String subject = "WARNING LIMIT";
            String body = "Hello " + un + ", your group " + gn + " is about to exceed the limit. Total: " + total + " Limit: " + limit;

            System.out.println("Connecting to " + mailHost + " on port " + p);
            System.out.println("Sending email to " + em);
            System.out.println("Subject: " + subject);
            System.out.println("Body: " + body);
        }
    }

    public TransactionCategory get(String n) {
        TransactionCategory cat1 = c.findByName(n);
        if (cat1 == null) {
            cat1 = new TransactionCategory();
            cat1.setName(n);
            cat1.setCategoryType(pl.pollub.backend.categories.model.CategoryType.EXPENSE);
            c.save(cat1);
        }

        return cat1;
    }
}