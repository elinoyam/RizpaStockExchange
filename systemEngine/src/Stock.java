import java.util.*;

/**
 * A class that defines a stock.
 */
public class Stock {
    /**
     * All the variables that defines a stock.
     */
    private final String companyName;
    private final String symbol;
    private float sharePrice; // NEED TO BE UPDATED
    private List<Transaction> stockTransactions; // Transactions that already been done (to calculate the full value of the transactions)
    private Queue <TradeCommand> buyCommands;
    private Queue <TradeCommand> sellCommands;

    /**
     * A ctor of stock instance.
     * @param companyName the name of the company that holds the stock.
     * @param symbol the symbol of the stock.
     * @param startPrice the initial price of the stock.
     * @throws InputMismatchException will be thrown when one of the variables from above isn't valid.
     */
    Stock(String companyName, String symbol, float startPrice) throws InputMismatchException {
        if (!symbolCheck(symbol))   //checks if the symbol is valid.
            throw new InputMismatchException("Invalid symbol, use upper letters only!");
        else if(!(startPrice>=0))   //checks if the start price is valid.
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

    /**
     * A method that checks the validity of a symbol.
     * @param symbol a symbol of a stock that we want to check.
     * @return true if valid, and false otherwise.
     */
    static public boolean symbolCheck(String symbol) {
        for (char ch : symbol.toCharArray()){
            if(!(Character.isUpperCase(ch) && Character.isLetter(ch)))
                return false;
        }
        return true;
    }

    /**
     * A method that creates a string with all the data about a stock.
     * @return string with all the data about a stock.
     */
    @Override
    public String toString() {
        return (symbol + " (" + companyName + ") shares current price is: " + sharePrice+ ". " +
                "Until now " + stockTransactions.size() + " transactions have been made. " +
                "Total transactions turn over is : " + getTransactionsTurnOver());
    }

    /**
     * A method that equalizes between an instance of a stock and an instance of another object
     * @param o an instance of another object.
     * @return true if they are equal, and false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stock stock = (Stock) o;
        return Objects.equals(companyName, stock.companyName);
    }

    /**
     * A method that generates a hash code from the company name that holds the stock.
     * @return a hash code from the company name that holds the stock.
     */
    @Override
    public int hashCode() {
        return Objects.hash(companyName);
    }

    /**
     * A getter of the company name
     * @return the company name
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * A getter of the share price
     * @return the share price
     */
    public float getSharePrice() {
        return sharePrice;
    }

    /**
     * A getter of the symbol.
     * @return the symbol of the stock.
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * A getter of the number of buy trade commands.
     * @return the number of buy trade commands.
     */
    public int getBuyCommandsCount(){ return buyCommands.size(); }

    /**
     * A getter of the number of sell trade commands.
     * @return the number of sell trade commands.
     */
    public int getSellCommandsCount(){ return sellCommands.size(); }

    /**
     * A getter of the list of buy commands.
     * @return list of buy commands.
     */
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

    /**
     * A getter of the list of sell commands.
     * @return list of sell commands.
     */
    public List<TradeCommandDT> getSellCommandsList() {
        int size = getSellCommandsCount();
        List<TradeCommandDT> res = new ArrayList<>(size);
        Queue<TradeCommand> tmp = new PriorityQueue<>(1); // temporary min queue
        for(int i=0;i<size;++i){
            TradeCommand command = sellCommands.poll();
            tmp.add(command);
            res.add(new TradeCommandDT(command.getDirection(),command.getCommandType(),command.getQuantity(),command.getPrice(),command.getSymbol(),command.getDate()));
        }
        sellCommands = tmp;
        return res;
    }

    /**
     * A getter of the list of the made transactions.
     * @return
     */
    public List<Transaction> getStockTransactions(){
        return stockTransactions;
    }

    /**
     * A method that calculates the made transactions turnover.
     * @return the made transactions turnover.
     */
    public float getTransactionsTurnOver() {
        float res =0;

        for(Transaction tr : stockTransactions){                               //Go through all the transactions and summing their turn over total
            res+= tr.getTurnOver();
        }
        return res;
    }

    /**
     * A method that add a new made transaction to the list of transactions.
     * @param quantity the number of share that were traded.
     * @param price the price per share of the trade.
     */
    public void addTransaction(int quantity, float price){
        stockTransactions.add(0,new Transaction(quantity,price));
        sharePrice = price;
    }

    /**
     * A method that add an existing transaction to the list of transactions.
     * @param trans the existing made transaction.
     */
    public void addTransaction(Transaction trans){
        stockTransactions.add(0, trans);                                        //Adds to the beginning of the list.
        sharePrice = trans.getPrice();
    }

    /**
     * A method that adds a new trade command to the relevant list of trade commands.
     * @param dir the trade direction.
     * @param command the type of the trade.
     * @param quantity the number of share to trade with.
     * @param wantedPrice the desired price per share.
     * @return A string with the initial status of the added trade.
     */
   public String addTradeCommand(TradeCommand.direction dir, TradeCommand.commandType command, int quantity, float wantedPrice){
       TradeCommand tr;
        if(command != TradeCommand.commandType.MKT)
           tr = new TradeCommand(dir, command, quantity, wantedPrice, this.getSymbol());
        else{
            float mktPrice;
            if(dir == TradeCommand.direction.BUY)
                mktPrice = getMKTSellPrice(quantity);
            else // sell command
                mktPrice = getMKTBuyPrice(quantity);
            tr = new TradeCommand(dir, command, quantity, mktPrice, this.getSymbol());
        }

        // search here for a matching command
       return commandHandler(tr);
   }

    /**
     * A methods that adds an existing trade command.
     * @param tr an existing trade command.
     */
   public void addTradeCommand(TradeCommand tr){
       // search here for a matching command
       commandHandler(tr);
   }

    /**
     * A method that calculates the buy trade commands turnover.
     * @return the buy trade commands turnover.
     */
    public float getBuyTransTurnover() {
        float res = 0;
        for (TradeCommand tr : buyCommands)
            res += (tr.getPrice() * tr.getQuantity());
        return res;
    }

    /**
     *  A method that calculates the sell trade commands turnover.
     * @return the sell trade commands turnover.
     */
    public float getSellTransTurnover() {
        float res = 0;
        for (TradeCommand tr : sellCommands)
            res += (tr.getPrice() * tr.getQuantity());
        return res;
    }

    /**
     * A method that searches for a matching trade command for a LMT trade command.
     * @return -1 if there isn't any matching commands, otherwise the number of shares that were traded.
     */
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

    /**
     * A method that handles with trad commands.
     * @param command the trade command that needed to be handled.
     * @return a string with the status of the trade command execution.
     * @throws IllegalArgumentException will be thrown in case there isn't a matching command type.
     */
    public String commandHandler(TradeCommand command) throws IllegalArgumentException{
        switch (command.getCommandType()) {
            case LMT:
                return LMTHandler(command);
            case MKT:
                return LMTHandler(command);
            /*  TODO: add FOK and IOC
            case FOK:
                return null;
            case IOC:
                return null;

            */
            default:
                throw new IllegalArgumentException("No such command type.");
        }
    }

    /**
     * A method that handles with LMT trade commands.
     * @param command an LMT trade command.
     * @return the status of the LMT trade command execution.
     */
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
        else if(res == saveQuantity)
            return("The command was fully executed. ");
        else if(res < saveQuantity)
            return("The command was partly executed. The rest of the "+command.getQuantity() +" shares was entered to the waiting "+command.getDirection() + " commands list.");

        throw new UnknownError("Unknown Error Occurred In LMT command Handler");
    }

