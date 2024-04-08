package uk.ac.nulondon;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Stack;

//represents the image, including all the necessary methods and data
public class Image {
    //represents image as a 2d array of objects type Color
    private Color[][] img;

    //Keeps track of which image we are on (for png name purposes)
    private int count = 0;

    //keeps track of the index of the column that will be removed
    private int toBeRemoved = -1;

    //Stack that keeps track of the prior Images for undo() purposes
    private final Stack<Color[][]> previous;

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
        previous = new Stack<>();
    }

    /**
     * Highlights a randomly chosen column red, then renders the image with the red highlight
     * @throws IOException Makes sure the ImageIO class can operate properly within function
     */
    public void highlightRandom() throws IOException {
        toBeRemoved = (int)(Math.random() * img[0].length);
        Color[][] newImg = new Color [img.length][img[0].length];

        //Cycles through old array, skipping target column
        //And provides color values to new array
        for (int y = 0; y < img.length; y++) {
            for(int x = 0; x < img[0].length; x++) {
                if(x == toBeRemoved){
                    newImg[y][x] = Color.RED;
                }
                else
                    newImg[y][x] = img[y][x];
            }
        }

        previous.add(img);
        img = newImg;
        renderImg();
    }

    /**
     * Highlights the column with the highest blue value blue, then renders the image with that highlight
     * @throws IOException Makes sure the ImageIO class can operate properly within function
     */
    public void highlightBlue() throws IOException{
        int[] blueValue = new int[img[0].length];

        //Cycles through old array
        //Adding blue values to corresponding x value in blue value array
        for (Color[] colors : img) {
            for (int x = 0; x < img[0].length; x++) {
                blueValue[x] += colors[x].getBlue();
            }
        }

        toBeRemoved = 0;
        for(int i = 0; i<blueValue.length; i++){
            if (blueValue[i] > blueValue[toBeRemoved])
                toBeRemoved = i;
        }

        Color[][] newImg = new Color [img.length][img[0].length];

        for (int y = 0; y < img.length; y++) {
            for(int x = 0; x < img[0].length; x++) {
                if(x == toBeRemoved){
                    newImg[y][x] = Color.BLUE;
                }
                else
                    newImg[y][x] = img[y][x];
            }
        }

        previous.add(img);
        img = newImg;
        renderImg();
    }

    /**
     * Removes the column determined in highlightBlue() or randomHighlight(), creating and rendering a trimmed image
     * @throws IOException Makes sure the ImageIO class can operate properly within function
     */
    public void removeColumn() throws IOException{
        Color[][] newImg = new Color[img.length][img[0].length-1];

        //Cycles through old array, skipping target column
        //And provides color values to new array
        for (int y = 0; y < img.length; y++) {
            if (toBeRemoved >= 0) System.arraycopy(img[y], 0, newImg[y], 0, toBeRemoved);
            if (img[0].length - (toBeRemoved + 1) >= 0)
                System.arraycopy(img[y], toBeRemoved + 1, newImg[y], toBeRemoved + 1 - 1, img[0].length - (toBeRemoved + 1));
        }

        img = newImg;
        renderImg();
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
     * Allows user to set the color of a specific pixel(s), rendering image when all drawing is done (Wasn't sure if this was mandatory)
     * @param in Scanner provided in the UserMenu class
     * @throws IOException Makes sure the ImageIO class can operate properly within function
     */
    public void draw(Scanner in) throws IOException {
        int x;
        int y;
        Color color;
        Color[][] newImg = new Color[img.length][img[0].length];
        for(int i = 0; i < img.length; i++){
            System.arraycopy(img[i], 0, newImg[i], 0, img[0].length);
        }

        while (true) {
            System.out.println("Enter x coordinate(0-" + (newImg[0].length - 1) + ") or -1 to exit draw mode");
            x = in.nextInt();
            if (x == -1) {
                break;
            }
            System.out.println("Enter y coordinate(0-" + (newImg.length - 1) + "):");
            y = in.nextInt();

            try {
                System.out.println("Enter Red Value (0-225):");
                int red = in.nextInt();
                System.out.println("Enter Green Value (0-225):");
                int green = in.nextInt();
                System.out.println("Enter Blue Value (0-225):");
                int blue = in.nextInt();
                color = new Color(red, green, blue);
                if ((x >= 0 && x < newImg[0].length) && (y >= 0 && y < newImg.length)) {
                    newImg[y][x] = color;
                }
            } catch (InputMismatchException e) {
                System.out.println("Enter an integer Value");
            }
        }

        previous.add(img);
        img = newImg;
        renderImg();
    }

    /**
     * Saves the current value of img as a png titled tempImg_0<x>.png
     * @throws IOException Makes sure the ImageIO class can operate properly within function
     */
    public void renderImg() throws IOException{
        BufferedImage newImg = new BufferedImage(img[0].length
                , img.length
                , BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < img.length; y++) {
            for (int x = 0; x < img[0].length; x++) {
                try {
                    newImg.setRGB(x, y, img[y][x].getRGB());
                } catch (NullPointerException e) {
                    newImg.setRGB(x,y,Color.BLACK.getRGB());
                }
            }
        }
        ImageIO.write(newImg, "png", new File("tempImg" + ++count + ".png"));
        System.out.println("tempImg_0" + count + ".png" + " saved successfully");
    }

    /**
     * Saves the current value of img as a png titled "newImg.png"
     * @throws IOException Makes sure the ImageIO class can operate properly within function
     */
    public void finalImg() throws IOException{
        BufferedImage newImg = new BufferedImage(img[0].length
                , img.length
                , BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < newImg.getHeight(); y++) {
            for (int x = 0; x < newImg.getWidth(); x++) {
                try {
                    newImg.setRGB(x, y, img[y][x].getRGB());
                } catch (NullPointerException e) {
                    newImg.setRGB(x,y,Color.BLACK.getRGB());
                }
            }
        }
        ImageIO.write(newImg, "png", new File("newImg.png"));
        System.out.println("newImg.png saved successfully");
    }
}
