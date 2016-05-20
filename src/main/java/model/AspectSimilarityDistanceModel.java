package model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Timo on 14.05.16.
 */
public class AspectSimilarityDistanceModel {
    private HashMap<Integer, String> aspects = new HashMap<Integer, String>();
//    private Set<String> aspects = new HashSet<String>();
    private double[][] distances;

    public  AspectSimilarityDistanceModel(int distanceSize, HashMap<Integer, String> input){
        this.distances = new double[distanceSize][distanceSize];
        this.aspects = input;
    }

    public void setDistance(int row, int col, double distance){
        this.distances[row][col] = distance;
    }

    public HashMap<Integer, String> getAspects() {
        return aspects;
    }

    public double[][] getDistances() {
        return distances;
    }

    public String toString(){
        String s = "\n \n";

        for (String lemma : aspects.values()) {
            s+="\t"+lemma;
        }
        s += "\n";

        for (int row = 0; row < this.distances.length; row++){
            s += this.aspects.get(row);
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
        for (String lemma : aspects.values()) {
            count++;

            if (count == aspects.values().size()){
                writer.write(lemma);
            }
            else {
                writer.write(lemma + "\n");
            }
            System.out.print("\r[WritingAspectFile] "+count+"/"+this.aspects.values().size());
        }
        System.out.println("\n");
        writer.close();
    }

    public void writeDistanceMatrix(String path) throws IOException {
        BufferedWriter writer = new BufferedWriter ( new FileWriter(new File(path)));

        int count = 0;
        for (int row = 0; row < this.distances.length; row++){
            for (int col = 0; col < this.distances[row].length; col++){

                if(col == this.distances[row].length-1){
                    writer.write(String.valueOf(this.distances[row][col]));
                }
                else{
                    writer.write(String.valueOf(this.distances[row][col])+", ");
                }
            }
            count++;

            if (count < this.distances.length){
                writer.write("\n");
            }


            System.out.print("\r[WritingDistanceMatrix] "+count+"/"+(distances.length));
        }
        writer.close();

    }
}
