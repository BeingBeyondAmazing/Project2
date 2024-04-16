package uk.ac.nulondon;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Stack;

//represents the image as a graph, including all the necessary methods and data
public class Image {
    //represents image as a 2d array of objects type Color
    private Graph img;
    private Stack<Stack<Graph.Pixel>> seamHistory = new Stack<>();

    /**
     * Constructor for class Image
     * @param filePath the file path of the image being used
     * @throws IOException- catches whether it is improper file
     */
    public Image(String filePath) throws IOException {
        File originalFile = new File(filePath);
        BufferedImage buffImg = ImageIO.read(originalFile);
        img = new Graph(buffImg);
    }

    public BufferedImage renderImg(){
        return img.toBuff();
    }

    public double updateEnergy (Color pixel){

    }

    public bluestSeam(){

    }

    public lowestEnergySeam(){

    }

    public void highlightSeam(Stack<Graph.Pixel> highlightPix, Color col){

    }

    //Removes a seam provided as a stack of Pixels row by row
    public void removeSeam(Stack<Graph.Pixel> remNodes){
        Stack<Graph.Pixel> remPixels = new Stack<>();
        Graph.Pixel remP;

        for(int y = 0; y < img.getHeight(); y++){
            remP = remNodes.pop();
            remPixels.add(remP);

            if(remP.left == null){
                //somewhere else assure the graph is more than one column
                img.setLeftCol(y, remP.right);
                remP.right.left = null;
            }
            else if(remP.right == null){
                remP.left.right = null;
            }
            else {
                remP.left.right = remP.right;
                remP.right.left = remP.left;
            }
        }
        seamHistory.add(remPixels);
    }

    public insertSeam(Stack<Graph.Pixel> addNodes){

    }

}
