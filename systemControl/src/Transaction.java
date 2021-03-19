import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/*
This class is to save the data of a stock transaction.
The data will be saved for each transaction in separate and the stock will hold a list of all the transactions.
 */
public class Transaction {
    final private String dateStamp;
    final private int quantity;
    final private float turnOver; // quantity*price
    final private float price; // save the price the shares really sold for

    public Transaction(int quantity,float soldPrice){
        dateStamp = new SimpleDateFormat("HH:mm:ss:SSS").format(new Date());
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
