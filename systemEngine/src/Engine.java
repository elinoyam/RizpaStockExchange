
import schema.generated.RizpaStockExchangeDescriptor;
import schema.generated.RseStock;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.*;

/*
This class main purpose is to handle all the data to execute the sell and buy commands of all the companies stocks.
 */
public class Engine implements Trader {
     private final static String JAXB_XML_PACKAGE_NAME = "examples.jaxb.schema.generated";
     static private Map<String, CompanyStocks> stocks = new TreeMap<>(); // will be search with their symbol

     /*public AllStocks(){
            // need to add ctor from file - will automatically load all the data from the file

    }*/

    static private Collection<CompanyStocks> getListOfStocks(){
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

    static private CompanyStocks getSingleStock(String symbol){
        if(!(stocks.containsKey(symbol.toUpperCase()))) // check if there is a company with this symbol
            throw new InputMismatchException("There is no stock with this symbol. ");
        return stocks.get(symbol.toUpperCase());
    }
    static public boolean isExistingStock(String symbol){
        return stocks.containsKey(symbol.toUpperCase());
    }


    public void uploadDataFromFile(String path) throws IOException // first option
    {
    // need to upload all the stocks from xml file
        File xmlPath = new File(path);
        if(xmlPath.exists()) {
            try {
                InputStream inputStream = new FileInputStream(new File(path));
                Map<String, CompanyStocks> stocks = deserializeFrom(inputStream);
            } catch (JAXBException | FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else
            throw new IOException("There is no such a XML file.");
    }

    private static Map<String, CompanyStocks> deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();
        RizpaStockExchangeDescriptor rse = (RizpaStockExchangeDescriptor) u.unmarshal(in);
        List<RseStock> stocks = rse.getRseStocks().getRseStock();
        for(RseStock s : stocks)
        {
            //CompanyStocks tmp = new CompanyStocks(s); //TODO: add a ctor to companyStocks from RseStock.
        }

    }

    public List<StockDT> showAllStocks() // second option in the main menu
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
    public String addTradeCommand(String companySymbol, TradeCommand.direction dir, TradeCommand.commandType command, int quantity, float wantedPrice) {
        CompanyStocks stock = getSingleStock(companySymbol);
        return stock.addTradeCommand(dir, command, quantity, wantedPrice);
    }

    public List<StockDT> showAllCommands(){ // fifth option - for future use. for now just return the same as showAllStocks()
        return showAllStocks(); // same data is in both of them
    }
}
