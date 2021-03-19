import java.util.List;
import java.util.Scanner;

public class SystemMain {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
//        System.out.println("Please enter the XML full path: ");
//        String pathXML = in.nextLine();
        // add here reading the data from the file in the pathXML

        int op;
        do {
            printMainMenu();
            op = in.nextInt();
            executeOperation(op, in);

        } while (op != 6);
        System.out.println("Thank you for using our program and goodbye!");
    }

    private static void printMainMenu() {
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
                String name = in.nextLine();
                showStock(name);
            default:
                System.out.println("You entered wrong number. Chose only options from the main menu. ");
                break;
        }
    }

    public static void showAllStocks() { // second option in the main menu
        CompanyStocks[] st = AllStocks.getListOfStocks().toArray(new CompanyStocks[0]);
        for (CompanyStocks s : st) {
            System.out.println(s.toString());
        }
    }

    public static void showStock(String stockName) {
        try {
            CompanyStocks s = AllStocks.getSingleStock(stockName);
            System.out.println(s.toString());
            List<Transaction> lst = s.getStockTransactions();
            if (lst.size() == 0)
                System.out.println("There is no transactions in this company stocks.");
            else {
                System.out.println("This company transactions are: ");
                for (int i = lst.size() - 1; i >= 0; --i) {
                    Transaction t = lst.get(i);
                    System.out.println(t.toString());
                    System.out.println("Total worth of transaction is " + t.getTurnOver() + ". ");
                }
            }

        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void testOne() {
        CompanyStocks company1 = new CompanyStocks("google", "Gogle", 100);
        CompanyStocks company2 = new CompanyStocks("amazon", "amzn", 200);
        CompanyStocks company3 = new CompanyStocks("tesla", "TSla", 300);
        AllStocks.addStock(company1);
        AllStocks.addStock(company2);
        AllStocks.addStock(company3);
        System.out.println("All the data has been uploaded. ");
    }
}
