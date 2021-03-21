import java.util.*;

public class CompanyStocks {
    private final String companyName;
    private final String symbol;
    private float sharePrice; // NEED TO BE UPDATED
    private List<Transaction> stockTransactions; // Transactions that already been done (to calculate the full value of the transactions)

    public CompanyStocks(String name, String symbol, float startPrice) {
        this.companyName = name;
        this.symbol = symbol.toUpperCase();
        this.sharePrice = startPrice;
        stockTransactions = new ArrayList<>();
    }

    @Override
    public String toString() {
        return (symbol + " (" + companyName + ") shares current price is: " + sharePrice+ ". " +
                "Until now " + stockTransactions.size() + " transactions has been made. " +
                "Total transactions turn over is : " + getTransactionsTurnOver());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompanyStocks companyStocks = (CompanyStocks) o;
        return Objects.equals(companyName, companyStocks.companyName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyName);
    }

    public String getCompanyName() {
        return companyName;
    }

    public float getSharePrice() {
        return sharePrice;
    }

    public String getSymbol() {
        return symbol;
    }

    public List<Transaction> getStockTransactions(){
        return stockTransactions;
    }

    public float getTransactionsTurnOver(){
        float res =0;

        for(Transaction tr : stockTransactions){                               //Go through all the transactions and summing their turn over total
            res+= tr.getTurnOver();
        }
        return res;
    }

    public void addTransaction(int quantity, float price){
        stockTransactions.add(new Transaction(quantity,price));
        sharePrice = price;
        System.out.println("The transaction saved properly.");
    }

    public void addTransaction(Transaction trans){
        stockTransactions.add(0, trans);                           //Adds to the beginning of the list.
        sharePrice = trans.getPrice();
        System.out.println("The transaction saved properly.");
    }

   public void addTradeCommand(TradeCommand.direction dir, TradeCommand.commandType command, int quantity, float wantedPrice){
        TradeCommand tr = new TradeCommand(dir,command,quantity,wantedPrice,this.getSymbol());
        switch (dir) {
            case BUY:
                buyCommands.add(tr);
                break;
            case SELL:
                sellCommands.add(tr);
                break;
            default:
                throw new InputMismatchException("Invalid Trade Direction, The Options are (Buy/Sell).");
        }
   }

   public void addTradeCommand(TradeCommand tr){
        switch (tr.getDirection())
        {
            case BUY:
                buyCommands.add(tr);
                break;
            case SELL:
                sellCommands.add(tr);
                break;
            default:
                throw new InputMismatchException("Invalid Trade Direction, The Options are (Buy/Sell).");
        }
   }

    public float getBuyTransTurnover() {
        float res = 0;
        for (TradeCommand tr : buyCommands)
            res += (tr.getPrice() * tr.getQuantity());
        return res;
    }

    public float getSellTransTurnover() {
        float res = 0;
        for (TradeCommand tr : sellCommands)
            res += (tr.getPrice() * tr.getQuantity());
        return res;
    }

}



