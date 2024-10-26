package pl.pollub.frontend.controller.home;

import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import pl.pollub.frontend.annotation.NavBar;
import pl.pollub.frontend.annotation.PostInitialize;
import pl.pollub.frontend.annotation.Title;
import pl.pollub.frontend.annotation.View;
import pl.pollub.frontend.controller.home.transaction.TransactionListCell;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.model.transaction.Expense;
import pl.pollub.frontend.model.transaction.Income;
import pl.pollub.frontend.model.transaction.Transaction;
import pl.pollub.frontend.service.HttpService;
import pl.pollub.frontend.util.JsonUtil;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.List;

@NavBar()
@Title("Strona główna")
@View(name = "home", path = "home-view.fxml")
public class HomeController {
    @FXML
    public ListView<Transaction> mainList;

    @Inject
    private HttpService httpService;

    @PostInitialize
    public void postInitialize() {
        mainList.getItems().clear();
        mainList.getItems().addAll(fetchExpenses());
        mainList.getItems().addAll(fetchIncomes());

        mainList.getItems().sort((t1, t2) -> t2.getDate().compareTo(t1.getDate()));

        mainList.setCellFactory(param -> new TransactionListCell());
    }

    private List<Expense> fetchExpenses() {
        HttpResponse<String> response = httpService.get("/expenses");
        Type type = new TypeToken<List<Expense>>() {}.getType();
        return JsonUtil.GSON.fromJson(response.body(), type);
    }

    private List<Income> fetchIncomes() {
        HttpResponse<String> response = httpService.get("/incomes");
        Type type = new TypeToken<List<Income>>() {}.getType();
        return JsonUtil.GSON.fromJson(response.body(), type);
    }
}

