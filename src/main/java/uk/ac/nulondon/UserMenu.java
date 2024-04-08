package uk.ac.nulondon;

import java.io.IOException;
import java.util.*;
public class UserMenu {

    //represents the image to be operated on
    private static Image img;

    //Choice of the user (random, blue, undo, quit)
    private static String choice = "invalid";

    //Keeps track of history of changes
    private static final Stack<String> editHistory = new Stack<>();

    //keeps track whether user entered a valid confirmation (y or n)

    /**
     * Prints the options the user chooses from
     */
    public static void printMenu() {
        System.out.println("Please enter a command");
        System.out.println("b - Remove the bluest column");
        System.out.println("r - Remove a random column");
        System.out.println("u - Undo a previous edit");
        System.out.println("d - Draw!");
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
                editHistory.add("BLUE_HIGHLIGHT");
                System.out.println("Remove the bluest column. Continue? (Y/N)");
                break;
            case "r":
                img.highlightRandom();
                editHistory.add("RANDOM_HIGHLIGHT");
                System.out.println("Remove a random column. Continue? (Y/N)");
                break;
            case "u":
                System.out.println("Undo. Continue?");
                break;
            case "q":
                System.out.println("Thanks for playing.");
                break;
            case "d":
                System.out.println("Entering drawing mode");
                break;
            default:
                System.out.println("That is not a valid option.");
                choice = "invalid";
                break;
        }
    }

    /**
     * Executes or cancels operation indicated earlier by user
     * @param confirm String represent whether the user wants to make the edit or not
     */
    public static void printConfirmSelection(String confirm) throws IOException {
        switch(confirm){
            case "y":
                if(choice.equals("u")){
                    try {
                        img.undo();
                        if(editHistory.peek().equals("DRAW")) {
                            System.out.println("Undo: " + editHistory.pop());
                        }
                        else {
                            System.out.println("Undo: " + editHistory.pop());
                            System.out.println("Undo: " + editHistory.pop());
                        }
                    } catch (EmptyStackException ese) {
                        System.out.println("No Change to Undo");
                    }
                }
                else {
                    img.removeColumn();
                    editHistory.add("DELETE");
                }
                break;
            case "n":
                if(!choice.equals("u")) {
                    img.undo();
                    editHistory.pop();
                }
                System.out.println("operation canceled");
                break;
            default:
                System.out.println("That is not a valid option.");
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
                img = new Image(scan.nextLine());
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
            }

            //If choice is quit, terminate function print final product
            if(choice.equals("q")){
                shouldQuit = true;
                printResponse(choice);
                img.finalImg();
            }
            else if(choice.equals("d")){
                printResponse(choice);
                editHistory.add("DRAW");
                img.draw(scan);
            }
            else{
                printResponse(choice);
                //Confirmation of the choice (y or n)
                String confirm = scan.next().toLowerCase();
                printConfirmSelection(confirm);
            }
        }
        scan.close();
    }
}
