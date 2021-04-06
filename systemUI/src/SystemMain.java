import java.io.IOException;
import java.util.*;

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
        try {
            //Operations op = null;
            int op = 6;
            do {
                printMainMenu();
                try {
                    //op = Operations.valueOf(in.nextLine().toUpperCase());
                    op = in.nextInt();
                    in.nextLine();
                } catch (IllegalArgumentException e) {
                    System.out.println("You entered wrong input, Please try again.");
                    System.out.println(e.getMessage());
                }
                executeOperation(op, in);

            } while (op!=6/*op != Operations.EXIT*/);
            System.out.println("Thank you for using our program and goodbye!");
            in.close();
        }catch(Exception e){
            System.out.println("There was a problem somewhere in the program.");
            System.out.println(e.getMessage());
            System.out.println("stack trace: ");
            System.out.println(e.getStackTrace());
            System.out.println("\n"+e.toString()+"\n");

            System.out.println("\n"+e.getCause()+"\n");
            e.printStackTrace();

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

    private static void executeOperation(/*Operations*/int op, Scanner in) throws IOException {
        switch (op) {
            case 1:
                // need to add here the method for getting data from XML file
                System.out.println("Insert the file's path: ");
                String path = in.nextLine();
                engine.uploadDataFromFile(path);
                break;
            case 2:
                showAllStocks();
                break;
            case 3:
                showStock(in);
                break;
            case 4:
                addTradeCommand(in);
                break;
            case 5:
                showAllCommands();
                break;
            case 6:
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
            String symbol = getSymbol(in);

            System.out.println("Please choose the direction you want to trade: (enter: BUY/SELL) ");
            TradeCommand.direction direction = getDir();

            System.out.println("Please choose the type of command you want to make: (enter: LMT/MKT/FOK/IOC)");
            TradeCommand.commandType type = getCommand();

            System.out.println("Please enter how many stocks you want to trade in: (integer numbers only)");
            int quantity = (int) getPositiveNum(true);
            System.out.println("Please enter the limit price you want to trade with: (a use of point  is permitted)");
            float price = getPositiveNum(false);


            System.out.println(data.addTradeCommand(symbol,direction,type,quantity,price));

        } catch (IllegalArgumentException e) {
            //System.out.println("You entered wrong input. There isn't any"+ e.getCause()+ "Please try again.");
            System.out.println(e.getMessage());
        }catch (InputMismatchException e) {
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
            System.out.println("Total transaction turnover is:" + s.getTransTurnover() + "\n");
        }
    }

    private static String getSymbol(Scanner in) {
        String symbol = in.nextLine().toUpperCase();
        if(!(engine.isSymbolExists(symbol)))
            throw new IllegalArgumentException("There isn't any company with "+ symbol +" symbol.");
        else if (!(CompanyStocks.symbolCheck(symbol)))
            throw new InputMismatchException("The given symbol is not valid. Symbol contains English letters only!");
        else
            return symbol;
    }

    private static TradeCommand.direction getDir() {
        Scanner in = new Scanner(System.in);
        String dir = in.nextLine().toUpperCase();
        TradeCommand.direction res = null;
        try{
            res.valueOf(dir);
            return res;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid option!");
        }
    }

    private static TradeCommand.commandType getCommand() {
        Scanner in = new Scanner(System.in);
        String cmd = in.nextLine().toUpperCase();
        TradeCommand.commandType res = null;
        try{
            res.valueOf(cmd);
            return res;
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid option!");
        }
    }

    private static float getPositiveNum(boolean isInteger) {
        Scanner in = new Scanner(System.in);
        float num;
        try {
            num = in.nextFloat();
            in.nextLine();
        } catch (InputMismatchException e) {
            if(isInteger)
                throw new InputMismatchException("Invalid input!, Enter an integer!");
            else
                throw new InputMismatchException("Invalid input!, Enter a real number!");
        }
        if (!(num >= 0))
            throw new InputMismatchException("Invalid Input!, should be a positive value!");
        else if (isInteger && (!(num - (int) num == 0)))
            throw new InputMismatchException("Invalid Input!, should be an integer!");
        else
            return num;
    }

    public static void testOne() {
        //CompanyStocks company1 = new CompanyStocks("google", "Gogle", 100);
        //CompanyStocks company2 = new CompanyStocks("amazon", "amzn", 200);
        //CompanyStocks company3 = new CompanyStocks("tesla", "TSla", 300);
        CompanyStocks company1 = new CompanyStocks("google", "GOGLE", 100);
        CompanyStocks company2 = new CompanyStocks("amazon", "AMZN", 200);
        CompanyStocks company3 = new CompanyStocks("tesla", "TSLA", 300);

        Engine.addStock(company1);
        Engine.addStock(company2);
        Engine.addStock(company3);
        System.out.println("All the data has been uploaded. ");
    }
}
