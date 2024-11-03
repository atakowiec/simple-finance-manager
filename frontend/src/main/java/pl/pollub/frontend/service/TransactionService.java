package pl.pollub.frontend.service;

import com.google.gson.reflect.TypeToken;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.injector.Injectable;
import pl.pollub.frontend.model.transaction.Expense;
import pl.pollub.frontend.model.transaction.Income;
import pl.pollub.frontend.model.transaction.TransactionCategory;
import pl.pollub.frontend.util.JsonUtil;
import pl.pollub.frontend.util.SimpleJsonBuilder;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;

@Injectable
public class TransactionService {
    @Inject
    private HttpService httpService;

    public HttpResponse<String> addExpense(String name, double amount, TransactionCategory category, LocalDate date) {
        String body = SimpleJsonBuilder.empty()
                .add("name", name)
                .add("amount", amount)
                .add("categoryId", category.getId())
                .add("date", date.toString())
                .toJson();

        return httpService.post("/expenses", body);
    }

    public List<Expense> fetchExpenses() {
        HttpResponse<String> response = httpService.get("/expenses");
        Type type = new TypeToken<List<Expense>>() {}.getType();
        return JsonUtil.GSON.fromJson(response.body(), type);
    }

    public List<Income> fetchIncomes() {
        HttpResponse<String> response = httpService.get("/incomes");
        Type type = new TypeToken<List<Income>>() {}.getType();
        return JsonUtil.GSON.fromJson(response.body(), type);
    }

    public HttpResponse<String> addIncome(String name, double amount, TransactionCategory category, LocalDate date) {
        String body = SimpleJsonBuilder.empty()
                .add("name", name)
                .add("amount", amount)
                .add("categoryId", category.getId())
                .add("date", date.toString())
                .toJson();

        return httpService.post("/incomes", body);
    }
}