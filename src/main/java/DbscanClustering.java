import model.AspectSimilarityDistanceModel;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.distance.DistanceMeasure;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Timo on 22.05.16.
 */
public class DbscanClustering {

    public static void main(String[] args){

    }


    private double[][] distances;
    private HashMap<Integer, String> aspects;

    private static final double FILTERING_EPS = 0.85;
    private static final int FILTERING_MINPTS = 4;

    public DbscanClustering(AspectSimilarityDistanceModel model){
        this.distances = model.getDistances();
        this.aspects = model.getAspects();
    }

    public void cluster(){

        //0.7 ; 4
        DBSCANClusterer<DoublePoint> clusterer = new DBSCANClusterer<DoublePoint>(0.85, 4, new MyDistanceMeasure(this.distances));

        Collection<DoublePoint> points = this.createPoints(this.aspects);
        final List<Cluster<DoublePoint>> clusters = clusterer.cluster(points);

        System.out.println("\n");
        int counter = 1;
        for (Cluster<DoublePoint> cluster : clusters) {
            System.out.println("\n");
            System.out.println("Cluster "+counter+":");
            for (DoublePoint doublePoint : cluster.getPoints()) {
                System.out.println(this.aspects.get((int)(doublePoint.getPoint()[0])));
            }
            counter++;
        }

    }

    public void removeOutliersWriterClusteringFiles(String pathToAspects, String pathToDistances) throws IOException {
        DBSCANClusterer<DoublePoint> clusterer = new DBSCANClusterer<DoublePoint>(FILTERING_EPS, FILTERING_MINPTS, new MyDistanceMeasure(this.distances));
        Collection<DoublePoint> points = this.createPoints(this.aspects);
        final List<Cluster<DoublePoint>> clusters = clusterer.cluster(points);

        BufferedWriter writerAspects = new BufferedWriter ( new FileWriter(new File(pathToAspects)));
        BufferedWriter writerDistances = new BufferedWriter ( new FileWriter(new File(pathToDistances)));




        HashMap<Integer, Integer> mapOldToNewId = new HashMap<Integer, Integer>();

        //write aspects to file
        int newId = 0;
        for (Cluster<DoublePoint> cluster : clusters) {
            for (DoublePoint point1 : cluster.getPoints()) {
                int id1 = (int)(point1.getPoint()[0]);
                writerAspects.write(this.aspects.get(id1)+"\n");
                mapOldToNewId.put(newId, id1);
                newId++;
            }
            System.out.println("\n");
        }
        writerAspects.close();


        //create newDistances matrix
        double[][] newDistances = new double[mapOldToNewId.keySet().size()][mapOldToNewId.keySet().size()];

        for (Integer newId1 : mapOldToNewId.keySet()) {
            for (Integer newId2 : mapOldToNewId.keySet()) {

                Integer oldId1 = mapOldToNewId.get(newId1);
                Integer oldId2 = mapOldToNewId.get(newId2);

                double distance = this.distances[oldId1][oldId2];

                if(distance == 0.0){
                    System.out.println("");
                }

                newDistances[newId1][newId2] = distance;
            }
        }

        //save newDistances matrix to file
        int count = 0;
        for (int row = 0; row < newDistances.length; row++){
            for (int col = 0; col < newDistances[row].length; col++){

                if(col == newDistances[row].length-1){
                    writerDistances.write(String.valueOf(newDistances[row][col]));
                }
                else{
                    writerDistances.write(String.valueOf(newDistances[row][col])+", ");
                }
            }
            count++;

            if (count < newDistances.length){
                writerDistances.write("\n");
            }

        }
        writerDistances.close();

    }

    private Collection<DoublePoint> createPoints(HashMap<Integer, String> aspects){
        List<DoublePoint> points = new ArrayList<DoublePoint>();
        for (Integer id : aspects.keySet()) {
            DoublePoint p = new DoublePoint(new double[]{id});
            points.add(p);
        }

        return points;
    }

    public static class MyDistanceMeasure implements DistanceMeasure{

        private double[][] distances;

        public MyDistanceMeasure(double[][] distances){
            this.distances = distances;
        }

        public double compute(double[] a, double[] b) {
            int id1 = (int) a[0];
            int id2 = (int) b[0];

            return this.distances[id1][id2];
        }
    }
}


