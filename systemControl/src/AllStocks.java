import java.util.*;

/*
This class main purpose is to handle all the data to execute the sale and buy commands of all the companies stocks.
 */
public class AllStocks {
    static private Map<String, CompanyStocks> stocks; // will be search with their symbol
    static private List<executionCommand> buyCommands;
    static private List<executionCommand> saleCommands;


     static {stocks = new TreeMap<>();}
   /* public AllStocks(){
            // need to add ctor from file - will automatically load all the data from the file

    }*/

    static public Collection<CompanyStocks> getListOfStocks(){
        return stocks.values();
    }

    // this method is for self check before we enter data from xml file
    static public void addStock(CompanyStocks companyStocks){
        if(stocks.containsKey(companyStocks.getSymbol()))
            throw new IllegalArgumentException("This company already have stocks in the system.");
        stocks.put(companyStocks.getSymbol(), companyStocks);
    }

    static public int stocksCount(){
        return stocks.size();
    }
    static public void printAllStocks(){
        CompanyStocks[] st = stocks.values().toArray(new CompanyStocks[0]);
        for(CompanyStocks s :st){
           System.out.println(s.toString());
        }
    }

    static public CompanyStocks getSingleStock(String symbol){
        if(!(stocks.containsKey(symbol.toUpperCase()))) // check if there is a company with this symbol
            throw new IllegalArgumentException("There is no stock with this symbol. ");
        return stocks.get(symbol);
    }


}
