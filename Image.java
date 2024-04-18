package uk.ac.nulondon;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

public class Image {
    //List of Pixels representing the left most column of the image
    private final ArrayList<Pixel> leftCol = new ArrayList<>();

    //Pixel class, each with left, right, and color values
    public static class Pixel {
        Pixel right;
        Pixel left;
        Color color;
        double energy;

        //Pixel Constructor
        public Pixel(Color color){
            this.color = color;
            this.right = null;
            this.left = null;
            energy = 0;

        }

        //Overloaded Constructor
        public Pixel(Color color, Pixel right, Pixel left, double energy){
            this.color = color;
            this.right = right;
            this.left = left;
            this.energy = energy;
        }

        public Pixel(Color color, Pixel right, Pixel left){
            this.color = color;
            this.right = right;
            this.left = left;
            energy = 0.0;
        }
    }

    //Constructs a Graph object from a supplied bufferedImage
    public Image (BufferedImage img){

        Pixel previous = null;
        Pixel current = null;

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = img.getWidth()-1; x >= 0; x--) {
                current = new Pixel(new Color(img.getRGB(x, y)));
                current.right = previous;
                if (previous != null) {
                    previous.left = current;
                }
                previous = new Pixel(current.color, current.right, current.left);
            }

            leftCol.add(y,current);
            previous = null;
        }
    }


    //Creates temporary Pixel used to iterate without damaging data
    private Pixel getTemp(Pixel p){
        return new Pixel(p.color, p.right, p.left, p.energy);
    }

    //Gets height of grid
    public int getHeight(){
        return leftCol.size();
    }

    //Gets the width of grid
    public int getWidth(){
        int width = 0;
        Pixel temp = getTemp(leftCol.getFirst());

        while(temp != null){
            temp = temp.right;
            width++;
        }
        return width;
    }

    //Gets Pixel at given indices
    public Pixel getAt(int x, int y){
        Pixel temp = getTemp(leftCol.get(y));

        if(x >= getWidth()) {return null;}

        for(int i = 0; i<x; i++){temp = temp.right;}

        return temp;
    }

    public void setLeftCol(int y, Pixel p){
        leftCol.set(y,p);
    }

    //Returns a BufferedImage from a graph
    public BufferedImage toBuff(){
        int x;
        BufferedImage newImg = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);

        for(int y = 0; y< leftCol.size(); y++){
            Pixel temp = getTemp(leftCol.get(y));
            x = 0;
            while(temp != null){
                //if(temp.inSeam == true){newImg.setRGB(x,y,);
                newImg.setRGB(x,y, temp.color.getRGB());
                x++;
                temp = temp.right;
            }
        }

        return newImg;
    }

    public void updateEnergies(){
        double [][] pixBr = new double[getHeight()][getWidth()];
        double horizE; double vertE;

        for(int y = 0; y<getHeight(); y++){
            int x = 0;
            Pixel temp = getTemp(leftCol.get(y));
            while(temp != null) {
                pixBr[y][x] = br(temp);
                temp = temp.right;
                x++;
            }
        }

        for(int y = 0; y < getHeight(); y++){
            int x = 0;
            Pixel temp = getTemp(leftCol.get(y));
            while(temp != null){
                horizE = 0.0;
                vertE = 0.0;
                
                try {
                    horizE += pixBr[y-1][x-1];
                    vertE += pixBr[y-1][x-1];
                } catch (Exception e) {
                    horizE += pixBr[y][x];
                    vertE += pixBr[y][x];
                }

                try {
                    vertE += 2 * pixBr[y-1][x];
                } catch (Exception e) {
                    vertE += 2 * pixBr[y][x];
                }

                try {
                    horizE -= pixBr[y-1][x+1];
                    vertE += pixBr[y-1][x+1];
                } catch (Exception e) {
                    horizE -= pixBr[y][x];
                    vertE += pixBr[y][x];
                }

                try {
                    horizE += 2 * pixBr[y][x-1];
                } catch (Exception e) {
                    horizE += 2 * pixBr[y][x];
                }

                try {
                    horizE -= 2 * pixBr[y][x+1];
                } catch (Exception e) {
                    horizE -= 2 * pixBr[y][x];
                }

                try {
                    horizE += pixBr[y+1][x-1];
                    vertE -= pixBr[y+1][x-1];
                } catch (Exception e) {
                    horizE += pixBr[y][x];
                    vertE -= pixBr[y][x];
                }

                try {
                    vertE -= 2 * pixBr[y+1][x];
                } catch (Exception e) {
                    vertE -= 2 * pixBr[y][x];
                }

                try {
                    horizE -= pixBr[y+1][x+1];
                    vertE -= pixBr[y+1][x+1];
                } catch (Exception e) {
                    horizE -= pixBr[y][x];
                    vertE -= pixBr[y][x];
                }

                temp.energy = (int)(Math.sqrt((horizE * horizE) + (vertE * vertE)) * 100)/100.0;
                if(x == 0){
                    leftCol.get(y).energy = (int)(Math.sqrt((horizE * horizE) + (vertE * vertE)) * 100)/100.0;
                }

                temp = temp.right;
                x++;
            }
        }
    }

    public Double br(Pixel p){
        return (int)(((p.color.getRed() + p.color.getBlue() + p.color.getGreen())/3.0)*100)/100.0;
    }

    public ArrayList<Pixel> findSeam(boolean borE){
        //not sure if I should use Collections.min or use these current if elif else statements
        ArrayList<Double> prevValues = new ArrayList<>();
        for (Pixel pixel: getFirstRowPixels()){
            if(borE){prevValues.add((double) pixel.color.getBlue());}
            else{prevValues.add(pixel.energy);}
        }
        ArrayList<Pixel> prevPixels = new ArrayList<>(getFirstRowPixels());
        ArrayList<ArrayList<Pixel>> prevSeams = new ArrayList<>();
        for (Pixel pixel: getFirstRowPixels()){
            ArrayList placeholder = new ArrayList<>();
            placeholder.add(pixel);
            prevSeams.add(placeholder);
        }
        ArrayList<Double> currentValues = new ArrayList<>();
        ArrayList<ArrayList<Pixel>> currentSeams = new ArrayList<>();
        for (int i=1; i< leftCol.size(); i++){
            Pixel rowStart = leftCol.get(i);
            Pixel current = rowStart;
            int nodeIndex = 0;

            while (current !=null){
                if (current.left == null){
                    //not sure if to leave in equal
                    if (prevValues.get(nodeIndex) <= prevValues.get(nodeIndex + 1)) {
                        if(borE){currentValues.add(current.color.getBlue() + prevValues.get(nodeIndex));}
                        else{currentValues.add(current.energy + prevValues.get(nodeIndex));}
                        ArrayList<Pixel> prevSeam = new ArrayList<>(prevSeams.get(nodeIndex));
                        prevSeam.add(current);
                        currentSeams.add(prevSeam);
                    }
                    else {
                        if(borE){currentValues.add(current.color.getBlue() + prevValues.get(nodeIndex+1));}
                        else{currentValues.add(current.energy + prevValues.get(nodeIndex+1));}

                        ArrayList<Pixel> prevSeam = new ArrayList<>(prevSeams.get(nodeIndex+1));
                        prevSeam.add(current);
                        currentSeams.add(prevSeam);
                    }
                }
                else if (current.right == null){
                    if (prevValues.get(nodeIndex - 1) <= prevValues.get(nodeIndex)) {
                        if(borE){currentValues.add(current.color.getBlue() + prevValues.get(nodeIndex-1));}
                        else{currentValues.add(current.energy + prevValues.get(nodeIndex-1));}
                        ArrayList<Pixel> prevSeam = new ArrayList<>(prevSeams.get(nodeIndex-1));
                        prevSeam.add(current);
                        currentSeams.add(prevSeam);
                    }
                    else {
                        if(borE){currentValues.add(current.color.getBlue() + prevValues.get(nodeIndex));}
                        else{currentValues.add(current.energy + prevValues.get(nodeIndex));}
                        ArrayList<Pixel> prevSeam = new ArrayList<>(prevSeams.get(nodeIndex));
                        prevSeam.add(current);
                        currentSeams.add(prevSeam);
                    }
                }
                else {
                    //not sure if to leave in equal
                    if ((prevValues.get(nodeIndex - 1) <= prevValues.get(nodeIndex)) && (prevValues.get(nodeIndex - 1) <= prevValues.get(nodeIndex + 1))){
                        if(borE){currentValues.add(current.energy + prevValues.get(nodeIndex-1));}
                        else{currentValues.add(current.energy + prevValues.get(nodeIndex-1));}
                        ArrayList<Pixel> prevSeam = new ArrayList<>(prevSeams.get(nodeIndex-1));
                        prevSeam.add(current);
                        currentSeams.add(prevSeam);
                    }
                    else if ((prevValues.get(nodeIndex) <= prevValues.get(nodeIndex - 1)) && (prevValues.get(nodeIndex) <= prevValues.get(nodeIndex + 1))){
                        if(borE){currentValues.add(current.color.getBlue() + prevValues.get(nodeIndex));}
                        else{currentValues.add(current.energy + prevValues.get(nodeIndex));}
                        ArrayList<Pixel> prevSeam = new ArrayList<>(prevSeams.get(nodeIndex));
                        prevSeam.add(current);
                        currentSeams.add(prevSeam);
                    }
                    else{
                        if(borE){currentValues.add(current.color.getBlue() + prevValues.get(nodeIndex+1));}
                        else{currentValues.add(current.energy + prevValues.get(nodeIndex+1));}
                        ArrayList<Pixel> prevSeam = new ArrayList<>(prevSeams.get(nodeIndex+1));
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

        //if I used Collections.min then there is no need for loop
//        int smallest = prevValues.get(0).intValue();
//        System.out.println(smallest);
//        for(int i=0; i<prevValues.size();i++){
//            System.out.println("smallest: "+smallest);
//            System.out.println("next comparison: "+prevValues.get(i));
//            if (prevValues.get(i) <= smallest) {
//                smallest = prevValues.get(i);
//                System.out.println("current smallest:"+smallest);
//            }
//        }
        int smallest = prevValues.indexOf(Collections.min(prevValues));
        int largest = prevValues.indexOf(Collections.max(prevValues));
        double smallestReal = Collections.min(prevValues);
        if(borE){return prevSeams.get(largest);}
        return prevSeams.get(smallest);
    }

    public ArrayList<Pixel> getFirstRowPixels() {
        ArrayList<Pixel> firstRowPixels = new ArrayList<>();
        Pixel firstPixel = leftCol.get(0);
        while (firstPixel!= null){
            firstRowPixels.add(firstPixel);
            firstPixel = firstPixel.right;
        }
        return firstRowPixels;
    }


    public String toString(){
        StringBuilder str = new StringBuilder();
        for(int y = 0; y < leftCol.size(); y++){
            Pixel temp = getTemp(leftCol.get(y));
            str.append("[").append(y).append("]").append(" ");
            while(temp!= null){
                str.append("<-> (").append("Energy: ").append(temp.energy).append(" Blue value: " + temp.color.getBlue()+ ") ");
                temp = temp.right;
            }
            str.append("\n");
        }

        return str.toString();
    }
///Users/bwelsh/proj2/src/main/resources/beach.png
}
