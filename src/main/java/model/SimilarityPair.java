package model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by Timo on 07.05.16.
 */
public class SimilarityPair {
    private ExtractedAspectAndModifier first;
    private ExtractedAspectAndModifier second;
    private double similarity;

    public SimilarityPair(ExtractedAspectAndModifier first, ExtractedAspectAndModifier second, double similarity){
        this.first = first;
        this.second = second;
        this.similarity = similarity;
    }

    public ExtractedAspectAndModifier getFirst() {
        return first;
    }

    public ExtractedAspectAndModifier getSecond() {
        return second;
    }

    public double getSimilarity() {
        return similarity;
    }

    public String toString(){
        String first = this.first.toString();
        String second = this.second.toString();

        String s = first+" + "+second+" = "+similarity;
        return s;
    }

    public static void saveToFile(List<SimilarityPair> similarityPairs, String path) throws IOException {

        BufferedWriter writer = new BufferedWriter ( new FileWriter(new File(path)));
        for (SimilarityPair sp : similarityPairs) {
            writer.write(sp.getFirst().getFullAspect()+";"+sp.getSecond().getFullAspect()+";"+sp.getSimilarity()+"\n");
        }
        writer.close();
    }
}
