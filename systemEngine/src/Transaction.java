import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/*
This class is to save the data of a stock transaction.
The data will be saved for each transaction in separate and the stock will hold a list of all the transactions.
 */
public class Transaction {
    final static DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss:SSS");
    final private LocalDateTime dateStamp;
    //final private User buyer;
    //final private User seller;
    final private int quantity;
    final private float turnOver; // quantity*price
    final private float price; // save the price the shares really sold for

    public Transaction(int quantity,float soldPrice){
        dateStamp = LocalDateTime.now();
        this.quantity = quantity;
        this.price = soldPrice;
        this.turnOver = quantity*soldPrice;
    }

    @Override
    public String toString() {
        return "Transaction made at "+dateStamp+ " of " +quantity +" shares at the price of " + price +" for each. ";
    }

    public float getTurnOver() {
        return turnOver;
    }

    public float getPrice() { return price; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return quantity == that.quantity && Float.compare(that.price, price) == 0 && dateStamp.equals(that.dateStamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateStamp, quantity, price);
    }
}
