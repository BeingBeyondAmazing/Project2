package uk.ac.nulondon;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

public class Service {

    private Image img;
    //Keeps track of which image we are on (for png name purposes)
    private int count = 0;

    private Stack<ArrayList<Image.Pixel>> seamHistory = new Stack<>();

    public Service(String filePath) throws IOException {
            File originalFile = new File(filePath);
            BufferedImage buffImg = ImageIO.read(originalFile);
            img = new Image(buffImg);
    }

    /**
     * Highlights a given seam, dependent on whether it is blue or lowest energy
     * @throws IOException Makes sure the ImageIO class can operate properly within function
     */
    public void highlight(boolean blueOrEnergy) throws IOException {

    }


    /**
     * Removes the column determined in highlightBlue() or highlightEnergy(), creating and rendering a trimmed image
     * @throws IOException Makes sure the ImageIO class can operate properly within function
     */
    public void removeSeam(ArrayList<Image.Pixel> remNodes){
        for(Image.Pixel p: remNodes){
            System.out.println("Energy: " + p.energy + " Blue Value: "+ p.color.getBlue());
        }
        if(img.getWidth() <= 1){
            System.out.println("Cannot remove any more seams;");
            return;
        }

        for(int y = 0; y < remNodes.size(); y++){
            Image.Pixel remP = remNodes.get(y);
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
        seamHistory.add(remNodes);
    }

    /**
     * Assigns image value to last saved image value in previous, then renders the retrieved image
     * @throws IOException Makes sure the ImageIO class can operate properly within function
     */
    public void undo() throws IOException{
        //img = previous.pop();
        renderImg();
    }


    /**
     * Saves the current value of img as a png titled tempImg_0<x>.png
     * @throws IOException Makes sure the ImageIO class can operate properly within function
     */
    public void renderImg() throws IOException{
        ImageIO.write(img.toBuff(), "png", new File("src/main/resources/tempImg" + ++count + ".png"));
        System.out.println("tempImg_0" + count + ".png" + " saved successfully");
    }

    /**
     * Saves the current value of img as a png titled "newImg.png"
     * @throws IOException Makes sure the ImageIO class can operate properly within function
     */
    public void finalImg() throws IOException{
        img.updateEnergies();
        ArrayList<Image.Pixel> remPix = img.findSeam(false);
        System.out.println(img.toString());
        removeSeam(remPix);
        System.out.println(img.toString());

        //System.out.println(img.toString());
        //ImageIO.write(img.toBuff(), "png", new File("src/main/resources/newImg.png"));
        //System.out.println("newImg.png saved successfully");
    }
}
