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

    public highlightSeam(){

    }

    public removeSeam(Stack remNodes){
        for(int y = 0; y < img.)
    }

}
