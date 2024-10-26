package pl.pollub.frontend.model.transaction;

public class Expense extends Transaction {

    @Override
    public String getAmountWithSign() {
        return "-" + super.getAmountFormatted();
    }

    @Override
    public String getAmountStyleClass() {
        return "expense";
    }
}
