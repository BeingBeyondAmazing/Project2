package uk.ac.nulondon;

import java.io.IOException;
import java.util.*;
public class UserMenu {

    //represents the image to be operated on
    private static Service img;

    //Choice of the user (random, blue, undo, quit)
    private static String choice = "invalid";

    //Keeps track of history of changes
    private static final Stack<String> editHistory = new Stack<>();

    /**
     * Prints the options the user chooses from
     */
    public static void printMenu() {
        System.out.println("Please enter a command");
        System.out.println("b - Highlight the bluest column");
        System.out.println("e - Highlight the seam with lowest energy");
        System.out.println("d - Delete the highlighted seam");
        System.out.println("u - Undo a previous edit");
        System.out.println("q - Quit");
    }

    /**
     * Print a response to the user, given their selection
     * @param selection the String value of the user's choice
     */
    public static void printResponse(String selection) throws IOException{
        switch(selection) {
            case "b":
                img.highlightBlue();
                System.out.println("Bluest seam highlighted .");
                break;
            case "r":
                img.highlightEnergy();
                System.out.println("Lowest energy seam highlighted.");
                break;
            case "d":
                img.removeSeam();
                System.out.println("Seam removed.");
                break;
            case "u":
                img.undo();
                System.out.println("Last removal undone.");
                break;
            case "q":
                System.out.println("Thanks for playing.");
                break;
            default:
                System.out.println("That is not a valid option.");
                choice = "invalid";
                break;
        }
    }

    /**
     * Main method where other methods in UserMenu and Image are called
     * @throws IOException  Makes sure the ImageIO class can operate properly within function
     */
    public static void main(String[] args) throws IOException{
        // keep track of if we want to keep the program running
        boolean shouldQuit = false;
        //Reads user input
        Scanner scan = new Scanner(System.in);
        //Keeps track if file path works
        boolean validFile = false;

        //prompts user to enter file path, retrying until valid path is entered
        while(!validFile) {
            System.out.println("Welcome! Enter file path");
            try {
                img = new Service(new Image(scan.nextLine()));
                validFile = true;
            } catch (Exception e) {
                System.out.println("Input should be a valid file path");
            }
        }

        //Cycle through options & confirmations until told to quit
        while(!shouldQuit){
            choice = "invalid";
            while(choice.equals("invalid")) {
                // display options to the user
                printMenu();
                //reads next input as choice
                choice = scan.next().toLowerCase();
                //prints responses to the choice
                printResponse(choice);
            }

            //If choice is quit, terminate function
            if(choice.equals("q")){
                shouldQuit = true;
                printResponse(choice);
            }
        }
        img.finalImg();
        scan.close();
    }
}
