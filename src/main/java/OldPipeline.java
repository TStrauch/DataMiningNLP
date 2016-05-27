import model.AspectSimilarityDistanceModel;
import model.ExtractedAspectAndModifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Timo on 07.05.16.
 */
public class OldPipeline {
    public static void main(String[] args) throws IOException {
        ArrayList<ExtractedAspectAndModifier> result = new ArrayList<ExtractedAspectAndModifier>();

        //read business reviews
        HashMap<String, String> reviewsPerBusiness = ReviewCollector.readData("data/Phoenix_reviews_per_business_BarsRestCafes_CHINESE.csv", 50);

        String reviews = "";
        int counter = 0;
        for (String businessReviews : reviewsPerBusiness.values()) {
            reviews += businessReviews;
            counter++;

            if (counter == 50){
                break;
            }
        }

        reviewsPerBusiness = new HashMap<String, String>();
        reviewsPerBusiness.put("testbusiness", reviews);

        result = new DependencyExtractor().pipe(result, reviewsPerBusiness);
        result = new Util().pipe(result);

        //now create a new datastructure that will also be useful when trying to match sentiment values with cluster-ids. = aspectLemma --> object
        HashMap<String, ArrayList<ExtractedAspectAndModifier>> mapAspectLemmaExtractedAspectAndModifier = Util.createConsolidatedDatastructure(result);


        /**
         * create data files for clustering:
         */
        AspectSimilarityDistanceModel model = new SimilarityCalculator().pipe(mapAspectLemmaExtractedAspectAndModifier.keySet());

        DbscanClustering clusterer = new DbscanClustering(model);
        clusterer.removeOutliersWriterClusteringFiles("data/output/aspectsDbscanFiltered.txt", "data/output/distancesDbscanFiltered.txt");

        PythonExec.executePythonKMedoids(20);

        /**
         * use clustering result
         */

        SentimentCalculator.assignSentimentScore(result);
        Util.ClusteredAspectMaps clusteredAspectMaps = Util.createClusteredAspectMaps("data/clusteringresult/aspectClusterDbscanFiltered.csv", "data/output/aspectsDbscanFiltered.txt", mapAspectLemmaExtractedAspectAndModifier);

        System.out.println(clusteredAspectMaps);

        Map<ExtractedAspectAndModifier, Double> clustersSortedBySentimentMap = Util.orderClustersBySentimentPerBusiness(clusteredAspectMaps.clusteridAspectsMap, clusteredAspectMaps.clusterIdCentroidMap);

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


        //for sentiment analysis:
        //http://stackoverflow.com/questions/4188706/sentiment-analysis-dictionaries
        //loop through all ExtractAspectAndModifier objects
        //take the sentimentModifier and split it at blank ' '
        //run a stemmer / lemmatizer / synonymizer / etc. on each word
        //access the sentiment lexicon and get a value per word (maybe use different versions: 1. original, 2. stemmed, 3. synomized to make sure to get a result)
        //take the sentiment values and combine them in a sensible way (e.g. weigh the right-most adjective higher etc)


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
