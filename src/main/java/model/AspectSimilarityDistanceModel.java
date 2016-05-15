package model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Timo on 14.05.16.
 */
public class AspectSimilarityDistanceModel {
    private HashMap<Integer, ExtractedAspectAndModifier> aspects = new HashMap<Integer, ExtractedAspectAndModifier>();
    private double[][] distances;

    public  AspectSimilarityDistanceModel(int distanceSize, List<ExtractedAspectAndModifier> input){
        this.distances = new double[distanceSize][distanceSize];

        for (int i=0; i < input.size(); i++){
            this.aspects.put(i, input.get(i));
        }
    }

    public void setDistance(int row, int col, double distance){
        this.distances[row][col] = distance;
    }

    public HashMap<Integer, ExtractedAspectAndModifier> getAspects() {
        return aspects;
    }

    public double[][] getDistances() {
        return distances;
    }

    public String toString(){
        String s = "\n \n";

        for (ExtractedAspectAndModifier extractedAspectAndModifier : aspects.values()) {
            s+="\t"+extractedAspectAndModifier.getFullAspect();
        }
        s += "\n";

        for (int row = 0; row < this.distances.length; row++){
            s += this.aspects.get(row).getFullAspect();
            for (int col = 0; col < this.distances[row].length; col++){
                s += "\t"+this.distances[row][col];
            }
            s += "\n";
        }

        return s;
    }

    public void writeAspectFile(String path) throws IOException {
        BufferedWriter writer = new BufferedWriter ( new FileWriter(new File(path)));

        int count = 0;
        for (ExtractedAspectAndModifier aspectAndModifier : aspects.values()) {
            count++;
            writer.write(aspectAndModifier.getFullAspect()+"\n");
            System.out.print("\r[WritingAspectFile] "+count+"/"+this.aspects.size());
        }
        System.out.println("\n");
        writer.close();
    }

    public void writeDistanceMatrix(String path) throws IOException {
        BufferedWriter writer = new BufferedWriter ( new FileWriter(new File(path)));

        int count = 0;
        for (int row = 0; row < this.distances.length; row++){
            for (int col = 0; col < this.distances[row].length; col++){
                count++;

//                if (row == 702 && col == 150){
//                    System.out.println("too small");
//                }

                System.out.print("\r[WritingDistanceMatrix] "+count+"/"+(distances.length * distances.length));
                if(col == this.distances[row].length-1){
                    writer.write(String.valueOf(this.distances[row][col]));
                }
                else{
                    writer.write(String.valueOf(this.distances[row][col])+", ");
                }
            }
            writer.write("\n");
        }
        writer.close();

    }
}
