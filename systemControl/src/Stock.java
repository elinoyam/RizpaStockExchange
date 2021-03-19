import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Stock {
    private final String companyName;
    private final String symbol;
    private float sharePrice;
    private List<Transaction> stockTransactions; // Transactions that already been done (to calculate the full value of the transactions)

    public Stock(String name, String symbol, float startPrice) {
        this.companyName = name;
        this.symbol = symbol.toUpperCase();
        this.sharePrice = startPrice;
        stockTransactions = new ArrayList<>();
    }

    @Override
    public String toString() {
        return (symbol + " (" + companyName + ") shares current price is: " + sharePrice+ ". " +
                "Until now " + stockTransactions.size() + "has been made." +
                "Total transactions turn over is : " + getTransactionsTurnOver());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stock stock = (Stock) o;
        return Objects.equals(companyName, stock.companyName);
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

    // updates the share price according to the last action made
    public void setSharePrice(float sharePrice) {
        this.sharePrice = sharePrice;
    }

    public float getTransactionsTurnOver(){
        float res =0;

        for(Transaction tr : stockTransactions){ // go through all the transactions and summing their turn over total
            res+= tr.getTurnOver();
        }
        return res;
    }

    public void addTransaction(int quantity, float startPrice){
        stockTransactions.add(new Transaction(quantity,startPrice));
        System.out.println("The transaction saved properly. ");
    }

    public void addTransaction(Transaction transaction){
        stockTransactions.add(transaction);
    }

    public List<Transaction> getStockTransactions(){
        return stockTransactions;
    }

}



