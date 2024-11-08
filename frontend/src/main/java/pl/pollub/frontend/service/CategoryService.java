package pl.pollub.frontend.service;

import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import pl.pollub.frontend.annotation.PostInitialize;
import pl.pollub.frontend.event.EventType;
import pl.pollub.frontend.event.OnEvent;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.injector.Injectable;
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
    @OnEvent(EventType.CATEGORIES_UPDATE)
    public void postInitialize() {
        List<TransactionCategory> categories = fetchCategories();
        expenseCategories.clear();
        incomeCategories.clear();

        for (TransactionCategory category : categories) {
            switch (category.getCategoryType()) {
                case EXPENSE:
                    expenseCategories.add(category);
                    break;
                case INCOME:
                    incomeCategories.add(category);
                    break;
                default:
                    throw new RuntimeException("Unknown category type: " + category.getCategoryType());
            }
        }
    }

    private List<TransactionCategory> fetchCategories() {
        HttpResponse<String> response = httpService.get("/categories");

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to fetch categories");
        }

        Type type = new TypeToken<List<TransactionCategory>>() {
            // empty
        }.getType();

        return JsonUtil.GSON.fromJson(response.body(), type);
    }

    public TransactionCategory getExpenseCategoryById(int id) {
        for (TransactionCategory expenseCategory : expenseCategories) {
            if (expenseCategory.getId() == id) {
                return expenseCategory;
            }
        }

        throw new RuntimeException("Category with id: " + id + " not found");
    }

    public List<TransactionCategory> getCategories(Transaction transaction) {
        return switch (transaction.getCategory().getCategoryType()) {
            case EXPENSE -> expenseCategories;
            case INCOME -> incomeCategories;
        };
    }
}
