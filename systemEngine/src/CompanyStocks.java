import java.util.*;

public class CompanyStocks {
    private final String companyName;
    private final String symbol;
    private float sharePrice; // NEED TO BE UPDATED
    private List<Transaction> stockTransactions; // Transactions that already been done (to calculate the full value of the transactions)
    private Queue <TradeCommand> buyCommands;
    private Queue <TradeCommand> sellCommands;

    CompanyStocks(String companyName, String symbol, float startPrice) {
        if (!symbolCheck(symbol))
            throw new InputMismatchException("Invalid symbol, use upper letters only!");
        else if(!(startPrice>=0))
            throw new InputMismatchException("Invalid stock's start price value!, should be a positive real number");
        else {
            this.companyName = companyName;
            this.symbol = symbol;
            this.sharePrice = startPrice;
            stockTransactions = new LinkedList<>();
            buyCommands = new PriorityQueue<>(1, Collections.reverseOrder()); //Max Queue
            sellCommands = new PriorityQueue<>(1);                            //Min Queue
        }
    }

    static public boolean symbolCheck(String symbol) {
        for (char ch : symbol.toCharArray()){
            if(!(Character.isUpperCase(ch) && Character.isLetter(ch)))
                return false;
        }
        return true;
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

    public int getBuyCommandsCount(){ return buyCommands.size(); }
    public int getSellCommandsCount(){ return sellCommands.size(); }

    public List<TradeCommandDT> getBuyCommandsList() {
        int size = getBuyCommandsCount();
        List<TradeCommandDT> res = new ArrayList<>(getBuyCommandsCount());
        Queue<TradeCommand> tmp = new PriorityQueue<>(1, Collections.reverseOrder()); //Temporary Queue that will hold the values so we can run over all the
        for(int i=0;i<size;++i) {
            TradeCommand command = buyCommands.poll();
            tmp.add(command);
            res.add(new TradeCommandDT(command.getDirection(),command.getCommandType(),command.getQuantity(),command.getPrice(),command.getSymbol(),command.getDate()));
        }
        buyCommands = tmp;
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

    public float getTransactionsTurnOver() {
        float res =0;

        for(Transaction tr : stockTransactions){                               //Go through all the transactions and summing their turn over total
            res+= tr.getTurnOver();
        }
        return res;
    }

    public void addTransaction(int quantity, float price){
        stockTransactions.add(0,new Transaction(quantity,price));
        sharePrice = price;
    }

    public void addTransaction(Transaction trans){
        stockTransactions.add(0, trans);                                        //Adds to the beginning of the list.
        sharePrice = trans.getPrice();
    }

   public String addTradeCommand(TradeCommand.direction dir, TradeCommand.commandType command, int quantity, float wantedPrice){
        TradeCommand tr = new TradeCommand(dir, command, quantity, wantedPrice, this.getSymbol());
        // search here for a matching command
       return commandHandler(tr);
   }

   public void addTradeCommand(TradeCommand tr){
       // search here for a matching command
       commandHandler(tr);
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

    public int searchMatchingLMTCommand() {
        if (buyCommands.isEmpty() || sellCommands.isEmpty())
            return -1; // there isn't any matching command

        TradeCommand buy = buyCommands.peek(); // get the first buying trade
        TradeCommand sell = sellCommands.peek(); // get the first selling trade
        boolean flag = true;
        int sumShares = 0;

        while (flag) {
            if (buy.getPrice() < sell.getPrice())
                flag = false; // the buying rate isn't high enough

            // get the minimum quantity for the trade
            int finalQuantity = Arrays.stream(new int[]{buy.getQuantity(), sell.getQuantity()}).min().getAsInt();
            Transaction transaction = new Transaction(finalQuantity, sell.getPrice());
            stockTransactions.add(0, transaction); // add the new transaction
            sharePrice = sell.getPrice();
            sumShares += finalQuantity; // add the shares that been traded until now

            // need to check if there's any leftover shares
            if (buy.getQuantity() - sell.getQuantity() > 0) { // there are more buying shares awaiting
                buy.setQuantity(buy.getQuantity() - sell.getQuantity()); // updating the number of shares to buy
                sellCommands.remove(); // removing the first sell command
                sell = sellCommands.peek(); // check if there are more shares to trade
                if (sell == null)
                    flag = false;
            } else if (sell.getQuantity() - buy.getQuantity() > 0) {
                sell.setQuantity(sell.getQuantity() - buy.getQuantity());
                buyCommands.remove();
                buy = buyCommands.peek(); // check if there are more shares to trade
                if (buy == null)
                    flag = false;
            } else { // the quantities were equal - remove both of them from the queue
                flag = false;
                buyCommands.remove();
                sellCommands.remove();
            }
        }
        return sumShares;
    }

    public String commandHandler(TradeCommand command){
        System.out.println(command);
        switch (command.getCommandType()) {
            case LMT:
                return LMTHandler(command);
            case MKT:
                return null;
            case FOK:
                return null;
            case IOC:
                return null;
            default:
                throw new IllegalArgumentException("No such command type.");
        }
    }

    private String LMTHandler(TradeCommand command){
        switch (command.getDirection()){ // this is the only command that always saves the command to the queue
            case BUY:
                buyCommands.add(command);
                break;
            case SELL:
                sellCommands.add(command);
                break;
        }
        int saveQuantity = command.getQuantity();
        int res = searchMatchingLMTCommand(); // generic method, for both buy\sell commands. returns the number of shares that been traded
        if(res == -1)
            return("There isn't any opposite commands. The command entered to the waiting " + command.getDirection() + " commands list.");
        else if(res==0)
            return("The existing command rates aren't high enough for trade.The command entered to the waiting " + command.getDirection() + " commands list.");
        else if(res == command.getQuantity())
            return("The command was fully executed. ");
        else if(res < saveQuantity)
            return("The command was partly executed. The rest of the shares was entered to the waiting "+command.getDirection() + " commands list.");

        throw new UnknownError("Unknown Error Occurred In LMT command Handler");
    }
}



