import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import jaxb.schema.generated.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.*;

import static java.lang.Math.pow;

class FileLoadTask extends Task<Boolean> {

    private static long defTotalWork = 4;
    private long timeout = 300;
    private String path;
    private Engine engine;
    private Object lock1 = new Object();
    private Object lock2 = new Object();
    private long subTasks;
    private long doneSubTasks;

    protected FileLoadTask(String filePath, Engine engine) {
        this.path = filePath;
        this.engine = engine;
        this.subTasks = defTotalWork;
        this.doneSubTasks = 0;
        //super.updateValue(false);
    }

    public long getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(long subTasks) {
        this.subTasks = subTasks;
        updateProgress(getDoneSubTasks(),subTasks);
    }

    public long getDoneSubTasks() {
        return doneSubTasks;
    }

    public void setDoneSubTasks(long doneSubTasks) {
        this.doneSubTasks = doneSubTasks;
        updateProgress(doneSubTasks,getSubTasks());
    }

    public void incDoneSubTasks() {
        this.doneSubTasks++;
        updateProgress(doneSubTasks,getSubTasks());
    }

    /* public long getSubTasks() {
        return subTasks.getValue();
    }

    public LongProperty subTasksProperty() {
        return subTasks;
    }

    public void setSubTasks(long subTasks) {
        this.subTasks.setValue(subTasks);
    }

    public long getDoneSubTasks() {
        return doneSubTasks.getValue();
    }

    public LongProperty doneSubTasksProperty() {
        return doneSubTasks;
    }

    public void incDoneSubTasks() {
        this.doneSubTasks.setValue(getDoneSubTasks()+1);
    }

    public void setDoneSubTasks(long doneSubTasks) {
        this.doneSubTasks.setValue(doneSubTasks);
    }*/

    @Override
    protected Boolean call() throws Exception {
        synchronized (lock1) {
            updateMessage("Fetching File...");
            incDoneSubTasks();//1
            lock1.wait(timeout);
            try {
                uploadDataFromFile();
            } catch (Exception e) {
                System.out.println(e.toString());
                updateMessage("Failed to load the file:\n" + e.getMessage());
                return false;
            }
            lock1.wait(timeout);
            incDoneSubTasks();//4
            updateMessage("Reading of file completed!");
            return true;
        }
    }

    protected void uploadDataFromFile() throws FileNotFoundException, JAXBException, IllegalArgumentException {
        // need to upload all the stocks from xml file
        MultiKeyMap<String, Stock> tmpStocks = new MultiKeyMap<>();

        try {
            File xmlPath = new File(path);
            InputStream inputStream = new FileInputStream(new File(path));
            updateMessage("Opening file.. ");
            incDoneSubTasks();//2
            if (!isXMLFile(path))
                throw new IllegalArgumentException("The given file is not a xml file.");
            deserializeFrom(inputStream, tmpStocks);
            engine.getStocks().clear();
            engine.setStocks(tmpStocks);
            updateMessage("Fetching data from file ended.");
            incDoneSubTasks();//3
        } catch (JAXBException e) {
            throw new JAXBException("JAXB Exception detected.");
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("There is no such a XML file.");
        }
    }

    private boolean isXMLFile(String path) {
        String extension = "";
        int i = path.lastIndexOf('.');
        if (i > 0) {
            extension = path.substring(i + 1);
        }
        if (extension.toLowerCase().equals("xml"))
            return true;
        else
            return false;
    }

