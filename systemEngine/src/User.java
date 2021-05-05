import javax.management.openmbean.InvalidKeyException;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class saves all the data about a specific user in the system
 */
public class User {
    private final String userName;
    private Map<Stock,Integer> userStocks;
    private float totalHoldingsValue;

    User(String name){
        this.userName = name;
        this.userStocks = new TreeMap<>();
        totalHoldingsValue =0;

    }
    User(String name,Map<Stock,Integer> stocks){
        this.userName = name;
        if(!stocks.equals(null))
            this.userStocks = stocks;
        else
            this.userStocks = new TreeMap<>();
        totalHoldingsValue =0;
        for(Stock stock : userStocks.keySet()){
            totalHoldingsValue += (stock.getSharePrice()*userStocks.get(stock));
        }
    }

    public String getUserName() {
        return userName;
    }

    public float getTotalHoldingsValue() {
        return totalHoldingsValue;
    }

    /**
     *
     * @param stock the stock we want to get the user holdings of
     * @return the number of shares the user have of the given stock
     * @throws InvalidKeyException The method throws exception if the user do not hold shares of the desired stock
     */
    public int getUserStockHoldings(Stock stock){
        if(!userStocks.containsKey(stock))
            throw new InvalidKeyException("The user "+ userName +" don't have shares of the stock " + stock.getSymbol());
        return userStocks.get(stock);
    }


}
