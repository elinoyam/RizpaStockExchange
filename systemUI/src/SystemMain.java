import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * An enum class of the possible operations in the system's UI
 * Each item in the class contains 2 keys (number and string)
 * and a boolean expression that indicates if the operation is enabled or not
 */
enum Operations {

    /**
     * The possible operations
     */
    LOAD(1,"load",true),
    SHOW_ALL_STOCKS(2,"show stocks",false),
    SHOW_STOCK(3,"show stock",false),
    TRADE(4,"trade",false),
    SHOW_ALL_COMMANDS(5,"show commands",false),
    SAVE(6,"save",false),
    EXIT(7,"exit",true);


    /**
     * Items' data
     */
    private final int opNum;
    private final String opName;
    private boolean isEnabled;

    /**
     * A ctor method
     * @param num a unique number that represents the command.
     * @param name a unique string that represents the command.
     * @param isEnabled true = enabled operation / false = disabled operation.
     */
    Operations(int num, String name, boolean isEnabled) {
        this.opNum = num;
        this.opName = name;
        this.isEnabled = isEnabled;
    }

    /**
     * A method that "casts" a number to operation.
     * @param num a number that represents a command.
     * @return the operation that is represented by the input number or null in case there is no such a operation.
     */
    public static Operations getOperation(int num){
        for(Operations o : Operations.values()){
            if(o.opNum == num) {
                return o;
            }
        }
        return null;
    }

    /**
     * A method that "casts" a string to operation.
     * @param name a string that represents a command.
     * @return the operation that is represented by the input string or null in case there is no such a operation.
     */
    public static Operations getOperation(String name){
        for(Operations o : Operations.values()){
            if(o.opName.equals(name.toLowerCase())) {
                return o;
            }
        }
        return null;
    }

    /**
     * A getter of the number that represents the command.
     * @return the number that represents the command.
     */
    public int getNum() {
        return opNum;
    }

    /**
     * A setter that makes enables the operation.
     */
    public void enable() {
        this.isEnabled = true;
    }

    /**
     * A static setter that enables all the operation.
     */
    static public void enableAll(){
        for(Operations o : Operations.values()){
            o.enable();
        }
    }

    /**
     * A setter that disables the operation.
     */
    public void disable() {
        this.isEnabled = false;
    }

    /**
     * A getter of the availability state of the operation.
     * @return true if the operation is enabled, and false if not.
     */
    public boolean isEnabled() {
        return this.isEnabled;
    }
}

/**
 * The main class of the program.
 */
public class SystemMain {

    //The engine is a singleton so we use getInstance method to get access to it.
    private static final Engine engine = Engine.getInstance();

