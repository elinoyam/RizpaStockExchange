import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.InputMismatchException;
import java.util.List;
import java.util.ResourceBundle;

public class PrimaryController implements Initializable {
    public Button BtnReset;
    public Button BtnSubmit;
    public Button BtnLoad;
    public Button BtnSave;
    public TextField TxtPrice;
    public TextField TxtQuantity;
    public ChoiceBox ChbSymbol;
    public ChoiceBox ChbType;
    public RadioButton RdioBuy;
    public RadioButton RdioSell;
    public Tab TabStock;
    public Tab TabAllStocks;
    public ChoiceBox ChbUser;
    public ProgressBar PBarStatus;
    public Text txtStatus;
    public ChoiceBox ChbStock;
    public ChoiceBox ChbView;
    public Label LblQuantity;
    public Label LblTotalValue;
    public Label LblCompany;
    public Label LblMktPrice;
    public Label LblOwnerName;
    public TableView stocksViewTable;
    public TableColumn SymbolClmn;
    public TableColumn CompanyNameClmn;
    public TableColumn MKTPriceClmn;
    public TableColumn TurnOverClmn;
    public TableColumn QuantityClmn;
    public RadioButton RdioMine;
    public RadioButton RdioAll;
    public Pane PaneOwner;
    public LineChart<LocalDateTime,Float> ChrtView;
    public TableView tradeViewTable;
    public TableView transViewTable;
    public HBox HBDetails;
    public Slider SlidEffects;
    public Slider SlidTheme;


    private User currentUser;
    private DoubleProperty readingProgress = new SimpleDoubleProperty();
    private StringProperty statusString = new SimpleStringProperty();
    private Object lock1 = new Object();

    private Engine RSEEngine;

enum View {

    TREND(1,"Share Price Tendency"),
    BUY_COMMANDS(2,"Buy Trade Commands"),
    SELL_COMMANDS(3,"Sell Trade Commands"),
    ALL_COMMANDS(4,"All Trade Commands"),
    TRANSACTIONS(5,"Transactions");

    private final int opNum;
    private final String opText;

    View(int num, String text) {
        this.opNum = num;
        this.opText = text;
    }

    public static View getView(int num){
        for(View o : View.values()){
            if(o.opNum == num) {
                return o;
            }
        }
        return null;
    }

    public static View getView(String text){
        for(View o : View.values()){
            if(o.opText.equals(text)) {
                return o;
            }
        }
        return null;
    }

    public int getNum() {
        return opNum;
    }

    public String getOpText() {
        return opText;
    }
}

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        RSEEngine = Engine.getInstance();


        for(TradeCommand.commandType c: TradeCommand.commandType.values())
            ChbType.getItems().add(c.toString());

        for(View v:View.values())
            ChbView.getItems().add(v.opText);


