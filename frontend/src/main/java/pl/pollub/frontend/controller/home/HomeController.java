package pl.pollub.frontend.controller.home;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import pl.pollub.frontend.annotation.NavBar;
import pl.pollub.frontend.annotation.PostInitialize;
import pl.pollub.frontend.annotation.Title;
import pl.pollub.frontend.annotation.View;
import pl.pollub.frontend.controller.home.transaction.TransactionListCell;
import pl.pollub.frontend.event.EventType;
import pl.pollub.frontend.event.OnEvent;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.model.transaction.Transaction;
import pl.pollub.frontend.service.ModalService;
import pl.pollub.frontend.service.TransactionService;

@NavBar()
@Title("Strona główna")
@View(name = "home", path = "home-view.fxml")
public class HomeController {
    @FXML
    public ListView<Transaction> mainList;

    @Inject
    private TransactionService transactionService;

    @Inject
    private ModalService modalService;

    @PostInitialize
    public void postInitialize() {
        mainList.getItems().clear();
        mainList.getItems().addAll(transactionService.fetchExpenses());
        mainList.getItems().addAll(transactionService.fetchIncomes());

        mainList.getItems().sort((t1, t2) -> t2.getDate().compareTo(t1.getDate()));

        mainList.setCellFactory(param -> new TransactionListCell());
    }

    @OnEvent(EventType.TRANSACTION_UPDATE)
    public void onTransactionUpdate() {
        mainList.getItems().clear();
        mainList.getItems().addAll(transactionService.fetchExpenses());
        mainList.getItems().addAll(transactionService.fetchIncomes());

        mainList.getItems().sort((t1, t2) -> t2.getDate().compareTo(t1.getDate()));
    }

    public void openAddExpenseModal() {
        modalService.showModal("add-expense-view.fxml");
    }

    public void openAddIncomeModal() {
        modalService.showModal("add-income-view.fxml");
    }
}

