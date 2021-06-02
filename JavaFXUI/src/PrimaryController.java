import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
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
import java.util.stream.Collectors;

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
    public Tab TabPortfolio;
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
    public HBox HBDetails;
    public Slider SlidEffects;
    public Slider SlidTheme;

    public VBox ttVBox;
    //public HBox HBView;
    public HBox HBoxTrade;
    public HBox HBoxTrans;
    public HBox HBoxRdio;
    public TableView transViewTable;
    public TableView tradeViewTable;
    public TableColumn TSColumn;
    public TableColumn TSColumn1;
    public TableColumn UserColumn;
    public TableColumn SellerColumn;
    public TableColumn BuyerColumn;
    public TableColumn TypeColumn;
    public TableColumn PColumn;
    public TableColumn QColumn;
    public TableColumn TColumn;
    public TableColumn PColumn1;
    public TableColumn QColumn1;
    public TableColumn TColumn1;
    public TableColumn DirColumn;
    public RadioButton rdioViewMine;
    public RadioButton rdioViewAll;



    public Text txtWelcome;
    public Text txtTotalWorth;
    public PieChart ChrtShares;
    public StackedAreaChart ChrtWorth;






    private User currentUser;
    private DoubleProperty readingProgress = new SimpleDoubleProperty();
    private StringProperty statusString = new SimpleStringProperty();
    private Object lock1 = new Object();
    private DoubleProperty styleSliderChanged = new SimpleDoubleProperty();
    private Engine RSEEngine;

    public double isStyleSliderChanged() {
        return styleSliderChanged.get();
    }

    public DoubleProperty styleSliderChangedProperty() {
        return styleSliderChanged;
    }

    public void setStyleSliderChanged(double styleSliderChanged) {
        this.styleSliderChanged.set(styleSliderChanged);
    }

    public void symbolChosen(ActionEvent actionEvent) {
        if(!ChbType.getSelectionModel().isEmpty() && TradeCommand.commandType.valueOf(ChbType.getValue().toString()) == TradeCommand.commandType.MKT && !ChbSymbol.getSelectionModel().isEmpty()) {
            StockDT sDT = RSEEngine.getSingleStockData(ChbSymbol.getValue().toString());
            TxtPrice.setText(""+sDT.getSharePrice());
         }
    }

    public void AllViewSelected(ActionEvent actionEvent) {
        UserColumn.setVisible(true);
        tradeCommandViewUpdate(currentUser==null ? "All":currentUser.getUserName());
        transViewUpdate(currentUser==null ? "All":currentUser.getUserName());
    }

    public void MineViewSelected(ActionEvent actionEvent) {
        UserColumn.setVisible(false);
        tradeCommandViewUpdate(currentUser==null ? "All":currentUser.getUserName());
        transViewUpdate(currentUser==null ? "All":currentUser.getUserName());
    }

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

        ttVBox.getChildren().removeAll(HBoxTrade,HBoxTrans,HBoxRdio);

        styleSliderChanged.bind(SlidTheme.valueProperty());

        for(TradeCommand.commandType c: TradeCommand.commandType.values())
            ChbType.getItems().add(c.toString());

        for(View v:View.values())
            ChbView.getItems().add(v.opText);

        TabPortfolio.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(currentUser!=null && TabPortfolio.isSelected()){
                portfolioUpdate(currentUser.getUserName());
            }});

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
                    ChbView.setDisable(true);
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
                    ChbView.setDisable(false);
                    tradeCommandViewUpdate(currentUser==null ? "All":currentUser.getUserName());
                    transViewUpdate(currentUser==null ? "All":currentUser.getUserName());
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

        TSColumn1.setComparator(TSColumn1.getComparator().reversed());
        tradeViewTable.getSortOrder().add(TSColumn1);


        //Stocks
        SymbolClmn.setCellValueFactory(new PropertyValueFactory<StockDT,String>("symbol"));
        CompanyNameClmn.setCellValueFactory(new PropertyValueFactory<StockDT, String>("companyName"));
        QuantityClmn.setCellValueFactory(new PropertyValueFactory<StockDT, Integer>("quantity"));
        MKTPriceClmn.setCellValueFactory(new PropertyValueFactory<StockDT, Float>("sharePrice"));
        TurnOverClmn.setCellValueFactory(new PropertyValueFactory<StockDT, Float>("transactionsTurnOver"));

        //TradeCommands
        TSColumn1.setCellValueFactory(new PropertyValueFactory<TradeCommandDT, String>("formattedDateTime"));
        UserColumn.setCellValueFactory(new PropertyValueFactory<TradeCommandDT, String>("user"));
        TypeColumn.setCellValueFactory(new PropertyValueFactory<TradeCommandDT, String>("commandType"));
        QColumn1.setCellValueFactory(new PropertyValueFactory<TradeCommandDT, String>("quantity"));
        PColumn1.setCellValueFactory(new PropertyValueFactory<TradeCommandDT, String>("wantedPrice"));
        TColumn1.setCellValueFactory(new PropertyValueFactory<TradeCommandDT, String>("turnover"));
        DirColumn.setCellValueFactory(new PropertyValueFactory<TradeCommandDT, String>("direction"));

        //Transaction
        TSColumn.setCellValueFactory(new PropertyValueFactory<Transaction, String>("formattedTimestamp"));
        SellerColumn.setCellValueFactory(new PropertyValueFactory<Transaction, String>("seller"));
        BuyerColumn.setCellValueFactory(new PropertyValueFactory<Transaction, String>("buyer"));
        QColumn.setCellValueFactory(new PropertyValueFactory<Transaction, String>("quantity"));
        TColumn.setCellValueFactory(new PropertyValueFactory<Transaction, String>("turnover"));
        PColumn.setCellValueFactory(new PropertyValueFactory<Transaction, String>("price"));
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
                        TabPortfolio.setDisable(true);

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
            txtTotalWorth.setText("Total Worth: "+currentUser.getTotalHoldingsValue());
        }catch (IllegalArgumentException e){
            txtStatus.setText(e.getMessage());
            txtStatus.setVisible(true);
            Reset(null);
        }
        tradeCommandViewUpdate(currentUser==null ? "All":currentUser.getUserName());
        transViewUpdate(currentUser==null ? "All":currentUser.getUserName());
    }

    public void userChosen(ActionEvent actionEvent) {
        if (ChbUser.getValue() == null)
            return;
        if (ChbUser.getValue().equals("Admin")) {
            BtnSubmit.setDisable(true);
            updateSymbolsToAll("All",true,true);
            RdioAll.setSelected(true);
            RdioMine.setDisable(true);
            rdioViewAll.setSelected(true);
            rdioViewMine.setDisable(true);
            currentUser = null;
            updateStocksTView("All");
            TabPortfolio.setDisable(true);
            LblOwnerName.setText("Admin doesn't own stocks: ");
            HBDetails.getChildren().remove(PaneOwner);

        } else {
            // for the show stock tab
            RdioMine.setDisable(false);
            rdioViewMine.setDisable(false);
            BtnSubmit.setDisable(false);
            TabStock.setDisable(false);
            ChbStock.getItems().clear();
            String currentUserName = ChbUser.getValue().toString();
            currentUser = RSEEngine.getUser(currentUserName);

            LblOwnerName.setText(currentUserName + " owns: ");
            TabPortfolio.setDisable(false);
            portfolioUpdate(ChbUser.getValue().toString());

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
            if (!ChbSymbol.getSelectionModel().isEmpty()) {
                StockDT sDT = RSEEngine.getSingleStockData(ChbSymbol.getValue().toString());
                TxtPrice.setText(""+sDT.getSharePrice());
            }
            else
                TxtPrice.clear();
            TxtPrice.setDisable(true);
        }
        else
            TxtPrice.setDisable(false);
    }

    public void viewChosen(ActionEvent actionEvent) {

        if(ChbView.getValue()==null)
            return;
        View choice = View.getView(ChbView.getValue().toString());

        transViewTable.getItems().clear();
        DirColumn.setVisible(false);
        if(!ttVBox.getChildren().isEmpty())
            ttVBox.getChildren().clear();


        switch (choice) {
            case TREND:
                ttVBox.getChildren().add(ChrtView);
                if(!(ChbStock.getValue()==null))
                    showStockTransInLineChart(ChbStock.getValue().toString());
                break;

            case BUY_COMMANDS:
                ttVBox.getChildren().addAll(HBoxRdio,HBoxTrade);
                tradeCommandViewUpdate(currentUser == null ? "All":currentUser.toString());
                break;

            case SELL_COMMANDS:
                ttVBox.getChildren().addAll(HBoxRdio,HBoxTrade);
                tradeCommandViewUpdate(currentUser == null ? "All":currentUser.toString());
                break;

            case ALL_COMMANDS:
                DirColumn.setVisible(true);
                ttVBox.getChildren().addAll(HBoxRdio,HBoxTrade);
                tradeCommandViewUpdate(currentUser == null ? "All":currentUser.toString());
                break;

            case TRANSACTIONS:
                ttVBox.getChildren().addAll(HBoxRdio,HBoxTrans);
                transViewUpdate(currentUser == null ? "All":currentUser.toString());
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

    public void tradeCommandViewUpdate(String user) {
        if(ChbView.getValue()==null || ChbStock.getValue()==null)
            return;

        tradeViewTable.getItems().clear();
        if(!rdioViewMine.isSelected())
            user = "All";

        if (user.equals("All")) {
            switch (ChbView.getValue().toString()) {
                case "Buy Trade Commands":
                    tradeViewTable.getItems().addAll(RSEEngine.getSingleStockData(ChbStock.getValue().toString()).getBuysCommands());
                    break;
                case "Sell Trade Commands":
                    tradeViewTable.getItems().addAll(RSEEngine.getSingleStockData(ChbStock.getValue().toString()).getSellsCommands());
                    break;
                case "All Trade Commands":
                    tradeViewTable.getItems().addAll(RSEEngine.getSingleStockData(ChbStock.getValue().toString()).getBuysCommands());
                    tradeViewTable.getItems().addAll(RSEEngine.getSingleStockData(ChbStock.getValue().toString()).getSellsCommands());
                default:
                    return;
            }

        } else {
            currentUser = RSEEngine.getUser(user);
            switch (ChbView.getValue().toString()) {
                case "Buy Trade Commands":
                    if (!currentUser.getUserBuyCommands().isEmpty()) {
                        for (TradeCommand tc : currentUser.getUserBuyCommands().values()) {
                            if (tc.getSymbol().equals(ChbStock.getValue().toString())) {
                                TradeCommandDT dt = new TradeCommandDT(tc.getDirection(), tc.getQuantity(), tc.getSymbol(), tc.getPrice(), tc.getDate(), tc.getCommandType(), currentUser);
                                tradeViewTable.getItems().add(dt);
                            }
                        }
                    }
                    break;
                case "Sell Trade Commands":
                    if (!currentUser.getUserSellCommands().isEmpty()) {
                        for (TradeCommand tc : currentUser.getUserSellCommands().values()) {
                            if (tc.getSymbol().equals(ChbStock.getValue().toString())) {
                                TradeCommandDT dt = new TradeCommandDT(tc.getDirection(), tc.getQuantity(), tc.getSymbol(), tc.getPrice(), tc.getDate(), tc.getCommandType(), currentUser);
                                tradeViewTable.getItems().add(dt);
                            }
                        }
                    }
                    break;
                case "All Trade Commands":
                    if (!currentUser.getUserBuyCommands().isEmpty()) {
                        for (TradeCommand tc : currentUser.getUserBuyCommands().values()) {
                            if (tc.getSymbol().equals(ChbStock.getValue().toString())) {
                                TradeCommandDT dt = new TradeCommandDT(tc.getDirection(), tc.getQuantity(), tc.getSymbol(), tc.getPrice(), tc.getDate(), tc.getCommandType(), currentUser);
                                tradeViewTable.getItems().add(dt);
                            }
                        }
                    }
                    if (!currentUser.getUserSellCommands().isEmpty()) {
                        for (TradeCommand tc : currentUser.getUserSellCommands().values()) {
                            if (tc.getSymbol().equals(ChbStock.getValue().toString())) {
                                TradeCommandDT dt = new TradeCommandDT(tc.getDirection(), tc.getQuantity(), tc.getSymbol(), tc.getPrice(), tc.getDate(), tc.getCommandType(), currentUser);
                                tradeViewTable.getItems().add(dt);
                            }
                        }
                    }
                    break;
                default:
                    return;
            }
        }
        tradeViewTable.sort();
    }

    public void transViewUpdate(String user){

        if(ChbStock.getValue()==null)
            return;

        transViewTable.getItems().clear();

        if(user.equals("All") || rdioViewAll.isSelected()) {
            transViewTable.getItems().addAll(RSEEngine.showAllTransactions().values()
                    .stream()
                    .filter(p-> p.getSymbol().equals(ChbStock.getValue()))
                    .collect(Collectors.toList()));
        } else {
            currentUser = RSEEngine.getUser(user);
            transViewTable.getItems().addAll(currentUser.getUserTransactions()
                 .stream()
                 .filter(p-> p.getSymbol().equals(ChbStock.getValue()))
                    .collect(Collectors.toList()));
        }
    }

    public void portfolioUpdate(String user){
        if(user == null || user.equals("All"))
            return;

        txtWelcome.setText(currentUser.getUserName()+ "'s Portfolio");
        txtTotalWorth.setText("Total Worth: "+currentUser.getTotalHoldingsValue());
        List<PieChart.Data> sharesData = currentUser.getUserStocks().values()
                .stream()
                .map(p-> new PieChart.Data(p.getSymbol(),p.getTotalHold()))
                .collect(Collectors.toList());
        ObservableList<PieChart.Data> shares = FXCollections.observableArrayList(sharesData);
        ChrtShares.setData(shares);
        sharesData.forEach(data -> data.nameProperty().bind(
                Bindings.concat(data.getName(),"\n", data.pieValueProperty())));
    }
}

