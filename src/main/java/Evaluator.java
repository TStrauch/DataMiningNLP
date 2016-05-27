import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Timo on 26.05.16.
 */
public class Evaluator {
    private HashMap<String, Set<String>> manualClusters = new HashMap<>();
    private Set<String> sentences = new HashSet<>();

    public void readManualClusters(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File(path)));

        reader.readLine();

        String line;
        while((line = reader.readLine()) != null){
            String[] split = line.split(";");
            String aspect = split[0];
            String category = split[1];
            String sentence = split[2];

            if(category.equals("")){
                continue;
            }

            Set<String> aspects = this.manualClusters.get(category);
            if (aspects == null){
                aspects = new HashSet<>();
                this.manualClusters.put(category, aspects);
            }
            aspects.add(aspect.toLowerCase());

            this.sentences.add(sentence);
        }
    }

    public Double evaluateManualVSAutomaticGeneralOverlap(Set<String> automaticAspects){
        Set<String> total = new HashSet<>();
        for (Set<String> strings : this.manualClusters.values()) {
            total.addAll(strings);
        }
        Set<String> intersect = intersect(total, automaticAspects);
        double intersectionShare = (double)intersect.size() / (double)total.size();


        return intersectionShare;

    }

    public HashMap<String, Double> evaluateManualVSAutomaticClusters(HashMap<String, Set<String>> automaticClusters){
        HashMap<String, Double> result = new HashMap<>();

        for (Map.Entry<String, Set<String>> manCl : manualClusters.entrySet()) {
            String category = manCl.getKey();
            Set<String> manualCluster = manCl.getValue();

            double maxOverlap = 0.0;

            for (Set<String> automaticCluster : automaticClusters.values()) {
                Set<String> intersect = this.intersect(manualCluster, automaticCluster);
                double intersectionShare = (double)intersect.size() / (double)manualCluster.size();

                if (intersectionShare > maxOverlap){
                    maxOverlap = intersectionShare;
                }
            }

            result.put(category, maxOverlap);
        }

        return result;

    }

    private Set<String> intersect(Set<String> s1, Set<String> s2){
        Set<String> i = new HashSet<>(s1);
        i.retainAll(s2);
        return i;
    }


    public HashMap<String, Set<String>> getManualClusters() {
        return manualClusters;
    }

    public void setManualClusters(HashMap<String, Set<String>> manualClusters) {
        this.manualClusters = manualClusters;
    }

    public Set<String> getSentences() {
        return sentences;
    }

    public void setSentences(Set<String> sentences) {
        this.sentences = sentences;
    }
}
