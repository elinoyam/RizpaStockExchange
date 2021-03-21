import java.util.List;
import java.util.Scanner;

public class SystemMain {
    static private Trader data = new Engine(); // for interface use

    public enum Operations {
        LOAD(1,"load"),
        SHOW_ALL_STOCKS(2,"show all stocks"),
        SHOW_STOCK(3,"show stock"),
        TRADE(4,"trade");

        private int opNum;
        private String opName;
        Operations(int num, String name) {
            this.opNum = num;
            this.opName = name;
        }
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        // System.out.println("Please enter the XML full path: ");
        // String pathXML = in.nextLine();
        // add here reading the data from the file in the pathXML

        int op;
        do {
            printMainMenu();
            op = Integer.parseInt(in.nextLine());//.parseInt(in.nextLine());//.getInteger(in.nextLine());
            executeOperation(op, in);

        } while (op != 6);
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

    private static void executeOperation(int chosen, Scanner in) {
        switch (chosen) {
            case 1:
                // need to add here the method for getting data from XML file
                testOne();
                break;
            case 2:
                showAllStocks();
                break;
            case 3:
                System.out.println("Please enter the name of the stock you want to see: ");
                //in.nextLine(); // ignore newline
                String name = in.nextLine();
                showStock(name);
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

    public static void showStock(String stockName) {
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
