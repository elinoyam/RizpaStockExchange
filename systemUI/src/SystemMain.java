import java.util.*;




public class SystemMain {
    static private Trader data = new Engine(); // for interface use

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        // System.out.println("Please enter the XML full path: ");
        // String pathXML = in.nextLine();
        // add here reading the data from the file in the pathXML
        try {
            Operations op = null;

            do {
                printMainMenu();
                try {
                    op = Operations.valueOf(in.nextLine().toUpperCase());
                } catch (IllegalArgumentException e) {
                    System.out.println("You entered wrong input, Please try again.");
                    System.out.println(e.getMessage());
                }
                executeOperation(op, in);

            } while (op != Operations.EXIT);
            System.out.println("Thank you for using our program and goodbye!");
            in.close();
        }catch(Exception e){
            System.out.println("There was a problem somewhere in the program.");
            System.out.println(e.getMessage());
            System.out.println("stack trace: ");
            System.out.println(e.getStackTrace());
        }

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
        try {
            StockDT[] stocks = data.showAllStocks().toArray(new StockDT[0]);
            for (StockDT st : stocks) {
                System.out.println(st.toString());
            }
        }catch (Exception e){
            System.out.println("Exception was caught in showAllStocks in SystemMain. ");
            System.out.println(e.getMessage());
        }

    }

    public static void showStock(Scanner in) {
        try {
            System.out.println("Please enter the name of the stock you want to see: ");
            String stockName = in.nextLine();

            StockDT stock = data.showStock(stockName); // throws exception if there isn't any company stocks with the given symbol
            System.out.println(stock.toString());
            System.out.println("This stock transactions are:");
            for(Transaction transaction: stock.getTransactions()){
                System.out.println(transaction.toString());
            }

        } catch (InputMismatchException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void addTradeCommand(Scanner in){
        try {
            System.out.println("Please enter the symbol of the company you want to trade with it's stocks: ");
            String symbol = getSymbol();

            System.out.println("Please choose the direction you want to trade: (enter: BUY/SELL) ");
            /*String tempDir = in.nextLine().toUpperCase();
            List<String> list = new ArrayList<>();
            for (TradeCommand.direction direction1 : TradeCommand.direction.values()) {
                list.add(direction1.name());
            }
            String [] check = list.toArray();
            Enum.valueOf(TradeCommand.direction.,tempDir);*/
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


            System.out.println(data.addTradeCommand(symbol,direction,type,quantity,price));

        } catch (IllegalArgumentException e) {
            System.out.println("You entered wrong input. There isn't any"+ e.getCause()+ "Please try again.");
            System.out.println(e.getMessage());
        }
 }

    static public void showAllCommands(){
        List<StockDT> stocks = data.showAllStocks();
        for(StockDT s:stocks) {
            System.out.print("\nThe Stock: " + s.getSymbol() + " of " + s.getCompanyName() + " company ");
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
            System.out.println("\nThe company has currently the following sell commands: ");
            if(sell.size() ==0)
                System.out.println("There is no sell commands.");
            else
                for(TradeCommandDT c: sell) {
                    System.out.println(c.toString());
                }
            System.out.println("Total sell transaction commands turnover: "+ s.getSellTransTurnover());

            List<Transaction> transactions = s.getTransactions();
            System.out.println("\nThe company's stock made transactions are: ");
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
