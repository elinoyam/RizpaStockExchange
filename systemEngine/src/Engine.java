
import jaxb.schema.generated.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.*;

/*
This class main purpose is to handle all the data to execute the sell and buy commands of all the companies stocks.
 */
public class Engine implements Trader {
     private static Engine instance = new Engine();
     private static final String JAXB_XML_PACKAGE_NAME = "jaxb.schema.generated";
     private MultiKeyMap<String,CompanyStocks> stocks = new MultiKeyMap<>(); // will be search with their symbol

    private Engine(){ }

    public static Engine getInstance() {
        return instance;
    }

    private Collection<CompanyStocks> getListOfStocks(){
        return stocks.values();
    }

    // this method is for self check before we enter data from xml file
    public void addStock(CompanyStocks companyStocks){
        if(stocks.containsKey(companyStocks.getSymbol()))
            throw new InputMismatchException("This company already have stocks in the system.");
        stocks.put(companyStocks.getSymbol(),companyStocks.getCompanyName(), companyStocks);
    }

    public int stocksCount(){
        return stocks.size();
    }
    private CompanyStocks getSingleStock(String symbol){
        if(!(stocks.containsKey(symbol.toUpperCase()))) // check if there is a company with this symbol
            throw new InputMismatchException("There is no stock with this symbol. ");
        return stocks.get(symbol.toUpperCase());
    }

    public boolean isExistingStock(String symbol){
        return stocks.containsKey(symbol.toUpperCase());
    }

    public void uploadDataFromFile(String path) throws IOException // first option
    {
    // need to upload all the stocks from xml file
        stocks.clear();
        File xmlPath = new File(path);
        if(xmlPath.exists()) {
            try {
                InputStream inputStream = new FileInputStream(new File(path));
                deserializeFrom(inputStream);
            } catch (JAXBException | FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else
            throw new IOException("There is no such a XML file.");
    }

    private void deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();
        RizpaStockExchangeDescriptor rse = (RizpaStockExchangeDescriptor) u.unmarshal(in);
        List<RseStock> rseStocks = rse.getRseStocks().getRseStock();
        for(RseStock s : rseStocks)
        {
            CompanyStocks tmp = castRseStockToStock(s);
            stocks.put(tmp.getSymbol(),tmp.getCompanyName(),tmp);
        }
    }

    private CompanyStocks castRseStockToStock(RseStock rs){
        String symbol = rs.getRseSymbol();
        if(isSymbolExists(symbol))
            throw new IllegalArgumentException("The "+ symbol + " symbol is already exist, the stock's symbol should be unique!");

        String company = rs.getRseCompanyName();
        if(isCompanyNameExists(company))
            throw new IllegalArgumentException("The "+ company + " company is already exist, each company should have a single stock!");

        float price = rs.getRsePrice();
        if(price<=0)
            throw new IllegalArgumentException("The price ("+ price +") is not a positive number, stock's price should be a real positive number.");

        return new CompanyStocks(company,symbol,price);
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

    public boolean isSymbolExists(String symbol){
        if(stocks.containsKey(symbol))
            return true;
        return false;
    }

    public boolean isCompanyNameExists(String name) {
       if (stocks.containsKey(name))
           return true;
        return false;
    }
}

