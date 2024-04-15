package uk.ac.nulondon;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Graph {
    //List of Pixels representing the left most column of the image
    private ArrayList<Pixel> leftCol;

    //Pixel class, each with left, right, and color values
    public static class Pixel {
        Pixel right;
        Pixel left;
        Color color;

        //Pixel Constructor
        public Pixel(Color color){
            this.color = color;
            this.right = null;
            this.left = null;
        }

        //Overloaded Constructor
        public Pixel(Color color, Pixel right, Pixel left){
            this.color = color;
            this.right = right;
            this.left = left;
        }
    }

    //Constructs a Graph object from a supplied bufferedImage
    public Graph (BufferedImage img){
        Pixel previous = null;
        Pixel current = null;

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = img.getWidth()-1; x >= 0; x--) {
                current = new Pixel(new Color(img.getRGB(x, y)));
                current.right = previous;
                previous.left = current;
                previous = new Pixel(previous.color, previous.right, previous.left);
            }
            leftCol.set(y,current);
            previous = null;
        }
    }

    //Returns a BufferedImage from a graph
    public BufferedImage toBuff(){
        int x = 0;
        BufferedImage newImg =

        for(int y = 0; y< leftCol.size(); y++){
            Pixel temp = new Pixel(leftCol.get(y).color, leftCol.get(y).right, leftCol.get(y).left);
            while(temp != null){

            }
        }
    }

}