        ChbStock.valueProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (ChbStock.getItems().isEmpty()) {
                    LblQuantity.setText("Quantity:  <number>");
                    LblCompany.setText("Company: <name>");
                    LblTotalValue.setText("Total Value:  <number>");
                    LblMktPrice.setText("Market Price: <number>");
                    if(!ChrtView.getData().isEmpty())
                        ChrtView.getData().clear();
                } else {
                    String symbol = ChbStock.getValue().toString();
                    StockDT chosenStock = RSEEngine.getSingleStockData(symbol);
                    if(currentUser == null) {
                        LblQuantity.setText("Quantity: Admin has NaN");
                        LblTotalValue.setText("Total Value: Admin has NaN");

                    }else {
                        int numberOfHoldings = currentUser.getUserStockHoldings(symbol);
                        LblQuantity.setText("Quantity:  " + numberOfHoldings);
                        LblTotalValue.setText("Total Value: " + chosenStock.getSharePrice() * numberOfHoldings);
                    }
                    LblCompany.setText("Company: " + chosenStock.getCompanyName());
                    LblMktPrice.setText("Market Price: " + chosenStock.getSharePrice());
                    if(!(ChbView.getValue() == null) &&ChbView.getValue().toString()=="Share Price Tendency")
                        showStockTransInLineChart(symbol);
                }

            }
        });

        readingProgress.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                PBarStatus.progressProperty().bind(readingProgress);

            }
        });
        PBarStatus.setDisable(false);
        readingProgress.setValue(0);
        statusString.addListener((observable, oldValue, newValue) -> {
            Platform.runLater(()->{
                txtStatus.setText(statusString.getValue());});
        });

        SymbolClmn.setCellValueFactory(new PropertyValueFactory<StockDT,String>("symbol"));
        CompanyNameClmn.setCellValueFactory(new PropertyValueFactory<StockDT, String>("companyName"));
        QuantityClmn.setCellValueFactory(new PropertyValueFactory<StockDT, Integer>("quantity"));
        MKTPriceClmn.setCellValueFactory(new PropertyValueFactory<StockDT, Float>("sharePrice"));
        TurnOverClmn.setCellValueFactory(new PropertyValueFactory<StockDT, Float>("transactionsTurnOver"));

    }

    public void Save(ActionEvent actionEvent) {
    }

    public void Load(ActionEvent actionEvent) throws JAXBException, FileNotFoundException, InterruptedException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open XML File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML File", "*.xml"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {


            ChbStock.getItems().removeAll(ChbStock.getItems());
            ChbUser.getItems().removeAll(ChbUser.getItems());
            ChbSymbol.getItems().removeAll(ChbSymbol.getItems());

            new Thread(()->{
                try {synchronized (lock1) {
                    BtnSave.setDisable(true);
                    ChbUser.setDisable(true);
                    BtnReset.setDisable(true);
                    BtnSubmit.setDisable(true);
                    ChbSymbol.setDisable(true);
                    ChbType.setDisable(true);
                    TxtQuantity.setDisable(true);
                    TxtPrice.setDisable(true);
                    TabStock.setDisable(true);
                    TabAllStocks.setDisable(true);
                    RdioSell.setDisable(true);
                    RdioBuy.setDisable(true);


                    readingProgress.setValue(0);
                    statusString.setValue("Fetching File..");
                    txtStatus.setVisible(true);
                    lock1.wait(1000);
                    RSEEngine.uploadDataFromFile(selectedFile.getAbsolutePath(),readingProgress,statusString);

                    lock1.wait(500);
                    statusString.setValue("Reading of file completed!");
                    readingProgress.setValue(1);
                    Platform.runLater(()->{
                        StockDT[] stocks = RSEEngine.showAllStocks().toArray(new StockDT[0]); //gets an array of the existing stocks
                        for (StockDT st : stocks) {                                        //for each stock it prints the full data about the stock.
                            System.out.println(st.toString());
                        } // TODO: before submission we need to delete it


                        updateSymbolsToAll("All",true,true);
                        updateStocksTView("All");

                        for(User u:RSEEngine.getUsers().values())
                            ChbUser.getItems().add(u.getUserName());
                        ChbUser.getItems().add("Admin");
                        ChbUser.setValue("Admin");
                        RdioMine.setDisable(true);
                        RdioMine.setSelected(false);

                        BtnSave.setDisable(false);
                        ChbUser.setDisable(false);
                        BtnReset.setDisable(false);
//                        BtnSubmit.setDisable(false);
                        ChbSymbol.setDisable(false);
                        ChbType.setDisable(false);
                        TxtQuantity.setDisable(false);
                        TxtPrice.setDisable(false);
                        TabStock.setDisable(false);
                        TabAllStocks.setDisable(false);
                        RdioSell.setDisable(false);
                        RdioBuy.setDisable(false);
                        RdioBuy.setSelected(true);
                    });
                }
                } catch (FileNotFoundException | JAXBException | InterruptedException|NullPointerException e) {
                    System.out.println("Problem in reading the Xml file.\n");
                    System.out.println(e.getMessage());
                    e.printStackTrace();

                    Platform.runLater(()->{
                        txtStatus.setText("Invalid File." /*+ e.getMessage()*/ + " Please select a new valid file.");
                        readingProgress.setValue(0);}
                        );
                    Thread.currentThread().stop();

                }
            }).start();

        }
    }

    public void Reset(ActionEvent actionEvent) {
        RdioBuy.setSelected(true);
        updateSymbolsToAll("All",true,false);
        TxtPrice.clear();
        TxtQuantity.clear();
        ChbType.getSelectionModel().clearSelection();
        ChbSymbol.getSelectionModel().clearSelection();
    }

    public void BuyStockClicked(ActionEvent actionEvent) {

        updateSymbolsToAll("All",true,false);
    }

    public void SellStockClicked(ActionEvent actionEvent) {
        if(currentUser== null)
            return;
        if(currentUser.getUserName().equals("Admin"))
            throw new InputMismatchException("Admin can't make buy/sell commands. You must pick a user.");

        updateSymbolsToAll(currentUser.getUserName(),true,false);
    }

    public void Submit(ActionEvent actionEvent) {
        if(ChbSymbol.getValue() == null || ChbType.getValue() == null || TxtQuantity.getText().isEmpty()) {
            txtStatus.setText("All fields must be filled to submit a trade command request.");
            return;
        }
        else if((!ChbType.getValue().equals("MKT")) && TxtPrice.getText().isEmpty() ){
            txtStatus.setText("If not MKT command, you must enter a wanted price.");
            return;
        }
        try {
            TradeCommand.direction dir;
            TradeCommand.commandType type = TradeCommand.commandType.valueOf(ChbType.getValue().toString());
            if (RdioSell.isSelected())
                dir = TradeCommand.direction.SELL;
            else
                dir = TradeCommand.direction.BUY;

            float price = ChbType.getValue().equals("MKT") ? 0 : Float.parseFloat(TxtPrice.getText());
            String msg = RSEEngine.addTradeCommand(ChbSymbol.getValue().toString(), dir, type, Integer.parseInt(TxtQuantity.getText()), price, RSEEngine.getUser(ChbUser.getValue().toString()));
            txtStatus.setText(msg);
            txtStatus.setVisible(true);
            Reset(null);
            updateSymbolsToAll(currentUser.getUserName(), false, true);
            updateStocksTView(RdioMine.isSelected() ? currentUser.getUserName() : "All");
        }catch (IllegalArgumentException e){
            txtStatus.setText(e.getMessage());
            txtStatus.setVisible(true);
            Reset(null);
        }
    }

    public void userChosen(ActionEvent actionEvent) {
        if (ChbUser.getValue() == null)
            return;
        if (ChbUser.getValue().equals("Admin")) {
            BtnSubmit.setDisable(true);
            updateSymbolsToAll("All",true,true);
            RdioAll.setSelected(true);
            RdioMine.setDisable(true);
            currentUser = null;
            updateStocksTView("All");

            LblOwnerName.setText("Admin doesn't own stocks: ");
            HBDetails.getChildren().remove(PaneOwner);

        } else {
            // for the show stock tab
            RdioMine.setDisable(false);
            BtnSubmit.setDisable(false);
            TabStock.setDisable(false);
            ChbStock.getItems().clear();
            String currentUserName = ChbUser.getValue().toString();
            currentUser = RSEEngine.getUser(currentUserName);

            LblOwnerName.setText(currentUserName + " owns: ");
            if (RdioSell.isSelected())
                updateSymbolsToAll(currentUserName,true,true);

            else
                updateSymbolsToAll(currentUserName,false,true);

            if(RdioMine.isSelected())
                updateStocksTView(currentUserName);
            else
                updateStocksTView("All");
            if(TabStock.isSelected() && !(ChbView.getValue()== null) && ChbView.getValue().toString().equals("Share Price Tendency") && !(ChbStock.getValue()==(null)))
                showStockTransInLineChart(ChbStock.getValue().toString());

            if(!HBDetails.getChildren().contains(PaneOwner))
                HBDetails.getChildren().add(0,PaneOwner);
        }
    }

    /**
     * @param string the name of the user that we want to update according to his holdings OR "All" if we want to update to all the stocks in the system
     * @param symbol true if we want to update the items of the symbol choice box (in the add command part)
     * @param stock  true if we want to update the items of the stock choice box (in the show stock tab)
     */
    private void updateSymbolsToAll(String string, boolean symbol, boolean stock){
        if(symbol)
            ChbSymbol.getItems().clear();
        if(stock)
            ChbStock.getItems().clear();

        if(string == "All"){        // all the stocks in the engine
            List<StockDT> Stocks = RSEEngine.showAllStocks();
            for(StockDT Stock:Stocks) {
                if(symbol)
                    ChbSymbol.getItems().add(Stock.getSymbol());
                if(stock)
                    ChbStock.getItems().add(Stock.getSymbol());
            }
        }
        else{   // need to update to a specified user holdings
            currentUser = RSEEngine.getUser(string);
            for (UserHoldings hold : currentUser.getUserStocks().values()) {   // can show only the stocks that are in the user holdings
                if(symbol)
                    ChbSymbol.getItems().add(hold.getSymbol());
                if(stock)
                    ChbStock.getItems().add(hold.getSymbol());
            }

        }
    }

    private void updateStocksTView(String string){
        stocksViewTable.getItems().clear();

        if(string == "All"){
            List<StockDT> Stocks = RSEEngine.showAllStocks();
            for(StockDT stock:Stocks) {
                stocksViewTable.getItems().add(stock);
            }
            QuantityClmn.setVisible(false);
        }
        else{
            currentUser = RSEEngine.getUser(string);
            for (UserHoldings hold : currentUser.getUserStocks().values()) {   // can show only the stocks that are in the user holdings
                StockDT stock = RSEEngine.showStock(hold.getStock().getSymbol());
                stock.setQuantity(currentUser.getUserStockHoldings(hold.getStock().getSymbol()));
                stocksViewTable.getItems().add(stock);
            }
            QuantityClmn.setVisible(true);
        }
    }

    public void RdioAllClicked(ActionEvent actionEvent) {
        updateStocksTView("All");
    }

    public void RdioMineClicked(ActionEvent actionEvent) {
        updateStocksTView(currentUser.getUserName());
    }

    public void typeChosen(ActionEvent actionEvent) {
        if (ChbType.getValue() == null)
            TxtPrice.setDisable(false);
        else if(ChbType.getValue().equals(TradeCommand.commandType.MKT.toString())) {
            TxtPrice.clear();
            TxtPrice.setDisable(true);
        }
        else
            TxtPrice.setDisable(false);
    }

    public void viewChosen(ActionEvent actionEvent) {
        View choice = View.getView(ChbView.getValue().toString());
        switch (choice) {
            case TREND:
                transViewTable.setVisible(false);
                tradeViewTable.setVisible(false);
                ChrtView.setVisible(true);
                if(!(ChbStock.getValue()==null))
                    showStockTransInLineChart(ChbStock.getValue().toString());
                break;
            case BUY_COMMANDS:
                transViewTable.setVisible(false);
                tradeViewTable.setVisible(true);
                ChrtView.setVisible(false);
                break;
            case SELL_COMMANDS:
                transViewTable.setVisible(false);
                tradeViewTable.setVisible(true);
                ChrtView.setVisible(false);
                break;
            case ALL_COMMANDS:
                transViewTable.setVisible(false);
                tradeViewTable.setVisible(true);
                ChrtView.setVisible(false);
                break;
            case TRANSACTIONS:
                transViewTable.setVisible(true);
                tradeViewTable.setVisible(false);
                ChrtView.setVisible(false);
                break;
        }

    }

    public void showStockTransInLineChart(String symbol){
        ChrtView.setDisable(false);
        if(!ChrtView.getData().isEmpty())
            ChrtView.getData().clear();
        StockDT stock = RSEEngine.showStock(symbol);
        List<Transaction> transactions = stock.getTransactions();

        XYChart.Series stockMKTPrice = new XYChart.Series();

        stockMKTPrice.setName(stock.getSymbol() +" market price");
        for(int i = transactions.size()-1;i>=0;i--){//Transaction tran: transactions){
            stockMKTPrice.getData().add(new XYChart.Data<>(" " +transactions.get(i).getDateStamp().format(Transaction.getDateTimeFormat()).toString()+" ",transactions.get(i).getPrice()));
        }

        ChrtView.getData().add(stockMKTPrice);

    }
}

