public class UserHoldings {
    private final String symbol;
    private final Stock stock;
    private int quantity;
    private int freeShares;
    private float totalHold;
    private float sharePrice;

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.totalHold = quantity*stock.getSharePrice(); // the total hold needs to be updated according to the new quantity
    }

    /**
     *
     * @return the total holds the user have of this stock.
     * the function is updating the total holdings according to the current share price.
     */
    public float getTotalHold() {
        totalHold = stock.getSharePrice() *quantity;
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
        this.freeShares = this.quantity = quantity;
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

    public int getFreeShares() {
        return freeShares;
    }

    public void setFreeShares(int freeShares) {
        this.freeShares = freeShares;
    }
}
