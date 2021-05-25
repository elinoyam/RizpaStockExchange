public class UserHoldings {
    private final String symbol;
    private final Stock stock;
    private int quantity;
    private float totalHold;
    private float sharePrice;

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getTotalHold() {
        return totalHold;
    }

    public void setTotalHold(float totalHold) {
        this.totalHold = totalHold;
    }

    public float getSharePrice() {
        return sharePrice;
    }

    public void setSharePrice(float sharePrice) {
        this.sharePrice = sharePrice;
    }



    public UserHoldings(String symbol, Stock stock, int quantity, float sharePrice) {
        this.symbol = symbol;
        this.stock = stock;
        this.quantity = quantity;
        this.sharePrice = sharePrice;
        totalHold = sharePrice*quantity;
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
