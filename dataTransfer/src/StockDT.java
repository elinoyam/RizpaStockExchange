import java.util.LinkedList;
import java.util.List;

/**
 * A class that defines an output buffer of a stock.
 */
public class StockDT {
    /**
     * The variables that defines a stock.
     */
    private final String companyName;
    private final String symbol;
    private final float sharePrice;
    private final List<Transaction> stockTransactions; // Transactions that already been done
    private final float transactionsTurnOver;
    private final List<TradeCommandDT> buyCommandDTs;
    private final List<TradeCommandDT> sellCommandDTs;

    /**
     * A ctor of a class instance from all the needed variables.
     * @param companyName the name of the company that holds the share.
     * @param symbol the symbol of the stock.
     * @param sharePrice the price of the share.
     * @param transactions a list of the made transactions.
     * @param turnOver the turnover of the stock's transactions.
     * @param buy a list of the buy trade commands.
     * @param sell a list of the sell trade commands.
     */
    public StockDT(String companyName, String symbol, float sharePrice, List<Transaction> transactions, float turnOver,List<TradeCommandDT> buy, List<TradeCommandDT> sell){
        this.companyName = companyName;
        this.symbol = symbol;
        this.sharePrice = sharePrice;
        this.stockTransactions = new LinkedList<>(transactions); // copy the list, so no change can made from the UI
        this.transactionsTurnOver = turnOver;
        buyCommandDTs = buy;
        sellCommandDTs = sell;
    }

    /**
     * A ctor of a class instance from a stock instance.
     * @param stock
     */
     public StockDT(Stock stock){
        this.companyName = stock.getCompanyName();
        this.symbol = stock.getSymbol();
        this.sharePrice = stock.getSharePrice();
        this.stockTransactions = new LinkedList<>(stock.getStockTransactions()); // copy the list, so no change can made from the UI
        this.transactionsTurnOver = stock.getTransactionsTurnOver();
        buyCommandDTs = stock.getBuyCommandsList();
        sellCommandDTs = stock.getSellCommandsList();
    }

    /**
     * A getter of the company name.
     * @return the company name.
     */
    public String getCompanyName() { return companyName;}

    /**
     * A getter of the stock symbol.
     * @return the stock symbol.
     */
    public String getSymbol() { return symbol;}

    /**
     * A getter of the share price.
     * @return the share price.
     */
    public float getSharePrice(){ return sharePrice;}

    /**
     * A method that creates an output string with all the data about the stock.
     * @return string with all the data about the stock.
     */
    public String toString() {
        return (symbol + " (" + companyName + ") shares current price is: " + sharePrice+ ". " +
                "Until now " + stockTransactions.size() + " transactions have been made. " +
                "Total transactions turn over is : " + transactionsTurnOver);
    }

    /**
     * A method that calculates the buy commands turnover.
     * @return the buy commands turnover.
     */
    public float getBuyTransTurnover() {
        float res = 0;
        for (TradeCommandDT tr : buyCommandDTs)
            res += (tr.getPrice() * tr.getQuantity());
        return res;
    }

    /**
     * A method that calculates the sell commands turnover.
     * @return the sell commands turnover.
     */
    public float getSellTransTurnover() {
        float res = 0;
        for (TradeCommandDT tr : sellCommandDTs)
            res += (tr.getPrice() * tr.getQuantity());
        return res;
    }

    /**
     * A getter of the made transactions turnover.
     * @return the made transactions turnover.
     */
    public float getTransTurnover() {
        return transactionsTurnOver;
    }

    /**
     * A getter of list of buy commands.
     * @return list of buy commands.
     */
    public List<TradeCommandDT> getBuysCommands(){ return buyCommandDTs;}

    /**
     * A getter of list of sell commands.
     * @return list of sell commands.
     */
    public List<TradeCommandDT> getSellsCommands(){ return sellCommandDTs;}

    /**
     * A getter of list of the made transactions.
     * @return list of the made transactions.
     */
    public List<Transaction> getTransactions() { return stockTransactions;}
}