    /**
     * The main method of the main class, the first method that will run in the start-up of the program.
     * @param args array of variables that was entered in the start-up of the program (Not in use in right know!).
     */
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        try {
            Operations op = null;               //initialization.
            while(op != Operations.EXIT) {      //until we get an exit command.
                printMainMenu();                //prints the menu.
                try{
                    op = getInputOperation(in); //gets an input of an operation from the user (determined by a relevant number or string).
                    executeOperation(op, in);   //executes the operation that was entered.

                } catch (JAXBException | InputMismatchException | IOException | IllegalArgumentException e) {
                    System.out.println(e.getMessage()); //prints the message that is attached to the exception.
                }
            }
            System.out.println("Thank you for using our program and goodbye!"); //An on exit message
            in.close();
        }catch(Exception e){ //We don't want to get here ;-), but in case we do, it will make sure that
                             //The program won't crush right after an unexpected exception is being thrown.
                             //Instead we will get the following message:
            System.out.println("An unexpected error occurred:\n"+e.toString()+"\n"+e.getStackTrace());
        }
    }

    /**
     *
     * @param in an input of Scanner that from it we will get an input of an operation (it can be null as well).
     * @return an operation.
     * @throws InputMismatchException will be thrown in case there will be invalid input or an input of an operation that doesn't exists.
     */
    private static Operations getInputOperation(Scanner in) throws InputMismatchException{
        if(in==null)                        //in case we didn't get a specific Scanner we will create by default an input Scanner from the console.
            in = new Scanner(System.in);
        Operations op = null;
        if(in.hasNextInt()){                //in case the input is an integer, we will try to find an operation that is represented by the integer.
            op = Operations.getOperation(in.nextInt());
            in.nextLine();
        } else                              //in case the input isn't an integer, we will try to find an operation that is represented by the input of the string.
            op = Operations.getOperation(in.nextLine());
        if(op==null)                        //in case we couldn't find an operation that matches the input or the input is invalid.
            throw new InputMismatchException("The given input of operation, doesn't exists!");
        return op;
    }

    /**
     * A method that prints the main menu of the program.
     */
    private static void printMainMenu() {
        System.out.println("\n");
        System.out.println("Operation options menu:");
        System.out.println("1. Read data from file. (type \"load\" or 1) \n" +
                "2. Show existing stocks. (type \"show stocks\" or 2)\n" +
                "3. Show chosen stock data. (type \"show stock\" or 3)\n" +
                "4. Execution of a trading order. (type \"trade\" or 4)\n" +
                "5. View the lists of commands to execute. (type \"show commands\" or 5)\n" +
                "6. Save the data of the system into a file. (type \"save\" or 6)\n" +
                "7. Exit the program. (type \"exit\" or 7)\n");
        System.out.println("Please enter your next operation:");
    }

    /**
     * A method that executes the operations of the system.
     * @param op the operation that should be executed.
     * @param in an input of Scanner that from it we will get an input of an operation (it can be null as well).
     * @throws IOException will be thrown in case the operation is load and the file path is incorrect or invalid.
     * @throws JAXBException will be thrown in case there is an error in jaxb process of deserialization.
     */
    private static void executeOperation(Operations op, Scanner in) throws IOException, JAXBException {
        if(!(op.isEnabled())){  //checks if the operation is enabled, if not, it prints a suitable message
            System.out.println("This option is not enabled yet.\n" +
                    "You need to load XML file first.");
            return;
        }

        if(in==null)  //in case we didn't get a specific Scanner we will create by default an input Scanner from the console.
            in = new Scanner(System.in);

        String path = null;

        switch (op.getNum()) { //preforms different set of command for each operation.
            case 1: //Load
                System.out.println("Insert the file's path: ");
                path = in.nextLine();
                engine.uploadDataFromFile(path);
                System.out.println("The file loaded successfully.");
                Operations.enableAll();
                break;
            case 2: //Show stocks
                showAllStocks();
                break;
            case 3: //Show stock
                showStock(in);
                break;
            case 4: //Trade
                addTradeCommand(in);
                break;
            case 5: //Show commands
                showAllCommands();
                break;
            case 6: //Save
                System.out.println("Insert the file's path and name with .xml extension (Example: \"c:\\Users\\Admin\\Desktop\\Filename.xml\"):");
                path = in.nextLine();
                engine.saveDataToFile(path);
                System.out.println("The file saved successfully.");
                break;
            case 7: //Exit
                System.exit(0);
                break;
            default://An unknown operation.
                System.out.println("\nYou entered wrong number. Chose only options from the main menu. ");
                break;
        }
    }

    /**
     * A method that prints out all the existing stocks in the system and the full data for each one of them
     */
    public static void showAllStocks() {
        StockDT[] stocks = engine.showAllStocks().toArray(new StockDT[0]); //gets an array of the existing stocks
        for (StockDT st : stocks) {                                        //for each stock it prints the full data about the stock.
            System.out.println(st.toString());
        }
    }

    /**
     * A method that prints out the full data about a specific stock.
     * @param in an input of Scanner that from it we will get an input of a stock symbol (it can be null as well).
     */
    public static void showStock(Scanner in) {
        if(in==null)                        //in case we didn't get a specific Scanner we will create by default an input Scanner from the console.
            in = new Scanner(System.in);

        try {
            System.out.println("Please enter the name of the stock you want to see: ");
            String stockName = in.nextLine();   //gets a name of a stock.

            StockDT stock = engine.showStock(stockName); //throws an InputMisMactchException if there isn't any company stocks with the given symbol
            System.out.println(stock.toString());        //prints out all tha data about the stock.
            System.out.println("This stock transactions are:");
            for(Transaction transaction: stock.getTransactions()){
                System.out.println(transaction.toString());
            }

        } catch (InputMismatchException e) { //will cache exception in case there isn't any company stocks with the given symbol.
            System.out.println(e.getMessage());
        }
    }

    /**
     * A method that adds a trade command to one of the stocks.
     * @param in an input of Scanner that from it we will get an input of all the needed parameters (it can be null as well).
     */
    public static void addTradeCommand(Scanner in){
        if(in==null)                        //in case we didn't get a specific Scanner we will create by default an input Scanner from the console.
            in = new Scanner(System.in);

        try {
            System.out.println("Please enter the symbol of the company you want to trade with it's stocks: ");
            String symbol = getSymbol(in); //gets a symbol of a stock.

            System.out.println("Please choose the direction you want to trade: (enter: BUY/SELL) ");
            TradeCommand.direction direction = getDir(in); //gets a direction (buy/sell).

            System.out.println("Please choose the type of command you want to make: (enter: LMT/MKT)"/*TODO: /FOK/IOC*/);
            TradeCommand.commandType type = getCommand(in); //gets a type of command (LMT/MKT/FOK/IOC).

            System.out.println("Please enter how many stocks you want to trade in: (integer numbers only)");
            int quantity = (int) getPositiveNum(in,true); //gets an integer that represents the number of stocks that we would like to buy or sell.

            float price = -1;   //initialization
            if(type!= TradeCommand.commandType.MKT) { //in case it's an MKT type of trade command, the price is the market price so an input of the desired price per stock is irrelevant
                System.out.println("Please enter the limit price you want to trade with: (a use of point  is permitted)");
                price = getPositiveNum(in,false);
            }

            System.out.println(engine.addTradeCommand(symbol,direction,type,quantity,price)); //adds the trade command and print out a suitable message

        } catch (IllegalArgumentException e) { //will catch an exception in case there is an invalid input.
            System.out.println(e.getMessage());
        } catch (InputMismatchException e) { //will catch an exception in case there isn't any company stocks with the given symbol, or one of the inputs is invalid.
            System.out.println(e.getMessage());
        }
 }

    /**
     * A method that prints out all the trade commands that are in the system.
     */
    static public void showAllCommands(){
        List<StockDT> stocks = engine.showAllStocks();  //gets a list of all the stocks
        for(StockDT s:stocks) {                         //for each stock prints the stock's details, and all the buy and sell commands.
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
                System.out.println("There are no made transactions.");
            else
                for(Transaction c: transactions) {
                    System.out.println(c.toString());
                }
            System.out.println("Total transaction turnover is:" + s.getTransTurnover() + "\n");
        }
    }

    /**
     * A method that gets an input of a stock symbol and checks that it is a valid and an existing symbol.
     * @param in an input of Scanner that from it we will get an input of stock symbol (it can be null as well).
     * @return a symbol of existing stock.
     * @throws InputMismatchException will be thrown in case the given input is not valid.
     * @throws IllegalArgumentException will be thrown in case the given input is not an existing stock symbol.
     */
    private static String getSymbol(Scanner in) throws InputMismatchException,IllegalArgumentException {
        if(in==null)                        //in case we didn't get a specific Scanner we will create by default an input Scanner from the console.
            in = new Scanner(System.in);

        String symbol = in.nextLine().toUpperCase();        //gets an input of string and turns all the characters to upper case.
        if(!(Stock.symbolCheck(symbol)))            //checks if the given symbol is valid.
            throw new InputMismatchException("The given symbol is not valid. Symbol contains English letters only!");
        else if (!(engine.isSymbolExists(symbol)))          //checks if the given symbol exists.
            throw new IllegalArgumentException("There isn't any company with "+ symbol +" symbol.");
        else
            return symbol;                                  //returns the symbol.
    }

    /**
     * A method that gets an input of a trade direction and checks if it's a valid input.
     * @param in an input of Scanner that from it we will get an input of the trade direction (it can be null as well).
     * @return a valid trade direction (buy/sell)
     * @throws IllegalArgumentException will be thrown in case that the given input of direction is invalid.
     */
    private static TradeCommand.direction getDir(Scanner in) throws IllegalArgumentException {
        if(in==null)                        //in case we didn't get a specific Scanner we will create by default an input Scanner from the console.
            in = new Scanner(System.in);

        String dir = in.nextLine().toUpperCase();   //gets an input of string that represents a trade direction.
        TradeCommand.direction res = null;          //initialization
        try{
            res = TradeCommand.direction.valueOf(dir);
            return res;
        } catch (IllegalArgumentException e) {      //in case the input is invalid.
            throw new IllegalArgumentException("Invalid direction option! (type only buy or sell).");
        }
    }

    /**
     * A method that gets an input of command type and checks if it's a valid input.
     * @param in an input of Scanner that from it we will get an input of a command type (it can be null as well).
     * @return a valid command type
     * @throws IllegalArgumentException will be thrown in case that the given input of command type is invalid.
     */
    private static TradeCommand.commandType getCommand(Scanner in) throws IllegalArgumentException {
        if (in==null)                       //in case we didn't get a specific Scanner we will create by default an input Scanner from the console.
            in = new Scanner(System.in);

        String cmd = in.nextLine().toUpperCase();  //gets an input of string that represents a command type.
        TradeCommand.commandType res = null;       //initialization
        try{
            res = TradeCommand.commandType.valueOf(cmd);
            return res;
        }
        catch (IllegalArgumentException e) {       //in case the input is invalid.
            throw new IllegalArgumentException("Invalid option!");
        }
    }

    /**
     * A method that gets an input of positive number and checks if it's a valid input.
     * @param in an input of Scanner that from it we will get an input of a real number (it can be null as well).
     * @param isInteger an boolean variable that acts as a flag and changes the operation of the method so the result will be an integer.
     * @return returns a positive number.
     * @throws InputMismatchException will be thrown in case that the given input of positive number is invalid.
     */
    private static float getPositiveNum(Scanner in,boolean isInteger) throws InputMismatchException {
        if (in==null)                       //in case we didn't get a specific Scanner we will create by default an input Scanner from the console.
            in = new Scanner(System.in);

        float num;
        try {
            num = in.nextFloat();                  //gets an input of a real number.
            in.nextLine();
        } catch (InputMismatchException e) {       //checks that the input is valid.
            if(isInteger)
                throw new InputMismatchException("Invalid input!, Enter an integer!");
            else
                throw new InputMismatchException("Invalid input!, Enter a real number!");
        }
        if (!(num >= 0))                          //checks that the input is positive.
            throw new InputMismatchException("Invalid Input!, should be a positive value!");
        else if (isInteger && (!(num - (int) num == 0))) //in case the needed number is an integer number, it checks that it is an integer number.
            throw new InputMismatchException("Invalid Input!, should be an integer!");
        else
            return num;
    }
}
