
import java.util.List;

public interface Trader {

 List<StockDT> showAllStocks(); // second option
 StockDT showStock(String companySymbol); // third option
 void uploadDataFromFile(String path); // first option
 List<StockDT> showAllCommands();
 String addTradeCommand(String companySymbol,TradeCommand.direction dir, TradeCommand.commandType command, int quantity, float wantedPrice);

}
