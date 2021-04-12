import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A class that defines an output buffer of a trade command instance.
 */
public class TradeCommandDT {

    /**
     * A format of showing the date and time.
     */
    final private static DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss:SSS");

    /**
     * The variables that defines a trade command.
     */
    final private TradeCommand.direction direction;
    final private int quantity;
    final private String symbol;
    final private float wantedPrice;
    final private LocalDateTime DateTimeStamp;
    final private TradeCommand.commandType commandType;

    /**
     * A ctor of class instance from the needed variables to define a trade command.
     * @param dir the direction of the trade command (sell/buy).
     * @param type the type of the trade command.
     * @param howMany the quantity of share to trade with.
     * @param whatPrice the desired price per share.
     * @param symbol the symbol of the traded stock.
     * @param dateTimeStamp the time and date of the trade command creation.
     */
    public TradeCommandDT(TradeCommand.direction dir, TradeCommand.commandType type, int howMany, float whatPrice, String symbol, LocalDateTime dateTimeStamp) {
        this.direction = dir;
        this.quantity = howMany;
        this.symbol = symbol;
        this.wantedPrice = whatPrice;
        this.commandType = type;
        this.DateTimeStamp = dateTimeStamp;
    }

    /**
     * A getter of the trade command direction.
     * @return the trade command direction.
     */
    public TradeCommand.direction getDirection(){
        return this.direction;
    }

    /**
     * A getter of the traded stock symbol.
     * @return the traded stock symbol.
     */
    public String getSymbol() {
        return this.symbol;
    }

    /**
     * A getter of the quantity of share to trade with.
     * @return  the quantity of share to trade with.
     */
    public int getQuantity() {
        return this.quantity;
    }

    /**
     * A getter of the desired price per share.
     * @return the desired price per share.
     */
    public float getPrice() {
        return this.wantedPrice;
    }

    /**
     * A getter of the trade command type.
     * @return the trade command type.
     */
    public TradeCommand.commandType getCommandType() {
        return this.commandType;
    }

    /**
     * A method that makes a string with all the data about the trade command.
     * @return string with all the data about the trade command.
     */
    public String toString(){
        return DateTimeStamp.format(dateTimeFormat) + " - " + quantity + " stocks for " + wantedPrice + " each. " +
                "Total turn over for command is " + (quantity*wantedPrice);
    }

    public LocalDateTime getDateTimeStamp() {
        return DateTimeStamp;
    }
}
