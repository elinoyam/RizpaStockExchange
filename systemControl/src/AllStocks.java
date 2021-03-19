import java.util.*;

public class AllStocks {
     static private Map<String, Stock> stocks; // will be search with their symbol

     static {stocks = new TreeMap<>();}
   /* public AllStocks(){
            // need to add ctor from file - will automatically load all the data from the file

    }*/

    static public Collection<Stock> getListOfStocks(){
        return stocks.values();
    }

    // this method is for self check before we enter data from xml file
    public void addStock(Stock stock){
        if(stocks.containsKey(stock.getSymbol()))
            throw new IllegalArgumentException("This company already have stocks in the system.");
        stocks.put(stock.getSymbol(),stock);
    }

    static public int stocksCount(){
        return stocks.size();
    }
    static public void printAllStocks(){
        Stock[] st = stocks.values().toArray(new Stock[0]);
        for(Stock s :st){
           System.out.println(s.toString());
        }
    }

    static public Stock getSingleStock(String symbol){
        if(!(stocks.containsKey(symbol.toUpperCase())))
            throw new IllegalArgumentException("There is no stock with this symbol. ");
        return stocks.get(symbol);
    }


}
