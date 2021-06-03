import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import java.util.Stack;

public class UserHoldings {
    private final String symbol;
    private final Stock stock;
    private int quantity;
    private int freeShares;
    private Stack<Float> totalHold;
    private float sharePrice;

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.totalHold.push(quantity*sharePrice);
        //this.totalHold = quantity*stock.getSharePrice(); TODO:! // the total hold needs to be updated according to the new quantity
    }

/*    *//**
     *
     * @return the total holds the user have of this stock.
     * the function is updating the total holdings according to the current share price.
     *//*
    public float getTotalHold() {
        totalHold = stock.getSharePrice() *quantity;
        return totalHold;
    }*/ //TODO:!

    public float getTotalHold() {
        return this.totalHold.peek().floatValue();
    }

    public void setTotalHold(float totalHold) {
        //this.totalHold = totalHold; TODO:!
        this.totalHold.push(totalHold);
    }

    public float getSharePrice() {
        return sharePrice;
    }

    public void setSharePrice(float sharePrice) {
        this.sharePrice = sharePrice;
    }


    public UserHoldings(String symbol, Stock stock, int quantity) {
        this.symbol = symbol;
        this.stock = stock;
        this.freeShares = this.quantity = quantity;
        this.sharePrice = stock.getSharePrice();
        totalHold = new Stack<>();
        totalHold.push(sharePrice*quantity);
        //totalHold = sharePrice*quantity; TODO:!

        stock.getSharePriceProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                updateTotalHold(newValue.floatValue());
            }
        });
    }

    private void updateTotalHold(float floatValue) {
        this.sharePrice = floatValue;
        this.totalHold.push(this.sharePrice*this.quantity);
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
