

import java.util.*;

/*
This class main purpose is to handle all the data to execute the sell and buy commands of all the companies stocks.
 */
public class Engine implements Trader {
     static private Map<String, CompanyStocks> stocks = new TreeMap<>(); // will be search with their symbol

     /*public AllStocks(){
            // need to add ctor from file - will automatically load all the data from the file

    }*/

    static public Collection<CompanyStocks> getListOfStocks(){
        return stocks.values();
    }

    // this method is for self check before we enter data from xml file
    static public void addStock(CompanyStocks companyStocks){
        if(stocks.containsKey(companyStocks.getSymbol()))
            throw new InputMismatchException("This company already have stocks in the system.");
        stocks.put(companyStocks.getSymbol(), companyStocks);
    }

    static public int stocksCount(){
        return stocks.size();
    }
//    static public void printAllStocks(){
//        CompanyStocks[] st = stocks.values().toArray(new CompanyStocks[0]);
//        for(CompanyStocks s :st){
//           System.out.println(s.toString());
//        }
//    }

    static public CompanyStocks getSingleStock(String symbol){
        if(!(stocks.containsKey(symbol.toUpperCase()))) // check if there is a company with this symbol
            throw new InputMismatchException("There is no stock with this symbol. ");
        return stocks.get(symbol.toUpperCase());
    }

    public void uploadDataFromFile(String path) // first option
    {
    // need to upload all the stocks from xml file
    }
    public List<StockDT> showAllStocks() // second option
    {
        List<StockDT> res = new ArrayList<>(stocksCount()); // make list in the size of the current number of stocks
        for(CompanyStocks cs : getListOfStocks()){
            res.add(new StockDT(cs.getCompanyName(),cs.getSymbol(),cs.getSharePrice(),cs.getStockTransactions(), cs.getTransactionsTurnOver(), cs.getBuyCommandsList(),cs.getSellCommandsList()));
        }
        return res;
    }
    public StockDT showStock(String companySymbol) // third option
    {
        CompanyStocks s = getSingleStock(companySymbol);
        return new StockDT(s.getCompanyName(), s.getSymbol(), s.getSharePrice(), s.getStockTransactions(), s.getTransactionsTurnOver(),s.getBuyCommandsList(),s.getSellCommandsList());
    }

    @Override
    public void addTradeCommand(String companySymbol, TradeCommand.direction dir, TradeCommand.commandType command, int quantity, float wantedPrice) {
        try{
            CompanyStocks stock = getSingleStock(companySymbol);
            stock.addTradeCommand(dir, command, quantity, wantedPrice);
        } catch (InputMismatchException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<StockDT> showAllCommands() // fifth option
    {
       return showAllStocks(); // same data is in both of them
    }
}