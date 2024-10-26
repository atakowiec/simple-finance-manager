package pl.pollub.frontend.model.transaction;

public class Income extends Transaction {

    @Override
    public String getAmountWithSign() {
        return "+" + this.getAmountFormatted();
    }

    @Override
    public String getAmountStyleClass() {
        return "income";
    }
}
