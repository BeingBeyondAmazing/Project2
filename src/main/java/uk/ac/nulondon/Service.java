package uk.ac.nulondon;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Stack;

public class Service {
    private Image img;
    //Keeps track of which image we are on (for png name purposes)
    private int count = 0;

    //keeps track of the index of the column that will be removed
    private int toBeRemoved = -1;

    //Stack that keeps track of the prior Images for undo() purposes
    private final Stack<Color[][]> previous = null;

    public Service(Image img){
        this.img = img;
    }

    /**
     * Highlights a given seam, dependent on whether it is blue or lowest energy
     * @throws IOException Makes sure the ImageIO class can operate properly within function
     */
    public void Highlight() throws IOException {

    }


    /**
     * Removes the column determined in highlightBlue() or highlightEnergy(), creating and rendering a trimmed image
     * @throws IOException Makes sure the ImageIO class can operate properly within function
     */
    public void removeSeam() throws IOException{

    }

    /**
     * Assigns image value to last saved image value in previous, then renders the retrieved image
     * @throws IOException Makes sure the ImageIO class can operate properly within function
     */
    public void undo() throws IOException{
        img = previous.pop();
        renderImg();
    }


    /**
     * Saves the current value of img as a png titled tempImg_0<x>.png
     * @throws IOException Makes sure the ImageIO class can operate properly within function
     */
    public void renderImg() throws IOException{
        //TODO: revert image into buffered image

        ImageIO.write(newImg, "png", new File("tempImg" + ++count + ".png"));
        System.out.println("tempImg_0" + count + ".png" + " saved successfully");
    }

    /**
     * Saves the current value of img as a png titled "newImg.png"
     * @throws IOException Makes sure the ImageIO class can operate properly within function
     */
    public void finalImg() throws IOException{
        //TODO: revert image into buffered image

        ImageIO.write(newImg, "png", new File("newImg.png"));
        System.out.println("newImg.png saved successfully");
    }
}
