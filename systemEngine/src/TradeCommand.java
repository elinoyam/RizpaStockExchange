import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.Objects;

public class TradeCommand implements Comparable<TradeCommand>{

    enum direction {BUY, SELL}
    enum commandType{LMT,MKT,FOK,IOC}

    private static DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss:SSS");

    final private direction direction;
    private int quantity;
    final private String symbol;
    private float wantedPrice;
    private LocalDateTime dateTimeStamp;
    final private commandType commandType;

    public TradeCommand(direction dir, commandType type, int howMany, float whatPrice, String symbol) {
        if (!(howMany>=0)) {
            throw new InputMismatchException("Invalid quantity value!, should be a positive integer");
        } else if(!(whatPrice>=0)) {
            throw new InputMismatchException("Invalid price value!, should be a positive real number");
        } else if (CompanyStocks.symbolCheck(symbol)) {
            throw new InputMismatchException("Invalid symbol, use upper letters only!");
        } else {
            this.direction = dir;
            this.quantity = howMany;
            this.symbol = symbol;
            this.wantedPrice = whatPrice;
            this.commandType = type;
            this.dateTimeStamp = LocalDateTime.now();
        }
    }

    public direction getDirection() { return this.direction; }

    public int getQuantity() {
        return quantity;
    }

    public String getSymbol() {
        return symbol;
    }

    public commandType getCommandType() { return commandType;}
    public float getPrice() {return wantedPrice;}
    public LocalDateTime getDate(){
        return dateTimeStamp;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    @Override
    public String toString() {
        return dateTimeStamp.format(dateTimeFormat) + " - This is " + commandType+ " " +direction +" command for " +quantity +" "+ symbol +" stocks.";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TradeCommand that = (TradeCommand) o;
        return quantity == that.quantity && Float.compare(that.wantedPrice, wantedPrice) == 0 && symbol.equals(that.symbol) && direction == that.direction && dateTimeStamp.equals(that.dateTimeStamp) && commandType == that.commandType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, direction, quantity, wantedPrice, dateTimeStamp, commandType);
    }

    @Override
    public int compareTo(TradeCommand other) {
        float res = this.wantedPrice - other.wantedPrice;
        if(res==0){
            return this.dateTimeStamp.compareTo(other.dateTimeStamp);
        }
        return (int)res;
    }

}
