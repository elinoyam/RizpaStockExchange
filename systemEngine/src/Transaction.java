import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
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
    final private int quantity;
    final private float turnOver; // =quantity * price
    final private float price; // save the price the shares really sold for
    final private User buyer;
    final private User seller;


    public User getBuyer() {
        return buyer;
    }

    public User getSeller() {
        return seller;
    }

    /**
     * A ctor of a transaction instance.
     * @param quantity the quantity of share that were traded
     * @param soldPrice the per share price of the trade.
     * @param buyer
     * @param seller
     */
    public Transaction(int quantity, float soldPrice, User buyer, User seller,Stock stock){

        if(quantity<=0)
            throw new InputMismatchException("Invalid number of traded shares, should be a positive integer.");
        else if(soldPrice<=0)
            throw new InputMismatchException("Invalid price per share, should be a positive real number.");
        else {
            dateStamp = LocalDateTime.now();
            this.quantity = quantity;
            this.price = soldPrice;
            this.turnOver = quantity * soldPrice;
            this.buyer = buyer;
            this.seller = seller;

            if(seller.getUserStockHoldings(stock.getSymbol()) == quantity){
                seller.getUserStocks().remove(stock.getSymbol());
                buyer.getUserStocks().put(stock.getSymbol(),new UserHoldings(stock.getSymbol(),stock,quantity,soldPrice));
            }
            else{
                seller.getUserStocks().get(stock.getSymbol()).setQuantity( seller.getUserStocks().get(stock.getSymbol()).getQuantity()-quantity);
                if(!buyer.getUserStocks().containsKey(stock.getSymbol()))
                    buyer.getUserStocks().put(stock.getSymbol(),new UserHoldings(stock.getSymbol(),stock,quantity,soldPrice));
                else{
                    UserHoldings holdings = buyer.getUserStocks().get(stock.getSymbol());
                    holdings.setQuantity(buyer.getUserStocks().get(stock.getSymbol()).getQuantity()+quantity);
                    holdings.setSharePrice(soldPrice);
                    holdings.setTotalHold(buyer.getUserStocks().get(stock.getSymbol()).getQuantity()*soldPrice);
                    holdings.setFreeShares(holdings.getFreeShares()+quantity);
                }
            }

        }
    }

    public Transaction(int quantity, float soldPrice, LocalDateTime datetimeStamp, User buyer, User seller){

        if(quantity<=0)
            throw new InputMismatchException("Invalid number of traded shares, should be a positive integer.");
        else if(soldPrice<=0)
            throw new InputMismatchException("Invalid price per share, should be a positive real number.");
        else {
            dateStamp = datetimeStamp;
            this.quantity = quantity;
            this.price = soldPrice;
            this.turnOver = quantity * soldPrice;
            this.buyer = buyer;
            this.seller = seller;
        }
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

    public LocalDateTime getDateStamp() {
        return dateStamp;
    }

    public int getQuantity() {
        return quantity;
    }

    public static DateTimeFormatter getDateTimeFormat() {
        return dateTimeFormat;
    }
}
