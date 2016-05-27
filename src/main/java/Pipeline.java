import model.AspectSimilarityDistanceModel;
import model.ExtractedAspectAndModifier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by Timo on 07.05.16.
 */
public class Pipeline {
    public static void main(String[] args) throws IOException {

        /**
         * Do evaluation:
         *
         * let the Evaluator read in the manually labeled data
         * get the sentences from the evaluator --> reviewText to run the pipeline on
         *
         * set the MinPts and Eps values of the DbscanClustering algorithm to some values
         * also set the number of clusters to some share (right now 0,2 * n)
         * run the algorithms (dbscan + k-medoids)
         * calculate the clusters --> since there is only one business we can actually use the old approach without
         * looking at different businesses.
         *
         * run the two evaluation functions of the evaluator.
         * save the results in an appropriate datastructure
         * it should contain the following information: dbscan-minpts, dbscan-eps, kmedoids-shareOfClusters, generalOverlap, avgClusterOverlap>
         */

//        executeParameterOptimization();

        /**
         * ---------------------------------------------------------------------------------------------------------------
         * ---------------------------------------------------------------------------------------------------------------
         *
         * Do the general calculation --> project goal.
         * run this with the optimized paramters:
         */

        executeClustering();

    }





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

    private static void executeParameterOptimization() throws IOException {
        ArrayList<ExtractedAspectAndModifier> result = new ArrayList<ExtractedAspectAndModifier>();

        Evaluator evaluator = new Evaluator();
        evaluator.readManualClusters("data/manualLabeledData.csv");

        String reviewText = "";
        for (String s : evaluator.getSentences()) {
            reviewText += s+". ";
        }

        HashMap<String, String> reviews = new HashMap<>();
        reviews.put("evaluationBusiness", reviewText);

        //Create List of aspects per Business via dependency extractor
        result = new DependencyExtractor().pipe(result, reviews);
        result = new Util().pipe(result);

        //Calculates sentiment value for each Extracted Aspect per Business and add attribute to aspect object
        result = SentimentCalculator.assignSentimentScore(result);

        //now create a new datastructure that will also be useful when trying to match sentiment values with cluster-ids. = aspectLemma --> object
        HashMap<String, ArrayList<ExtractedAspectAndModifier>> mapAspectLemmaExtractedAspectAndModifier = Util.createConsolidatedDatastructure(result);

        AspectSimilarityDistanceModel model = new SimilarityCalculator().pipe(mapAspectLemmaExtractedAspectAndModifier.keySet());


        /*
         * here the actual parameter testing begins --> loop
         */

        int param_minpts = 2;
        double param_eps = 0.75;
        double param_clusters = 0.2; //TODO make this a variable parameter as well

        final int INTERVAL_MINPTS = 1;
        final double INTERVAL_EPS = 0.02;
        final double START_EPS = 0.75;
        final int START_MINPTS = 2;
        final double END_EPS = 1;
        final double END_MINPTS = 6;
        final double START_CLUSTERS = 0.2;
        final double END_CLUSTERS = 0.9;
        final double INTERVAL_CLUSTERS = 0.1;

        String resultString = "dbscan-minpts;dbscan-eps;kmedoids-shareOfClusters;generalOverlap;avgClusterOverlap\n";

        int counter = 1;

        param_clusters = START_CLUSTERS;
        while(param_clusters < END_CLUSTERS){
            param_minpts = START_MINPTS;
            while (param_minpts < END_MINPTS) {

                param_eps = START_EPS;
                while (param_eps < END_EPS) {

                    DbscanClustering.FILTERING_EPS = param_eps;
                    DbscanClustering.FILTERING_MINPTS = param_minpts;

                    System.out.print("\r current iteration: "+counter);

                    //cluster
                    DbscanClustering clusterer = new DbscanClustering(model);
                    int aspectCountAfterDbscan = clusterer.removeOutliersWriterClusteringFiles("data/output/aspectsDbscanFiltered.txt", "data/output/distancesDbscanFiltered.txt");

                    int nclusters = (int) (aspectCountAfterDbscan * param_clusters);

                    if(nclusters <= 0){
                        resultString += param_minpts + ";" + param_eps + ";" + param_clusters + ";" + 0.0 + ";" + 0.0 + "\n";
                        counter++;
                        param_eps = param_eps + INTERVAL_EPS;
                        continue;
                    }

                    PythonExec.executePythonKMedoids(nclusters);

                    //retrieve clustered aspectModifier-Tuples
                    Util.ClusteredAspectMaps clusteredAspectMaps = Util.createClusteredAspectMaps("data/clusteringresult/aspectClusterDbscanFiltered.csv", "data/output/aspectsDbscanFiltered.txt", mapAspectLemmaExtractedAspectAndModifier);

                    //transform clustered tuples into evaluation form
                    Set<String> aspectSet = new HashSet<>();
                    HashMap<String, Set<String>> aspectClusterSet = new HashMap<>();
                    for (ArrayList<ExtractedAspectAndModifier> extractedAspectAndModifiers : clusteredAspectMaps.clusteridAspectsMap.values()) {
                        for (ExtractedAspectAndModifier extractedAspectAndModifier : extractedAspectAndModifiers) {

                            String aspect = extractedAspectAndModifier.getFullAspect();//TODO: maybe change this to lemma. try out
//                            String aspect = extractedAspectAndModifier.getAspectLemma();//TODO: maybe change this to lemma. try out

                            aspectSet.add(aspect.toLowerCase());

                            Set<String> aspectsCluster = aspectClusterSet.get(extractedAspectAndModifier.getBusinessId());
                            if (aspectsCluster == null) {
                                aspectsCluster = new HashSet<>();
                                aspectClusterSet.put(extractedAspectAndModifier.getBusinessId(), aspectsCluster);
                            }
                            aspectsCluster.add(aspect);

                        }
                    }
                    //evaluation

                    // > general
                    Double generalOverlap = evaluator.evaluateManualVSAutomaticGeneralOverlap(aspectSet);

                    // > per cluster
                    HashMap<String, Double> perClusterOverlap = evaluator.evaluateManualVSAutomaticClusters(aspectClusterSet);
                    double sumOverlap = 0.0;
                    for (Double overlap : perClusterOverlap.values()) {
                        sumOverlap += overlap;
                    }
                    Double avgOverlapPerCluster = sumOverlap / perClusterOverlap.size();


                    resultString += param_minpts + ";" + param_eps + ";" + param_clusters + ";" + generalOverlap + ";" + avgOverlapPerCluster + "\n";



                    counter++;

                    param_eps = param_eps + INTERVAL_EPS;
                }
                param_minpts = param_minpts + INTERVAL_MINPTS;
            }
            param_clusters = param_clusters + INTERVAL_CLUSTERS;
        }

        BufferedWriter writerAspects = new BufferedWriter ( new FileWriter(new File("data/output/parameterOpt_FullAspect_SingleSimilarity_WuPalmer.csv")));
        writerAspects.write(resultString);
        writerAspects.close();
    }



