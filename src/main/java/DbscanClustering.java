import model.AspectSimilarityDistanceModel;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.distance.DistanceMeasure;

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

    public DbscanClustering(AspectSimilarityDistanceModel model){
        this.distances = model.getDistances();
        this.aspects = model.getAspects();
    }

    public void cluster(){
        DBSCANClusterer<DoublePoint> clusterer = new DBSCANClusterer<DoublePoint>(0.80, 4, new MyDistanceMeasure(this.distances));
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


