
import jaxb.schema.generated.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.*;

/**
 * This class main purpose is to handle all the data to execute the sell and buy commands of all the companies stocks.
 * The class Implements a singleton design pattern.
 */
public class Engine implements Trader {
     private static Engine instance = new Engine();                                 //the single instance of the class.
     private static final String JAXB_XML_PACKAGE_NAME = "jaxb.schema.generated";
     private MultiKeyMap<String,CompanyStocks> stocks = new MultiKeyMap<>();        //will be search with their symbol

    private Engine(){ }                                                             //private to prevent a creation of instances

    /**
     * A method that let access to the single instance of the class.
     * @return the single instance of the class.
     */
    public static Engine getInstance() {
        return instance;
    }

    /**
     * A getter of the list of stocks.
     * @return
     */
    private Collection<CompanyStocks> getListOfStocks(){
        return stocks.values();
    }

    /**
     * A method that adds a new stock to the MultiKeyMap of stocks.
     * @param companyStocks the stock that we would like to add to the MultiKeyMap of stocks.
     * @throws InputMismatchException will be thrown in case that the stock is already exists.
     */
    public void addStock(CompanyStocks companyStocks) throws InputMismatchException{
        if(stocks.containsKey(companyStocks.getSymbol()))                                       //checks if the stock is already exists in the MultiKeyMap of stocks.
            throw new InputMismatchException("This company already have stocks in the system.");
        stocks.put(companyStocks.getSymbol(),companyStocks.getCompanyName(), companyStocks);    //adds the stock to the MultiKeyMap of stocks.
    }

    /**
     * A method that return the number of stocks we have in the system.
     * @return the number of stocks that the MultiKeyMap of stocks contains.
     */
    public int stocksCount(){
        return stocks.size();
    }

    /**
     * A method that return an instance of single stock.
     * @param symbol the symbol of the desired stock.
     * @return the stock that matches the symbol.
     * @throws InputMismatchException will be thrown in case there isn't a stock with this symbol.
     */
    private CompanyStocks getSingleStock(String symbol) throws InputMismatchException {
        if(!(stocks.containsKey(symbol.toUpperCase()))) // check if there is a company with this symbol
            throw new InputMismatchException("There is no stock with this symbol. ");
        return stocks.get(symbol.toUpperCase());
    }

    /**
     * A method that checks by a stock symbol if the stock exists in the system.
     * @param symbol the symbol of the stock that we would like to check it existence.
     * @return return true if the stock exists, and false otherwise.
     */
    public boolean isExistingStock(String symbol){
        return stocks.containsKey(symbol.toUpperCase());
    }

    /**
     * A method that uploads a system instance from an xml file.
     * @param path a path to a xml file that contains the required data.
     * @throws FileNotFoundException will be thrown in case the file isn't exits.
     * @throws JAXBException will be thrown in case the JAXB process failed.
     * @throws IllegalArgumentException will be thrown in case the the file isn't a xml file.
     */
    public void uploadDataFromFile(String path) throws FileNotFoundException,JAXBException,IllegalArgumentException // first option
    {
    // need to upload all the stocks from xml file
        MultiKeyMap<String,CompanyStocks> tmpStocks = new MultiKeyMap<>();

        try {
            File xmlPath = new File(path);
            InputStream inputStream = new FileInputStream(new File(path));
            if(!isXMLFile(path))
                throw new IllegalArgumentException("The given file is not a xml file.");
            deserializeFrom(inputStream,tmpStocks);
            stocks.clear();
            stocks = tmpStocks;
        } catch (JAXBException e){
            throw new JAXBException("JAXB Exception detected.");
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("There is no such a XML file.");
        }
    }

    /**
     * A method that checks if a file is an XML file.
     * @param path a path to the file that we would like to check.
     * @return returns true if it's a XML file, and false otherwise.
     */
    private boolean isXMLFile(String path){
        String extension = "";
        int i = path.lastIndexOf('.');
        if (i > 0) {
            extension = path.substring(i+1);
        }
        if(extension.toLowerCase().equals("xml"))
            return true;
        else
            return false;
    }

