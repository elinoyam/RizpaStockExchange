import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
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
    public Label LblStatus;
    public ChoiceBox ChbStock;
    public ChoiceBox ChbView;
    public Label LblQuantity;
    public Label LblTotalValue;
    public Label LblCompany;
    public Label LblMktPrice;
    public Label LblOwnerName;
    private User currentUser;

    private Engine RSEEngine;






    @Override
    public void initialize(URL location, ResourceBundle resources) {
        RSEEngine = Engine.getInstance();
        for(TradeCommand.commandType c: TradeCommand.commandType.values())
            ChbType.getItems().add(c.toString());
        ChbStock.valueProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                StockDT chosenStock = RSEEngine.getSingleStockData(ChbStock.getValue().toString());
                int numberOfHoldings = currentUser.getUserStockHoldings(ChbStock.getValue().toString());
                LblQuantity.setText("Quantity:  " + numberOfHoldings);
                LblCompany.setText("Company: " +chosenStock.getCompanyName());
                LblTotalValue.setText("Total Value: "+chosenStock.getSharePrice()*numberOfHoldings);
                LblMktPrice.setText("Market Price: " + chosenStock.getSharePrice());
            }
        });

    }


    public void Save(ActionEvent actionEvent) {
    }

    public void Load(ActionEvent actionEvent) throws JAXBException, FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open XML File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML File", "*.xml"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            BtnSave.setDisable(false);
            ChbUser.setDisable(false);
            BtnReset.setDisable(false);
            BtnSubmit.setDisable(false);
            ChbSymbol.setDisable(false);
            ChbType.setDisable(false);
            TxtQuantity.setDisable(false);
            TxtPrice.setDisable(false);
            PBarStatus.setDisable(false);
            TabStock.setDisable(false);
            TabAllStocks.setDisable(false);
            RdioSell.setDisable(false);
            RdioBuy.setDisable(false);
            RdioBuy.setSelected(true);

            //ChbSymbol.getItems().re
            ChbStock.getItems().removeAll(ChbStock.getItems());
            ChbUser.getItems().removeAll(ChbUser.getItems());
            ChbSymbol.getItems().removeAll(ChbSymbol.getItems());

            RSEEngine.uploadDataFromFile(selectedFile.getAbsolutePath());
            StockDT[] stocks = RSEEngine.showAllStocks().toArray(new StockDT[0]); //gets an array of the existing stocks
            for (StockDT st : stocks) {                                        //for each stock it prints the full data about the stock.
                System.out.println(st.toString());
            }

            List<StockDT> Stocks = RSEEngine.showAllStocks();
            for(StockDT Stock:Stocks) {
                ChbSymbol.getItems().add(Stock.getSymbol());
                ChbStock.getItems().add(Stock.getSymbol());
            }

            for(User u:RSEEngine.getUsers().values())
                ChbUser.getItems().add(u.getUserName());
            ChbUser.getItems().add("Admin");
        }


    }

    public void Reset(ActionEvent actionEvent) {
        RdioBuy.setSelected(true);
        TxtPrice.clear();
        TxtQuantity.clear();
        ChbType.getSelectionModel().clearSelection();
        ChbSymbol.getSelectionModel().clearSelection();


    }

    public void BuyStockClicked(ActionEvent actionEvent) {
        ChbSymbol.getItems().clear();
        List<StockDT> Stocks = RSEEngine.showAllStocks();
        for(StockDT Stock:Stocks) {
            ChbSymbol.getItems().add(Stock.getSymbol());
        }
    }

    public void SellStockClicked(ActionEvent actionEvent) {
        if(currentUser== null)
            return;
        if(currentUser.getUserName().equals("Admin"))
            throw new InputMismatchException("Admin can't make buy/sell commands. You must pick a user.");

        ChbSymbol.getItems().clear();

        for(UserHoldings hold:currentUser.getUserStocks().values()) {
            ChbSymbol.getItems().add(hold.getSymbol());
        }
    }

    public void Submit(ActionEvent actionEvent) {
        if(ChbSymbol.getValue() == null || ChbType.getValue() == null ||TxtQuantity.getText().isEmpty() || TxtPrice.getText().isEmpty()) {
            LblStatus.setText("All fields must be filled to submit a trade command request.");
            return;
        }
        TradeCommand.direction dir;
        TradeCommand.commandType type = TradeCommand.commandType.valueOf(ChbType.getValue().toString());
        if (RdioSell.isSelected())
            dir = TradeCommand.direction.SELL;
        else
            dir = TradeCommand.direction.BUY;
        String msg = RSEEngine.addTradeCommand(ChbSymbol.getValue().toString(),dir,type,Integer.parseInt(TxtQuantity.getText()),Float.parseFloat(TxtPrice.getText()),RSEEngine.getUser(ChbUser.getValue().toString()));
        LblStatus.setText(msg);
        LblStatus.setVisible(true);
        Reset(null);
    }

    public void userChosen(ActionEvent actionEvent) {
        if(ChbUser.getValue().equals(null))
            return;
        // for the show stock tab
       ChbStock.getItems().clear();
        String currentUserName = ChbUser.getValue().toString();
        currentUser = RSEEngine.getUser(currentUserName);
        for(UserHoldings hold:currentUser.getUserStocks().values()) {   // can show only the stocks that are in the user holdings
            ChbStock.getItems().add(hold.getSymbol());
        }
        LblOwnerName.setText(currentUserName + " owns: ");
        if(RdioSell.isSelected()){
            ChbSymbol.getItems().clear();

            for(UserHoldings hold:currentUser.getUserStocks().values()) {
                ChbSymbol.getItems().add(hold.getSymbol());
            }
        }
    }

}
