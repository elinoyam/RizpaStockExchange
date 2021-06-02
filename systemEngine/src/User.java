import javax.management.openmbean.InvalidKeyException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class saves all the data about a specific user in the system
 */
public class User {
    private final String userName;



    private Map<String,UserHoldings> userStocks;
    private float totalHoldingsValue;
    private List<Transaction> userTransactions;
    private Map<LocalDateTime,TradeCommand> userBuyCommands;
    private Map<LocalDateTime,TradeCommand> userSellCommands;

    User(String name){
        this.userName = name;
        this.userStocks = new TreeMap<>();
        totalHoldingsValue =0;
        this.userTransactions = new LinkedList<>();
        userBuyCommands = new TreeMap<>();
        userSellCommands = new TreeMap<>();
    }
    User(String name,Map<String,UserHoldings> stocks){
        this.userName = name;
        this.userTransactions = new LinkedList<>();
        userBuyCommands = new TreeMap<>();
        userSellCommands = new TreeMap<>();
        if(!stocks.equals(null))
            this.userStocks = stocks;
        else
            this.userStocks = new TreeMap<>();
        totalHoldingsValue =0;
        for(UserHoldings stock : userStocks.values()){
            totalHoldingsValue += (stock.getStock().getSharePrice()*stock.getQuantity());
        }
    }

    public void addUserTradeCommand(TradeCommand command,TradeCommand.direction dir){
        if(dir == TradeCommand.direction.SELL)
            userSellCommands.put(command.getDate(),command);
        else
            userBuyCommands.put(command.getDate(),command);
    }

    public void removeUserTradeCommand(LocalDateTime time, TradeCommand.direction dir, TradeCommand command){
        if(dir == TradeCommand.direction.SELL)
            userSellCommands.remove(time,command);
        else
            userBuyCommands.remove(time,command);
    }

    public void addUserTransaction(Transaction transaction){
        userTransactions.add(transaction);
    }

    public String getUserName() {
        return userName;
    }

    public float getTotalHoldingsValue() {
        totalHoldingsValue =0;
        for(UserHoldings hold: userStocks.values())
            totalHoldingsValue += hold.getQuantity()*hold.getStock().getSharePrice();
        return totalHoldingsValue;
    }
    public void setTotalHoldingsValue(float newValue) {
        totalHoldingsValue=newValue;
    }

    /**
     *
     * @param stock the stock we want to get the user holdings of
     * @return the number of shares the user have of the given stock
     * @throws InvalidKeyException The method throws exception if the user do not hold shares of the desired stock
     */
    public int getUserStockHoldings(Stock stock){
        if(!userStocks.containsKey(stock.getSymbol()))
            throw new InvalidKeyException("The user "+ userName +" don't have shares of the stock " + stock.getSymbol());
        return userStocks.get(stock.getSymbol()).getQuantity();
    }
    public int getUserStockHoldings(String stockSymbol){
        if(!userStocks.containsKey(stockSymbol))
            throw new InvalidKeyException("The user "+ userName +" don't have shares of the stock " + stockSymbol);
        return userStocks.get(stockSymbol).getQuantity();
    }

    public int getUserFreeHoldings(Stock stock){
        if(!userStocks.containsKey(stock.getSymbol()))
            throw new InvalidKeyException("The user "+ userName +" don't have shares of the stock " + stock.getSymbol());
        return userStocks.get(stock.getSymbol()).getFreeShares();
    }
    public int getUserFreeHoldings(String stockSymbol){
        if(!userStocks.containsKey(stockSymbol))
            throw new InvalidKeyException("The user "+ userName +" don't have shares of the stock " + stockSymbol);
        return userStocks.get(stockSymbol).getFreeShares();
    }



    public Map<LocalDateTime, TradeCommand> getUserBuyCommands() {
        return userBuyCommands;
    }

    public List<Transaction> getUserTransactions() {
        return userTransactions;
    }

    public Map<LocalDateTime, TradeCommand> getUserSellCommands() {
        return userSellCommands;
    }

    public Map<String, UserHoldings> getUserStocks() {
        return userStocks;
    }
    @Override
    public String toString() {
        return userName;
    }
}