    /**
     * A method that deserialize the XML file that contains the system's data.
     * @param in an FileInputStream that is contacted to the XML file.
     * @param tmpStocks a temporary MultiKeyMap of system's stocks (prevents a deletion of the previous system data in case there will be a failure).
     * @throws JAXBException will be thrown in case there is a problem in the process of JAXB.
     */
    private void deserializeFrom(InputStream in,MultiKeyMap<String,CompanyStocks> tmpStocks) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();
        RizpaStockExchangeDescriptor rse = (RizpaStockExchangeDescriptor) u.unmarshal(in); //Converts the XML file content into an instance of the generated class.
        List<RseStock> rseStocks = rse.getRseStocks().getRseStock();                       //gets a list of all the stocks
        for(RseStock s : rseStocks)                                                        //casts each generated stock class instance to the system's stock class and inserts them into the MultiKeyMap.
        {
            CompanyStocks tmp = castRseStockToStock(s,tmpStocks);                               //casts the generated stock class to system's stock class.
            tmpStocks.put(tmp.getSymbol(),tmp.getCompanyName(),tmp);                            //inserts the stock into the MultiKeyMap.
        }
    }

    /**
     * A method that casts a generated stock class instance to a system's stock class instance.
     * @param rs a generated stock class instance.
     * @param map a MultiKeyMap of the existing stocks (required in order to check prevent a creation of an exiting stock.
     * @return a stock instance.
     * @throws IllegalArgumentException will be thrown in case that the stock is invalid application wise.
     */
    private CompanyStocks castRseStockToStock(RseStock rs, MultiKeyMap<String,CompanyStocks> map) throws  IllegalArgumentException{
        String symbol = rs.getRseSymbol();
        if(map.containsKey(symbol))             //checks if there is a stock with this symbol.
            throw new IllegalArgumentException("The "+ symbol + " symbol is already exist, the stock's symbol should be unique!");

        String company = rs.getRseCompanyName();
        if(map.containsKey(company))            //checks if there is a stock that is owned by this company.
            throw new IllegalArgumentException("The "+ company + " company is already exist, each company should have a single stock!");

        float price = rs.getRsePrice();
        if(price<=0)                            //checks that the stock price is valid (positive real number).
            throw new IllegalArgumentException("The price ("+ price +") is not a positive number, stock's price should be a real positive number.");

        return new CompanyStocks(company,symbol,price);
    }

    /**
     * A method makes a list that acts as an output buffer of the existing stocks in the system.
     * @return a list of the existing stocks in systems.
     */
    public List<StockDT> showAllStocks() // second option in the main menu
    {
        List<StockDT> res = new ArrayList<>(stocksCount()); // make list in the size of the current number of stocks
        for(CompanyStocks cs : getListOfStocks()){
            res.add(new StockDT(cs.getCompanyName(),cs.getSymbol(),cs.getSharePrice(),cs.getStockTransactions(), cs.getTransactionsTurnOver(), cs.getBuyCommandsList(),cs.getSellCommandsList()));
        }
        return res;
    }

    /**
     * A method that makes a data transfer stock instance that acts like an output buffer of the stock in the system
     * @param companySymbol a symbol of the stock that we would like to show.
     * @return an instance of data transfer type of the stock class.
     * @throws InputMismatchException will be thrown in case there isn't a stock with this symbol.
     */
    public StockDT showStock(String companySymbol) throws InputMismatchException
    {
        CompanyStocks s = getSingleStock(companySymbol);
        return new StockDT(s.getCompanyName(), s.getSymbol(), s.getSharePrice(), s.getStockTransactions(), s.getTransactionsTurnOver(),s.getBuyCommandsList(),s.getSellCommandsList());
    }

    /**
     * A method that adds a trade command to a stock.
     * @param companySymbol a symbol of the stock that we would like to perform on it the command.
     * @param dir the direction of the command (buy/sell).
     * @param command the type of the trade command.
     * @param quantity the quantity of stocks that we would like to trade.
     * @param wantedPrice the desired price per stock.
     * @return a status message about the command addition (sometimes will be executed immediate and sometimes not)
     * @throws InputMismatchException will be thrown in case there isn't a stock will this symbol.
     */
    @Override
    public String addTradeCommand(String companySymbol, TradeCommand.direction dir, TradeCommand.commandType command, int quantity, float wantedPrice) throws InputMismatchException {
        CompanyStocks stock = getSingleStock(companySymbol);
        if(command != TradeCommand.commandType.MKT)                             //in MKT command there isn't a need to ask the user for a desired price.
            return stock.addTradeCommand(dir, command, quantity, wantedPrice);
        else
            return stock.addTradeCommand(dir, command, quantity, stock.getSharePrice());
    }

    /**
     * A method that creates a list that acts as an output buffer of all the trade commands in the system.
     * @return a list of all the trade commands in the system.
     */
    public List<StockDT> showAllCommands(){ // fifth option - for future use. for now just return the same as showAllStocks()
        return showAllStocks(); // same data is in both of them
    }

    /**
     * A method that checks if there is a stock with this symbol.
     * @param symbol the symbol that we would like to check.
     * @return true if it excites and false otherwise.
     */
    public boolean isSymbolExists(String symbol){
        if(stocks.containsKey(symbol))
            return true;
        return false;
    }

    /**
     * A method that checks if there is a stock that is owned by this company.
     * @param name a name of the company.
     * @return true if it excites and false otherwise.
     */
    public boolean isCompanyNameExists(String name) {
       if (stocks.containsKey(name))
           return true;
        return false;
    }
}