    private void deserializeFrom(InputStream in, MultiKeyMap<String, Stock> tmpStocks) throws JAXBException, IllegalArgumentException, InputMismatchException, DateTimeException {
        try {
            synchronized (lock2) {
                int i=0;
                JAXBContext jc = JAXBContext.newInstance(engine.getJaxbXmlPackageName());
                Unmarshaller u = jc.createUnmarshaller();
                updateMessage("Unmarshalling...");
                incDoneSubTasks();//3
                lock2.wait(timeout);
                RizpaStockExchangeDescriptor rse = (RizpaStockExchangeDescriptor) u.unmarshal(in); //Converts the XML file content into an instance of the generated class.
                List<RseStock> rseStocks = rse.getRseStocks().getRseStock();                       //gets a list of all the stocks
                updateMessage("Starting to read stocks data...");
                incDoneSubTasks();//4
                setSubTasks(rseStocks.size()+rse.getRseUsers().getRseUser().size()+4);
                setDoneSubTasks(0);//0
                lock2.wait(timeout);


                for (RseStock s : rseStocks)                                                        //casts each generated stock class instance to the system's stock class and inserts them into the MultiKeyMap.
                {
                    Stock tmp = castRseStockToStock(s, tmpStocks);                                   //casts the generated stock class to system's stock class.
                    tmpStocks.put(tmp.getSymbol(), tmp.getCompanyName(), tmp);                        //inserts the stock into the MultiKeyMap.
                    incDoneSubTasks();
                    updateMessage("Reading stock "+(i++)+"/"+rseStocks.size()+"...");
                    lock2.wait(timeout);
                }
                incDoneSubTasks();//1
                updateMessage("Starting to read users data...");
                lock2.wait(timeout);
                i=0;
                for (RseUser user : rse.getRseUsers().getRseUser()) {
                    Map<String, UserHoldings> holdings = new TreeMap<>();
                    for (RseItem item : user.getRseHoldings().getRseItem())      // make a list of all the stocks holdings of the user
                        holdings.put(item.getSymbol(), new UserHoldings(item.getSymbol(), tmpStocks.get(item.getSymbol()), item.getQuantity()/*, item.getSharePrice() TODO:!*/));
                    engine.getUsers().put(user.getName(), new User(user.getName(), holdings));
                    incDoneSubTasks();
                    updateMessage("Reading user "+(i++)+"/"+rse.getRseUsers().getRseUser().size()+"...");
                    lock2.wait(timeout);
                }
                incDoneSubTasks();//2
                updateMessage("Done with deserialization.");
                lock2.wait(timeout);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Stock castRseStockToStock(RseStock rs, MultiKeyMap<String, Stock> map) throws IllegalArgumentException, InputMismatchException {
        String symbol = rs.getRseSymbol();
        if (map.containsKey(symbol))             //checks if there is a stock with this symbol.
            throw new IllegalArgumentException("The file loading failed!, since the " + symbol + " symbol is already exist, the stock's symbol should be unique!");

        String company = rs.getRseCompanyName();
        if (map.containsKey(company))            //checks if there is a stock that is owned by this company.
            throw new IllegalArgumentException("The file loading failed!, since the " + company + " company is already exist, each company should have a single stock!");

        float price = rs.getRsePrice();
        if (price <= 0)                            //checks that the stock price is valid (positive real number).
            throw new IllegalArgumentException("The file loading failed!, since the price (" + price + ") is not a positive number, stock's price should be a real positive number.");

        Queue<TradeCommand> buyCommands = null;
        List<RseTradeCommand> rseBuyCommands = null;
        if (rs.getRseBuyCommands() != null) {
            buyCommands = new PriorityQueue<>(1, Collections.reverseOrder());
            rseBuyCommands = rs.getRseBuyCommands();
            for (RseTradeCommand tc : rseBuyCommands)
                buyCommands.add(new TradeCommand(TradeCommand.direction.valueOf(tc.getRseDir()), TradeCommand.commandType.valueOf(tc.getRseType()), tc.getRseQuantity(), tc.getRsePrice(), tc.getRseSymbol(), LocalDateTime.parse(tc.getRseDateTime()),engine.getUsers().get(tc.getRseUser())));
        }
        Queue<TradeCommand> sellCommands = null;
        List<RseTradeCommand> rseSellCommands = null;
        if (rs.getRseSellCommands() != null) {
            sellCommands = new PriorityQueue<>(1);
            rseSellCommands = rs.getRseSellCommands();
            for (RseTradeCommand tc : rseSellCommands)
                sellCommands.add(new TradeCommand(TradeCommand.direction.valueOf(tc.getRseDir()), TradeCommand.commandType.valueOf(tc.getRseType()), tc.getRseQuantity(), tc.getRsePrice(), tc.getRseSymbol(), LocalDateTime.parse(tc.getRseDateTime()), engine.getUsers().get(tc.getRseUser())));
        }
        List<Transaction> transactions = null;
        List<RseTransactions> rseTransactions = null;
        if (rs.getRseTransactions() != null) {
            transactions = new LinkedList<>();
            rseTransactions = rs.getRseTransactions();
            for (RseTransactions tran : rseTransactions)
                transactions.add(new Transaction(tran.getRseQuantity(), tran.getRsePrice(), LocalDateTime.parse(tran.getRseDateTime()), engine.getUsers().get(tran.getRseBuyer()), engine.getUsers().get(tran.getRseSeller()), tran.getRseStock()));
        }
        return new Stock(company, symbol, price, buyCommands, sellCommands, transactions);
    }

}