    private static void executeClustering() throws IOException {

        ArrayList<ExtractedAspectAndModifier> result = new ArrayList<ExtractedAspectAndModifier>();

        //read business reviews
        //returns HashMap with <businessID, reviewText>
        HashMap<String, String> reviewsPerBusiness = ReviewCollector.readData("data/Phoenix_reviews_per_business_BarsRestCafes_CHINESE.csv", 50);

        //Create List of aspects per Business via dependency extractor
        result = new DependencyExtractor().pipe(result, reviewsPerBusiness);
        result = new Util().pipe(result);

        //Calculates sentiment value for each Extracted Aspect per Business and add attribute to aspect object
        result = SentimentCalculator.assignSentimentScore(result);

        //now create a new datastructure that will also be useful when trying to match sentiment values with cluster-ids. = aspectLemma --> object
        //no connection to businesses needed?
        HashMap<String, ArrayList<ExtractedAspectAndModifier>> mapAspectLemmaExtractedAspectAndModifier = Util.createConsolidatedDatastructure(result);


        /*
         * create data files for clustering:
         */
        AspectSimilarityDistanceModel model = new SimilarityCalculator().pipe(mapAspectLemmaExtractedAspectAndModifier.keySet());

        DbscanClustering.FILTERING_EPS = 1;
        DbscanClustering clusterer = new DbscanClustering(model);
        int aspectCountAfterDbscan = clusterer.removeOutliersWriterClusteringFiles("data/output/aspectsDbscanFiltered.txt", "data/output/distancesDbscanFiltered.txt");

        int nclusters = (int) (aspectCountAfterDbscan * 0.2);
        PythonExec.executePythonKMedoids(nclusters);


        /*
         * use clustering result
         */

        Util.ClusteredAspectMaps clusteredAspectMaps = Util.createClusteredAspectMaps("data/clusteringresult/aspectClusterDbscanFiltered.csv", "data/output/aspectsDbscanFiltered.txt", mapAspectLemmaExtractedAspectAndModifier);

        //per business sentiment calculation
        HashMap<String, Map<ExtractedAspectAndModifier, Double>> finalResultPerBusiness = new HashMap<>();

        HashMap<String, HashMap<Integer, ArrayList<ExtractedAspectAndModifier>>> businessClusterAspectlist = Util.buildBusinessClusterAspectIndex(clusteredAspectMaps);
        for (Map.Entry<String, HashMap<Integer, ArrayList<ExtractedAspectAndModifier>>> entry : businessClusterAspectlist.entrySet()) {
            String businessId = entry.getKey();
            HashMap<Integer, ArrayList<ExtractedAspectAndModifier>> clusterAspectlist = entry.getValue();

            Map<ExtractedAspectAndModifier, Double> orderedClustersPerBusiness = Util.orderClustersBySentimentPerBusiness(clusterAspectlist, clusteredAspectMaps.clusterIdCentroidMap);

            finalResultPerBusiness.put(businessId, orderedClustersPerBusiness);
        }

//        System.out.println(finalResultPerBusiness);

        BufferedWriter writerAspects = new BufferedWriter ( new FileWriter(new File("data/output/perBusinessResult_50_chinese_JCN_kmedoids.csv")));
        writerAspects.write("BusinessId;Attribute;Sentiment\n");
        for (String businessId : finalResultPerBusiness.keySet()) {
            Map<ExtractedAspectAndModifier, Double> mapClustersSentiments = finalResultPerBusiness.get(businessId);
            for (Map.Entry<ExtractedAspectAndModifier, Double> entry : mapClustersSentiments.entrySet()) {
                writerAspects.write(businessId+";"+entry.getKey().getFullAspect()+";"+entry.getValue()+"\n");
            }
        }
        writerAspects.close();


        //not per business but entire sentiment calculation
        Map<ExtractedAspectAndModifier, Double> orderedClustersPerBusiness = Util.orderClustersBySentimentPerBusiness(clusteredAspectMaps.clusteridAspectsMap, clusteredAspectMaps.clusterIdCentroidMap);
        BufferedWriter writer = new BufferedWriter ( new FileWriter(new File("data/output/overallResult_50_chinese_JCN_kmedoids.csv")));
        writer.write("Attribute;Sentiment\n");
        for (Map.Entry<ExtractedAspectAndModifier, Double> entry : orderedClustersPerBusiness.entrySet()) {
            writer.write(entry.getKey().getFullAspect()+";"+entry.getValue()+"\n");
        }
        writer.close();

//        System.out.println(orderedClustersPerBusiness);
    }
}
