import java.util.LinkedList;
import java.util.List;

public class StockDT {
    private final String companyName;
    private final String symbol;
    private final float sharePrice;
    private final List<Transaction> stockTransactions; // Transactions that already been done
    private final float transactionsTurnOver;
    private final List<TradeCommandDT> buyCommandDTs;
    private final List<TradeCommandDT> sellCommandDTs;

    public StockDT(String name, String symbol, float sharePrice, List<Transaction> lst, float turnOver,List<TradeCommandDT> buy, List<TradeCommandDT> sell){
        this.companyName = name;
        this.symbol = symbol;
        this.sharePrice = sharePrice;
        this.stockTransactions = new LinkedList<>(lst); // copy the list, so no change can made from the UI
        this.transactionsTurnOver = turnOver;
        buyCommandDTs = buy;
        sellCommandDTs = sell;
    }

     public StockDT(CompanyStocks stocks){
        this.companyName = stocks.getCompanyName();
        this.symbol = stocks.getSymbol();
        this.sharePrice = stocks.getSharePrice();
        this.stockTransactions = new LinkedList<>(stocks.getStockTransactions()); // copy the list, so no change can made from the UI
        this.transactionsTurnOver = stocks.getTransactionsTurnOver();
        buyCommandDTs = stocks.getBuyCommandsList();
        sellCommandDTs = stocks.getSellCommandsList();
    }

    public String getCompanyName() { return companyName;}

    public String getSymbol() { return symbol;}

    public float getSharePrice(){ return sharePrice;}


    public String toString() {
        return (symbol + " (" + companyName + ") shares current price is: " + sharePrice+ ". " +
                "Until now " + stockTransactions.size() + " transactions has been made. " +
                "Total transactions turn over is : " + transactionsTurnOver);
    }

    public float getBuyTransTurnover() {
        float res = 0;
        for (TradeCommandDT tr : buyCommandDTs)
            res += (tr.getPrice() * tr.getQuantity());
        return res;
    }

    public float getSellTransTurnover() {
        float res = 0;
        for (TradeCommandDT tr : sellCommandDTs)
            res += (tr.getPrice() * tr.getQuantity());
        return res;
    }

    public float getTransTurnover() {
        return transactionsTurnOver;
    }


    public List<TradeCommandDT> getBuysCommands(){ return buyCommandDTs;}
    public List<TradeCommandDT> getSellsCommands(){ return sellCommandDTs;}
    public List<Transaction> getTransactions() { return stockTransactions;}
}