    /**
     * A method that gets the market sell price of a stock.
     * @param quantity the number of shares.
     * @return =the sell market price per share * number of shares.
     */
    public float getMKTSellPrice(int quantity){
        int count =0; // count the number of shares until it get to the wanted quantity
        float savePrice=0;
        boolean found = false;
        Queue<TradeCommand> tmp = new PriorityQueue<>(1);
        while(count<quantity && !sellCommands.isEmpty() && !found){
            TradeCommand command = sellCommands.poll();
            tmp.add(command); // saves the command
            savePrice = command.getPrice();
            count+=command.getQuantity();

            if(count >= quantity) // checks if we counted enough shares
                found = true;
        }
        while(!tmp.isEmpty())
            sellCommands.add(tmp.poll());       // enters the commands back to the main priority queue
        return savePrice;                       // returns the highest price between the counted shares
    }

    /**
     * A method that gets the market buy price of a stock.
     * @param quantity the number of shares.
     * @return =the buy market price per share * number of shares.
     */
    public float getMKTBuyPrice(int quantity){
        int count =0; // count the number of shares until it get to the wanted quantity
        float savePrice=0;
        boolean found = false;
        Queue<TradeCommand> tmp = new PriorityQueue<>(1,Collections.reverseOrder());
        while(count<quantity && !buyCommands.isEmpty()&& !found){
            TradeCommand command = buyCommands.poll();
            tmp.add(command); // saves the command
            savePrice = command.getPrice();
            count += command.getQuantity();

            if(count >= quantity) // checks if we counted enough shares
                found = true;
        }
        while(!tmp.isEmpty())
            buyCommands.add(tmp.poll());       // enters the commands back to the main priority queue
        return savePrice;              // returns the lowest price between the counted shares
    }

}



