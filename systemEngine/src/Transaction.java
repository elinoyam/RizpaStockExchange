import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * A class that defines a made transaction.
 */
public class Transaction {

    /**
     * A format of showing the date and time of when the transaction was made.
     */
    final static DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss:SSS");

    /**
     * All the variables that defines a made transaction
     */
    final private LocalDateTime dateStamp;
    //final private User buyer; //for future needs
    //final private User seller; //for future needs
    final private int quantity;
    final private float turnOver; // =quantity * price
    final private float price; // save the price the shares really sold for

    /**
     * A ctor of a transaction instance.
     * @param quantity the quantity of share that were traded
     * @param soldPrice the per share price of the trade.
     */
    public Transaction(int quantity,float soldPrice){
        dateStamp = LocalDateTime.now();
        this.quantity = quantity;
        this.price = soldPrice;
        this.turnOver = quantity*soldPrice;
    }

    /**
     * A method that creates a string with all the data about the transaction.
     * @return all the data about the transaction.
     */
    @Override
    public String toString() {
        return "Transaction made at "+ dateStamp.format(dateTimeFormat) + " of " + quantity +" shares at the price of " + price +" for each. ";
    }

    /**
     * A getter of the made transaction turnover.
     * @return
     */
    public float getTurnOver() {
        return turnOver;
    }

    /**
     * A getter of the price per share of the made trade.
     * @return
     */
    public float getPrice() { return price; }

    /**
     * A methods that equalize an instance of transaction with an instance of another object.
     * @param o an instance of anther object.
     * @return true if they are equal, and false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return quantity == that.quantity && Float.compare(that.price, price) == 0 && dateStamp.equals(that.dateStamp);
    }

    /**
     * A method that generates a hash code from the instance variables.
     * @return a hash code from the instance variables.
     */
    @Override
    public int hashCode() {
        return Objects.hash(dateStamp, quantity, price);
    }
}
