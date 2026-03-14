package pl.pollub.frontend.service;

import com.google.gson.reflect.TypeToken;
import pl.pollub.frontend.controller.group.transaction.dto.ImportExportDto;
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

    public List<Expense> fetchExpenses(long id) {
        HttpResponse<String> response = httpService.get("/expenses/" + id);
        Type type = new TypeToken<List<Expense>>() {
        }.getType();

        return JsonUtil.GSON.fromJson(response.body(), type);
    }

    public List<Income> fetchIncomes(long id) {
        HttpResponse<String> response = httpService.get("/incomes/" + id);
        Type type = new TypeToken<List<Income>>() {
        }.getType();
        return JsonUtil.GSON.fromJson(response.body(), type);
    }

    public HttpResponse<String> addExpense(String name, String amount, TransactionCategory category, LocalDate date, long groupId) {
        String body = SimpleJsonBuilder.empty()
                .add("name", name)
                .add("amount", amount)
                .add("categoryId", category.getId())
                .add("date", date.toString())
                .add("groupId", groupId)
                .toJson();

        return httpService.post("/expenses", body);
    }

    public HttpResponse<String> addIncome(String name, String amount, TransactionCategory category, LocalDate date, long groupId) {
        String body = SimpleJsonBuilder.empty()
                .add("name", name)
                .add("amount", amount)
                .add("categoryId", category.getId())
                .add("date", date.toString())
                .add("groupId", groupId)
                .toJson();

        return httpService.post("/incomes", body);
    }

    public HttpResponse<String> updateIncome(Long incomeId, String name, String amount, TransactionCategory category, LocalDate date, long groupId) {
        String body = SimpleJsonBuilder.empty()
                .add("name", name)
                .add("amount", amount)
                .add("categoryId", category.getId())
                .add("date", date.toString())
                .add("groupId", groupId)
                .toJson();

        return httpService.put("/incomes/" + incomeId, body);
    }

    public HttpResponse<String> updateExpense(Long expenseId, String name, String amount, TransactionCategory category, LocalDate date, long groupId) {
        String body = SimpleJsonBuilder.empty()
                .add("name", name)
                .add("amount", amount)
                .add("categoryId", category.getId())
                .add("date", date.toString())
                .add("groupId", groupId)
                .toJson();

        return httpService.put("/expenses/" + expenseId, body);
    }

    public HttpResponse<String> deleteExpense(Long expenseId) {
        return httpService.delete("/expenses/" + expenseId);
    }

    public HttpResponse<String> deleteIncome(Long incomeId) {
        return httpService.delete("/incomes/" + incomeId);
    }

    public HttpResponse<String> importTransactions(Long groupId, ImportExportDto dto) {
        return httpService.post("/groups/" + groupId + "/import", JsonUtil.GSON.toJson(dto));
    }

    public HttpResponse<byte[]> exportTransactions(Long groupId, String format) {
        return httpService.getBytes("/groups/" + groupId + "/export?format=" + format);
    }

    public List<Expense> queryExpenses(long groupId, String query) {
        HttpResponse<String> response = httpService.get("/expenses/" + groupId + "/query?query=" + encodeQuery(query));
        Type type = new TypeToken<List<Expense>>() {
        }.getType();
        return JsonUtil.GSON.fromJson(response.body(), type);
    }

    public List<Income> queryIncomes(long groupId, String query) {
        HttpResponse<String> response = httpService.get("/incomes/" + groupId + "/query?query=" + encodeQuery(query));
        Type type = new TypeToken<List<Income>>() {
        }.getType();
        return JsonUtil.GSON.fromJson(response.body(), type);
    }

    private String encodeQuery(String query) {
        return java.net.URLEncoder.encode(query, java.nio.charset.StandardCharsets.UTF_8);
    }
}
