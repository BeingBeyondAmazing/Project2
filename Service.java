package uk.ac.nulondon;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

public class Service {
    //Data representation of the image, instance of Image class
    private final Image img;
    //Keeps track of which image we are on (for png name purposes)
    private int count = 0;
    //Holds deleted seams in case of undo
    private final Stack<ArrayList<Image.Pixel>> seamHistory = new Stack<>();
    //The Current seam that will be removed
    private ArrayList<Image.Pixel> toBeRemoved = new ArrayList<>();

    /**
     * Service class constructor
     * @param filePath: File path used to create buffered image
     * @throws IOException
     */
    public Service(String filePath) throws IOException {
            File originalFile = new File(filePath);
            BufferedImage buffImg = ImageIO.read(originalFile);
            img = new Image(buffImg);
    }

    /**
     * Finds the given seam
     * @param borE: Boolean value that determines whether we are looking for bluest or lowest energy seam
     */
    public void findSeam(boolean borE){
        img.updateEnergies();
        //not sure if I should use Collections.min or use these current if elif else statements
        ArrayList<Double> prevValues = new ArrayList<>();
        for (Image.Pixel pixel: getFirstRowPixels()){
            if(borE){prevValues.add((double) pixel.color.getBlue());}
            else{prevValues.add(pixel.energy);}
        }
        ArrayList<Image.Pixel> prevPixels = new ArrayList<>(getFirstRowPixels());
        ArrayList<ArrayList<Image.Pixel>> prevSeams = new ArrayList<>();
        for (Image.Pixel pixel: getFirstRowPixels()){
            ArrayList placeholder = new ArrayList<>();
            placeholder.add(pixel);
            prevSeams.add(placeholder);
        }
        ArrayList<Double> currentValues = new ArrayList<>();
        ArrayList<ArrayList<Image.Pixel>> currentSeams = new ArrayList<>();
        for (int i=1; i< img.leftCol.size(); i++){
            Image.Pixel rowStart = img.leftCol.get(i);
            Image.Pixel current = rowStart;
            int nodeIndex = 0;

            while (current !=null){
                if (current.left == null){
                    //not sure if to leave in equal
                    if (prevValues.get(nodeIndex) <= prevValues.get(nodeIndex + 1)) {
                        if(borE){currentValues.add(current.color.getBlue() + prevValues.get(nodeIndex));}
                        else{currentValues.add(current.energy + prevValues.get(nodeIndex));}
                        ArrayList<Image.Pixel> prevSeam = new ArrayList<>(prevSeams.get(nodeIndex));
                        prevSeam.add(current);
                        currentSeams.add(prevSeam);
                    }
                    else {
                        if(borE){currentValues.add(current.color.getBlue() + prevValues.get(nodeIndex+1));}
                        else{currentValues.add(current.energy + prevValues.get(nodeIndex+1));}

                        ArrayList<Image.Pixel> prevSeam = new ArrayList<>(prevSeams.get(nodeIndex+1));
                        prevSeam.add(current);
                        currentSeams.add(prevSeam);
                    }
                }
                else if (current.right == null){
                    if (prevValues.get(nodeIndex - 1) <= prevValues.get(nodeIndex)) {
                        if(borE){currentValues.add(current.color.getBlue() + prevValues.get(nodeIndex-1));}
                        else{currentValues.add(current.energy + prevValues.get(nodeIndex-1));}
                        ArrayList<Image.Pixel> prevSeam = new ArrayList<>(prevSeams.get(nodeIndex-1));
                        prevSeam.add(current);
                        currentSeams.add(prevSeam);
                    }
                    else {
                        if(borE){currentValues.add(current.color.getBlue() + prevValues.get(nodeIndex));}
                        else{currentValues.add(current.energy + prevValues.get(nodeIndex));}
                        ArrayList<Image.Pixel> prevSeam = new ArrayList<>(prevSeams.get(nodeIndex));
                        prevSeam.add(current);
                        currentSeams.add(prevSeam);
                    }
                }
                else {
                    //not sure if to leave in equal
                    if ((prevValues.get(nodeIndex - 1) <= prevValues.get(nodeIndex)) && (prevValues.get(nodeIndex - 1) <= prevValues.get(nodeIndex + 1))){
                        if(borE){currentValues.add(current.energy + prevValues.get(nodeIndex-1));}
                        else{currentValues.add(current.energy + prevValues.get(nodeIndex-1));}
                        ArrayList<Image.Pixel> prevSeam = new ArrayList<>(prevSeams.get(nodeIndex-1));
                        prevSeam.add(current);
                        currentSeams.add(prevSeam);
                    }
                    else if ((prevValues.get(nodeIndex) <= prevValues.get(nodeIndex - 1)) && (prevValues.get(nodeIndex) <= prevValues.get(nodeIndex + 1))){
                        if(borE){currentValues.add(current.color.getBlue() + prevValues.get(nodeIndex));}
                        else{currentValues.add(current.energy + prevValues.get(nodeIndex));}
                        ArrayList<Image.Pixel> prevSeam = new ArrayList<>(prevSeams.get(nodeIndex));
                        prevSeam.add(current);
                        currentSeams.add(prevSeam);
                    }
                    else{
                        if(borE){currentValues.add(current.color.getBlue() + prevValues.get(nodeIndex+1));}
                        else{currentValues.add(current.energy + prevValues.get(nodeIndex+1));}
                        ArrayList<Image.Pixel> prevSeam = new ArrayList<>(prevSeams.get(nodeIndex+1));
                        prevSeam.add(current);
                        currentSeams.add(prevSeam);
                    }
                }
                current = current.right;

                nodeIndex++;
            }
            prevSeams = (ArrayList)currentSeams.clone();
            currentSeams.clear();
            prevValues = (ArrayList)currentValues.clone();
            currentValues.clear();
        }

        int smallest = prevValues.indexOf(Collections.min(prevValues));
        int largest = prevValues.indexOf(Collections.max(prevValues));
        double smallestReal = Collections.min(prevValues);
        if(borE){toBeRemoved = prevSeams.get(largest);}
        toBeRemoved = prevSeams.get(smallest);
    }

