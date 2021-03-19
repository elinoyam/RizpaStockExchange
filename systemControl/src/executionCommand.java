import java.text.SimpleDateFormat;
import java.util.Date;

public class executionCommand {
    enum direction {BUY,SALE}
    enum commandType{LMT,MKT,FOK,IOC}
    final private String symbol;
    final private direction direction;
    private int quantity;
    private float wantedPrice;
    private String timeStamp;
    private commandType commandType;

    public executionCommand(direction dir,commandType type,String companyName,int howMany, float whatPrice){
        this.direction = dir;
        this.quantity = howMany;
        this.symbol = companyName;
        this.wantedPrice = whatPrice;
        this.commandType = type;
        this.timeStamp =  new SimpleDateFormat("HH:mm:ss:SSS").format(new Date());
    }

    @Override
    public String toString() {
        return timeStamp+ " - This is " + commandType+ " " +direction +" command for " +quantity +" "+symbol +" stocks.";
    }
}
