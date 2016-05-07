import model.ExtractedAspectAndModifier;
import model.SimilarityPair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timo on 07.05.16.
 */
public class Pipeline {
    public static void main(String[] args) throws IOException {
        ArrayList<ExtractedAspectAndModifier> result = new ArrayList<ExtractedAspectAndModifier>();

        result = new DependencyExtractor().pipe(result);
        result = new ResultIntermediateNLP().pipe(result);

        List<SimilarityPair> similarityPairs = new SimilarityCalculator().pipeParallel(result);

        SimilarityPair.saveToFile(similarityPairs, "data/outputSimilarity.csv");

        System.out.println(similarityPairs.size());
    }
}