    //findSeam helper method
    public ArrayList<Image.Pixel> getFirstRowPixels() {
        ArrayList<Image.Pixel> firstRowPixels = new ArrayList<>();
        Image.Pixel firstPixel = img.leftCol.getFirst();
        while (firstPixel!= null){
            firstRowPixels.add(firstPixel);
            firstPixel = firstPixel.right;
        }
        return firstRowPixels;
    }

    /**
     * Highlights a given seam, dependent on whether it is blue or lowest energy
     * @throws IOException Makes sure the ImageIO class can operate properly within function
     */
    public void highlight(Color color) throws IOException {
            renderImg(img.toBuff(toBeRemoved, color));
    }


    /**
     * Removes the seam determined in findSeam, creating and rendering a trimmed image
     * @throws IOException Makes sure the ImageIO class can operate properly within function
     */
    public void removeSeam() throws IOException {
        if(img.getWidth() <= 1){
            System.out.println("Cannot remove any more seams;");
            return;
        }

        for(int y = 0; y < toBeRemoved.size(); y++){
            Image.Pixel remP = toBeRemoved.get(y);

            if(remP.left == null){
                //somewhere else assure the graph is more than one column
                img.setLeftCol(y, remP.right);
                remP.right.left = null;

            }
            else if(remP.right == null){
                remP.left.right = null;
            }
            else{
                remP.left.right = remP.right;
                remP.right.left = remP.left;
                System.out.println(remP.left.color.getBlue() + " " +  remP.left.right.color.getBlue());
            }
        }

        seamHistory.add(toBeRemoved);
        renderImg(img.toBuff());
    }


    /**
     * Insert the last removed seam back into the image
     * @throws IOException Makes sure the ImageIO class can operate properly within function
     */
    public void undo() throws IOException{
        ArrayList<Image.Pixel> lastEdit= seamHistory.pop();

        for(int y = 0; y < lastEdit.size(); y++){
            Image.Pixel add = lastEdit.get(y);

            if(add.left == null){
                //somewhere else assure the graph is more than one column
                img.setLeftCol(y, add);
                add.right.left = add;

            }
            else if(add.right == null){
                add.left.right = add;
            }
            else{
                add.left.right = add;
                add.right.left = add;
            }
        }

        renderImg(img.toBuff());
    }


    /**
     * Saves the current value of img as a png titled tempImg_0<x>.png
     * @throws IOException Makes sure the ImageIO class can operate properly within function
     */
    public void renderImg(BufferedImage buff) throws IOException{
        ImageIO.write(buff, "png", new File("src/main/resources/tempImg" + ++count + ".png"));
        System.out.println("tempImg_0" + count + ".png" + " saved successfully");
    }

    /**
     * Saves the current value of img as a png titled "newImg.png"
     * @throws IOException Makes sure the ImageIO class can operate properly within function
     */
    public void finalImg() throws IOException{
        ImageIO.write(img.toBuff(), "png", new File("src/main/resources/newImg.png"));
        System.out.println("newImg.png saved successfully");
    }
}
