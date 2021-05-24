import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableListValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
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

    private Engine RSEEngine;






    @Override
    public void initialize(URL location, ResourceBundle resources) {
        RSEEngine = Engine.getInstance();
        for(TradeCommand.commandType c: TradeCommand.commandType.values())
            ChbType.getItems().add(c.toString());
        ChbStock.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                LblMktPrice.setText("Market Price: " + RSEEngine.showAllStocks().get(newValue.intValue()).getSharePrice());
                LblCompany.setText(("Company: " + RSEEngine.showAllStocks().get(newValue.intValue()).getCompanyName()));
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
    }

    public void SellStockClicked(ActionEvent actionEvent) {
    }

    public void Submit(ActionEvent actionEvent) {
    }
}
