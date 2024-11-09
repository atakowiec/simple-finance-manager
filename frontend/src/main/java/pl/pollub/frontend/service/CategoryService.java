package pl.pollub.frontend.service;

import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import pl.pollub.frontend.annotation.PostInitialize;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.injector.Injectable;
import pl.pollub.frontend.model.transaction.Expense;
import pl.pollub.frontend.model.transaction.Income;
import pl.pollub.frontend.model.transaction.Transaction;
import pl.pollub.frontend.model.transaction.TransactionCategory;
import pl.pollub.frontend.util.JsonUtil;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Injectable
@Getter
public class CategoryService {
    private final List<TransactionCategory> expenseCategories = new ArrayList<>();
    private final List<TransactionCategory> incomeCategories = new ArrayList<>();

    @Inject
    private HttpService httpService;

    @PostInitialize
    public void postInitialize() {
        expenseCategories.clear();
        expenseCategories.addAll(fetchCategories("expenses"));

        incomeCategories.clear();
        incomeCategories.addAll(fetchCategories("incomes"));
    }

    private List<TransactionCategory> fetchCategories(String categoryType) {
        HttpResponse<String> response = httpService.get("/categories/" + categoryType);

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to fetch categories of type: " + categoryType);
        }

        Type type = new TypeToken<List<TransactionCategory>>() {
            // empty
        }.getType();

        return JsonUtil.GSON.fromJson(response.body(), type);
    }

    public List<TransactionCategory> getCategories(Transaction transaction) {
        if (transaction instanceof Income) {
            return incomeCategories;
        } else if (transaction instanceof Expense) {
            return expenseCategories;
        }
        throw new IllegalArgumentException();
    }
}
