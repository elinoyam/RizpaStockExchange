import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TradeCommandDT {

    final private static DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss:SSS");

    final private TradeCommand.direction direction;
    final private int quantity;
    final private String symbol;
    final private float wantedPrice;
    final private LocalDateTime DateTimeStamp;
    final private TradeCommand.commandType commandType;

    public TradeCommandDT(TradeCommand.direction dir, TradeCommand.commandType type, int howMany, float whatPrice, String symbol, LocalDateTime dateTimeStamp) {
        this.direction = dir;
        this.quantity = howMany;
        this.symbol = symbol;
        this.wantedPrice = whatPrice;
        this.commandType = type;
        this.DateTimeStamp = dateTimeStamp;
    }

    public TradeCommand.direction getDirection(){
        return this.direction;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public float getPrice() {
        return this.wantedPrice;
    }

    public String getDateTimeStamp(DateTimeFormatter dtFormat) {
        //dtFormat = (dtFormat == null) ? dateTimeFormat:dtFormat;
       // String format = this.DateTimeStamp.format(dtFormat);
       return null;
    }

    public TradeCommand.commandType getCommandType() {
        return this.commandType;
    }

    public String toString(){
        return DateTimeStamp + " - " + quantity + " stocks for " + wantedPrice + " each. " +
                "Total turn over for command is " + (quantity*wantedPrice);
    }
}
