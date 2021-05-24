public class UserHoldings {
    private final String symbol;
    private final Stock stock;
    private int quantity;

    public UserHoldings(String symbol, Stock stock, int quantity) {
        this.symbol = symbol;
        this.stock = stock;
        this.quantity = quantity;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public Stock getStock() {
        return stock;
    }
}
