import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;import java.util.Scanner;


 enum Operations {
    LOAD(1,"load"),
    SHOW_ALL_STOCKS(2,"show all stocks"),
    SHOW_STOCK(3,"show stock"),
    TRADE(4,"trade"),
    SHOW_ALL_COMMANDS(5,"show all commands"),
    EXIT(6,"exit");

    private int opNum;
    private String opName;
    Operations(int num, String name) {
        this.opNum = num;
        this.opName = name;
    }

}

public class SystemMain {
    static private Trader data = new Engine(); // for interface use

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        // System.out.println("Please enter the XML full path: ");
        // String pathXML = in.nextLine();
        // add here reading the data from the file in the pathXML

        Operations op = null;

        do {
            printMainMenu();
            try {
            op =Operations.valueOf(in.nextLine().toLowerCase());
            } catch (IllegalArgumentException e) {
                System.out.println("You entered wrong input, Please try again.");
                System.out.println(e.getMessage());
             }
            executeOperation(op, in);

        } while (op != Operations.EXIT);
        System.out.println("Thank you for using our program and goodbye!");
    }

    private static void printMainMenu() {
        System.out.println("\n");
        System.out.println("Operation options menu:");
        System.out.println("1. Read data from file. \n" +
                "2. Show existing stocks.\n" +
                "3. Show chosen stock data. \n" +
                "4. Execution of a trading order. \n" +
                "5. View the lists of commands to execute. \n" +
                "6. Exit the program.\n");
        System.out.println("Please enter your next operation: (enter only number) ");
    }

    private static void executeOperation(Operations op, Scanner in) {
        switch (op) {
            case LOAD:
                // need to add here the method for getting data from XML file
                testOne();
                break;
            case SHOW_ALL_STOCKS:
                showAllStocks();
                break;
            case SHOW_STOCK:
                showStock(in);
                break;
            case TRADE:
                addTradeCommand(in);
                break;
            case SHOW_ALL_COMMANDS:
                showAllCommands();
                break;
            case EXIT:
                System.exit(0);
            default:
                System.out.println("\nYou entered wrong number. Chose only options from the main menu. ");
                break;
        }
    }

    public static void showAllStocks() { // second option in the main menu
        StockDT[] stocks = data.showAllStocks().toArray(new StockDT[0]);
        for(StockDT st:stocks){
            System.out.println(st.toString());
        }
    }

    public static void showStock(Scanner in) {
        try {
            System.out.println("Please enter the name of the stock you want to see: ");
            //in.nextLine(); // ignore newline
            String stockName = in.nextLine();

            StockDT stock = data.showStock(stockName); // throws exception if there isn't any company stocks with the given symbol
            System.out.println(stock.toString());
            //stock.showAllTransactions();
        } catch (InputMismatchException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void addTradeCommand(Scanner in){
        try {
            System.out.println("Please enter the symbol of the company you want to trade with it's stocks: ");
            String symbol = in.nextLine();
            System.out.println("Please choose the direction you want to trade: (enter: BUY/SELL) ");
            TradeCommand.direction direction = TradeCommand.direction.valueOf(in.nextLine().toUpperCase());
            System.out.println("Please choose the type of command you want to make: (enter: LMT/MKT/FOK/IOC)");
            TradeCommand.commandType type = TradeCommand.commandType.valueOf(in.nextLine().toUpperCase());
            System.out.println("Please enter how many stocks you want to trade in: (integer numbers only)");
            int quantity = in.nextInt();
            if(quantity<=0){
                System.out.println("The minimum quantity of stocks to trade is 1. Please try again.");
                return; // wrong input. cancel the command
            }
            System.out.println("Please enter the limit price you want to trade with: (floating point is permitted)");
            float price = in.nextFloat();
            in.nextLine();

            data.addTradeCommand(symbol,direction,type,quantity,price);
            System.out.println("The command entered to the system until a matching command will be found.");

        } catch (IllegalArgumentException e) {
            System.out.println("You entered wrong input  Please try again.");
            System.out.println(e.getMessage());
        }
 }

    static public void showAllCommands(){
        List<StockDT> stocks = data.showAllStocks();
        for(StockDT s:stocks) {
            System.out.print("The Stock: " + s.getSymbol() + " of " + s.getCompanyName() + "company ");
            List<TradeCommandDT> buy = s.getBuysCommands();
            System.out.println("has currently the following buy commands: ");
            if(buy.size() == 0)
                System.out.println("There is no buy commands.");
            else
                for(TradeCommandDT c: buy) {
                    System.out.println(c.toString());
                }
            System.out.println("Total buy transaction commands turnover: "+ s.getBuyTransTurnover());
            List<TradeCommandDT> sell = s.getSellsCommands();
            System.out.println("The company has currently the following sell commands: ");
            if(buy.size() ==0)
                System.out.println("There is no sell commands.");
            else
                for(TradeCommandDT c: sell) {
                    System.out.println(c.toString());
                }
            System.out.println("Total sell transaction commands turnover: "+ s.getSellTransTurnover());

            List<Transaction> transactions = s.getTransactions();
            System.out.println("The company's stock made transactions are: ");
            if(transactions.size() ==0)
                System.out.println("There are no transactions made.");
            else
                for(Transaction c: transactions) {
                    System.out.println(c.toString());
                }
            System.out.println("Total transaction turnover is: " + s.getTransTurnover());
        }
    }


    public static void testOne() {
        CompanyStocks company1 = new CompanyStocks("google", "Gogle", 100);
        CompanyStocks company2 = new CompanyStocks("amazon", "amzn", 200);
        CompanyStocks company3 = new CompanyStocks("tesla", "TSla", 300);
        Engine.addStock(company1);
        Engine.addStock(company2);
        Engine.addStock(company3);
        System.out.println("All the data has been uploaded. ");
    }
}
