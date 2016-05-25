import model.AspectSimilarityDistanceModel;
import model.ExtractedAspectAndModifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Timo on 07.05.16.
 */
public class Pipeline {
    public static void main(String[] args) throws IOException {
        HashMap<String, ArrayList<ExtractedAspectAndModifier>> result = new HashMap<String, ArrayList<ExtractedAspectAndModifier>>();
        
        //read business reviews
        //returns HashMap with <businessID, reviewText>
        HashMap<String, String> reviewsPerBusiness = ReviewCollector.readData("data/Phoenix_reviews_per_business_BarsRestCafes_CHINESE.csv");
        
        //Create List of aspects per Business via dependency extractor
        result = new DependencyExtractor().pipe(result, reviewsPerBusiness);
        result = new Util().pipe(result);
        
        //Calculates sentiment value for each Extracted Aspect per Business and add attribute to aspect object
        result = SentimentCalculator.assignSentimenScore(result);

        //now create a new datastructure that will also be useful when trying to match sentiment values with cluster-ids. = aspectLemma --> object
        //no connection to businesses needed?
        HashMap<String, ArrayList<ExtractedAspectAndModifier>> mapAspectLemmaExtractedAspectAndModifier = Util.createConsolidatedDatastructure(result);


        /**
         * create data files for clustering:
         */
//        AspectSimilarityDistanceModel model = new SimilarityCalculator().pipe(mapAspectLemmaExtractedAspectAndModifier.keySet());
//
//        DbscanClustering clusterer = new DbscanClustering(model);
//        clusterer.removeOutliersWriterClusteringFiles("data/output/aspectsDbscanFiltered.txt", "data/output/distancesDbscanFiltered.txt");

        /**
         * use clustering result
         */
        
        //execute clustering
        PythonExec.executeScript("C:\\Users\\D059184\\Documents\\Master\\Data Mining\\DataSet_Project\\", "example_kMedoid.py", "WIN");
        
        
        Util.ClusteredAspectMaps clusteredAspectMaps = Util.createClusteredAspectMaps("data/clusteringresult/aspectClusterDbscanFiltered.csv", "data/output/aspectsDbscanFiltered.txt", mapAspectLemmaExtractedAspectAndModifier);

        System.out.println(clusteredAspectMaps);

        Map<ExtractedAspectAndModifier, Double> clustersSortedBySentimentMap = Util.getMostPositiveAndNegativeClusters(clusteredAspectMaps);

        System.out.println(clustersSortedBySentimentMap);

//        createFilesForClustering(model);
//
//        GraphClustering graphClustering = new GraphClustering(model);
//        graphClustering.createSampledGraph();
//        graphClustering.createMinimumSpanningTree();
//        graphClustering.savePajekNetMinimumSpanningTree("data/output/aspectNetPhoenixBusiness.net");


//        createFilesForClustering(mapAspectLemmaExtractedAspectAndModifier);


        //for clustering
        //http://stackoverflow.com/questions/13769242/clustering-words-into-groups --> minimal spanning tree graph based

        //once the clustering is done through python and the result file is created: read the file and create an index
        //run through the hashmap above and use they key to access the clustering-result --> extract the cluster-id (& cluster-word maybe)
        //save the extracted info to the ExtractAspectAndModifier object

        //also create an index like so: clusterid --> word
        

        //combining it all:
        //now we have a sentiment value for each ExtractAspectAndModifier object
        //each object also knows to which cluster it is assigned
        //now loop through all objects and create a joint sentiment value per cluster

        //think about:
        //if we take reviews from multiple businesses to create the aspect-clusters,
        //we need to distinguish between businesses when calculating the sentiment scores for the clusters.
        //solution: attach a business id to each ExtractAspectAndModifier object right at the beginning when reading in the review-text-file.



//        AspectSimilarityDistanceModel model = new SimilarityCalculator().pipe(mapAspectLemmaExtractedAspectAndModifier.keySet());
//
//        model.writeAspectFile("data/outputAspects.csv");
//        model.writeDistanceMatrix("data/outputDistanceMatrix.csv");

//        SimilarityPair.saveToFile(similarityPairs, "data/outputSimilarity.csv");

//        System.out.println(similarityPairs.size()+"; "+Math.sqrt(similarityPairs.size())+"; "+countAspects);
//        SimilarityCalculator.createDistanceMatrix(similarityPairs);

//        System.out.println(similarityPairs.size());
    }

    private static void createFilesForClustering(AspectSimilarityDistanceModel model) throws IOException {
//        AspectSimilarityDistanceModel model = new SimilarityCalculator().pipe(mapAspectLemmaExtractedAspectAndModifier.keySet());

        model.writeAspectFile("data/outputAspects.csv");
        model.writeDistanceMatrix("data/outputDistanceMatrix.csv");
    }
}
