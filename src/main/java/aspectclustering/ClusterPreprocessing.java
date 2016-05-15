package aspectclustering;

import edu.stanford.nlp.util.IntTuple;
import model.AspectSimilarityDistanceModel;
import model.SimilarityPair;
import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.core.*;
import net.sf.javaml.distance.DistanceMeasure;

import java.io.*;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Timo on 12.05.16.
 */
public class ClusterPreprocessing {
    private static final String SIMILARITY_FILE = "data/outputSimilarity.csv";

    public static HashMap<IntTuple, Double> mapNumbersSimilarity = new HashMap<IntTuple, Double>();
    public static double minDistance = 0;
    public static double maxDistance = 0;

    public static void main (String[] ars) throws IOException {
        ClusterPreprocessing clusterer = new ClusterPreprocessing();
        clusterer.cluster();

    }

    public void cluster() throws IOException {
        //encode the aspects through numbers
        HashMap<Integer, String> mapNumbersAspects = new HashMap<Integer, String>();
        HashMap<String, DenseInstance> mapAspectsNumber = new HashMap<String, DenseInstance>();
        Dataset dataset = new DefaultDataset();


//        new IntTuple(new int[]{1,2});

        BufferedReader reader = new BufferedReader(new FileReader(new File(SIMILARITY_FILE)));

        String line;
        int id = 1;
        while((line = reader.readLine()) != null){
            String[] split = line.split(";");
            String aspect1 = split[0];
            String aspect2 = split[1];
            Double similarity = Double.parseDouble(split[2]);

            //add to mapNumbersAspects
            if(mapAspectsNumber.get(aspect1) == null) {
                DenseInstance inst = new DenseInstance(new double[]{id});
                mapAspectsNumber.put(aspect1, inst);
                mapNumbersAspects.put(id, aspect1);
                dataset.add(inst);
                id++;
            }
            if (mapAspectsNumber.get(aspect2) == null){
                DenseInstance inst = new DenseInstance(new double[]{id});
                mapAspectsNumber.put(aspect2, inst);
                mapNumbersAspects.put(id, aspect2);
                dataset.add(inst);
                id++;
            }


            //put similarity for id-pair
            int id1 = (int) mapAspectsNumber.get(aspect1).value(0);
            int id2 = (int) mapAspectsNumber.get(aspect2).value(0);
            IntTuple tuple1 = new IntTuple(new int[]{id1,id2});
            IntTuple tuple2 = new IntTuple(new int[]{id2,id1});

            mapNumbersSimilarity.put(tuple1, (1-similarity)); //1-similiary = distance
            mapNumbersSimilarity.put(tuple2, (1-similarity)); //1-similiary = distance

            double distance = 1 - similarity;
            if (distance < minDistance){
                minDistance = distance;
            }
            if(distance > maxDistance){
                maxDistance = distance;
            }
        }

        //set distance of same ids to minValue
        for (int i=1; i < id; i++){
            mapNumbersSimilarity.put(new IntTuple(new int[]{i,i}), minDistance);
        }

//        for (int i=1; i < id; i++){
//            for (int j=1; j < id; j++){
//                if(mapNumbersSimilarity.get(new IntTuple(new int[]{i,j})) == null){
//                    System.out.println("-----mistmatch-----");
//                    System.out.println("id: "+i+"; aspect: "+mapNumbersAspects.get(i));
//                    System.out.println("id: "+j+"; aspect: "+mapNumbersAspects.get(j));
//                }
//            }
//        }

        //variable dataset contains the data


        //cluster the dataset with k-medoids and provide a custom distanceMeasure object


        Clusterer clusterer = new KMeans(100, 50, new CustDistanceMeasure());

        Dataset[] cluster = clusterer.cluster(dataset);

        System.out.println(cluster);
    }

    private class CustDistanceMeasure implements DistanceMeasure{

        public double measure(Instance instance1, Instance instance2) {
            int id1 = (int) instance1.value(0);
            int id2 = (int) instance2.value(0);
//            System.out.println(id1+";"+id2);
            double distance = ClusterPreprocessing.mapNumbersSimilarity.get(new IntTuple(new int[]{id1,id2}));
            return distance;
        }

        public boolean compare(double distance1, double distance2) {
            return distance1 < distance2;
        }

        public double getMinValue() {
            return ClusterPreprocessing.minDistance;
        }

        public double getMaxValue() {
            return ClusterPreprocessing.maxDistance;
        }
    }

    private class DenseInstanceTuple{
        public DenseInstance d1;
        public DenseInstance d2;

        public DenseInstanceTuple(DenseInstance d1, DenseInstance d2){
            this.d1 = d1;
            this.d2 = d2;
        }
    }




}
