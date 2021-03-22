import java.util.*;

public class CompanyStocks {
    private final String companyName;
    private final String symbol;
    private float sharePrice; // NEED TO BE UPDATED
    private List<Transaction> stockTransactions; // Transactions that already been done (to calculate the full value of the transactions)
    private Queue <TradeCommand> buyCommands;
    private Queue <TradeCommand> sellCommands;

    public CompanyStocks(String name, String symbol, float startPrice) {
        this.companyName = name;
        this.symbol = symbol.toUpperCase();
        this.sharePrice = startPrice;
        stockTransactions = new LinkedList<>();
        buyCommands = new PriorityQueue<>(1,Collections.reverseOrder()); //Max Queue
        sellCommands = new PriorityQueue<>(1);                           //Min Queue
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

    public int getBuyCommandsCount(){return buyCommands.size();}
    public int getSellCommandsCount(){return sellCommands.size();}

    public List<TradeCommandDT> getBuyCommandsList() {
        List<TradeCommandDT> res = new ArrayList<>(getBuyCommandsCount());
        for(TradeCommand command: buyCommands){
            res.add(new TradeCommandDT(command.getDirection(),command.getCommandType(),command.getQuantity(),command.getPrice(),command.getSymbol(),command.getDate()));
        }
        return res;
    }
    public List<TradeCommandDT> getSellCommandsList() {
        List<TradeCommandDT> res = new ArrayList<>(getSellCommandsCount());
        for(TradeCommand command: sellCommands){
            res.add(new TradeCommandDT(command.getDirection(),command.getCommandType(),command.getQuantity(),command.getPrice(),command.getSymbol(),command.getDate()));
        }
        return res;
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
        // search here for a matching command
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
        // search here for a matching command
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

    public boolean searchMatchingCommand(TradeCommand command){
        switch (command.getDirection()){
            case BUY:
                // need to check if there is a sell command with a price that is lower from the set limit
                if(sellCommands.isEmpty())
                    return false; // there isn't any sell commands
                TradeCommand sell = sellCommands.peek(); // the sellCommands queue isn't empty so there must be a TradeCommand
                if(sell.getPrice()>command.getPrice())
                    return false; // the first (and with the lowest price) tradeCommand in the queue is to expansive for this buyCommand
                int[] quantity = {sell.getQuantity(),command.getQuantity()}; // to find the minimum quantity for the trade
                int finalQuantity = Arrays.stream(quantity).min().getAsInt();
                Transaction transaction = new Transaction(finalQuantity,sell.getPrice());
                stockTransactions.add(0,transaction); // add the new transaction
                if(sell.getQuantity()-finalQuantity >0)//need to check if there any shares left to the sell command
                break;
        }

        return true; // return if the method found a matching command
    }

}



