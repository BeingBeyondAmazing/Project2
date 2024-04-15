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

    /**
     * Constructor for class Image
     * @param filePath the file path of the image being used
     * @throws IOException- catches whether it is improper file
     */
    public Image(String filePath) throws IOException {
        File originalFile = new File(filePath);
        BufferedImage oldImg = ImageIO.read(originalFile);
        img = new Color[oldImg.getHeight()][oldImg.getWidth()];

        // This loops through the image pixel by pixel
        // And Stores it in the 2D array
        for (int y = 0; y < oldImg.getHeight(); y++) {
            for (int x = 0; x < oldImg.getWidth(); x++) {

                // Retrieving contents of a pixel
                int pixel = oldImg.getRGB(x, y);

                // Creating a Color object from pixel value
                Color originalColor = new Color(pixel);

                //Store in 2D array
                img[y][x] = originalColor;
            }
        }
    }

    public BufferedImage renderImg(){

    }

    public double energyCalculator(Color pixel){

    }

    public bluestSeam(){

    }

    public lowestEnergySeam(){

    }

    public highlightSeam(){

    }

    public removeSeam(){

    }

}
