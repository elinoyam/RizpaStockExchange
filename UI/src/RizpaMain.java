import java.util.Scanner;
import java.util.List;


public class RizpaMain {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Please enter the XML full path: ");
        String pathXML = in.nextLine();
        // add here reading the data from the file in the pathXML

        int op;
        do{
            printMainMenu();
            op=in.nextInt();
            executeOperation(op);

        }while(op !=6);
    }

    private static void printMainMenu(){
        System.out.println("Operation options menu:");
        System.out.println("1. Read data from file. \n" +
                "2. Show existing stocks.\n" +
                "3. Show chosen stock data. \n" +
                "4. Execution of a trading order. \n" +
                "5. View the lists of commands to execute. \n" +
                "6. Exit the program.\n");
        System.out.println("Please enter your next operation: (enter only number) ");
    }

    private static void executeOperation(int chosen){
        switch (chosen){
            case 1:
                // need to add here the method for getting data from XML file
                break;
            case 3:
                showAllStocks();
                break;
            default:
                System.out.println("You entered wrong number. Chose only options from the main menu. ");
                break;
        }
    }
    public static void showAllStocks(){ // second option in the main menu
        Stock[] st = AllStocks.getListOfStocks().toArray(new Stock[0]);
        for(Stock s :st){
            System.out.println(s.toString());
        }
    }

    public static void showStock(String stockName){
        try{
            Stock s = AllStocks.getSingleStock(stockName);
            System.out.println(s.toString());
            List<Transaction> lst = s.getStockTransactions();
            if(lst.size() == 0)
                System.out.println("There is no transactions in this company stocks.");
            else{
                System.out.println("This company transactions are: ");
                for(int i= lst.size()-1; i>=0;--i){
                    Transaction t = lst.get(i);
                    System.out.println(t.toString());
                    System.out.println("Total worth of transaction is " +t.getTurnOver()+". ");
                }
            }

        } catch(IllegalArgumentException e){
            System.out.println(e.getMessage());
        }
    }


}

