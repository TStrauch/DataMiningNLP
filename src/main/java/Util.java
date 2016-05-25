import edu.stanford.nlp.process.Morphology;
import model.ExtractedAspectAndModifier;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by Timo on 07.05.16.
 */
public class Util {

    public HashMap<String, ArrayList<ExtractedAspectAndModifier>> pipe(HashMap<String, ArrayList<ExtractedAspectAndModifier>> input){
        return input;
    }

    public static HashMap<String, ArrayList<ExtractedAspectAndModifier>> createConsolidatedDatastructure(HashMap<String, ArrayList<ExtractedAspectAndModifier>> input){
        HashMap<String, ArrayList<ExtractedAspectAndModifier>> consolidation = new HashMap<String, ArrayList<ExtractedAspectAndModifier>>();

        for (ArrayList<ExtractedAspectAndModifier> aspectsPerBusiness : input.values()){
          for (ExtractedAspectAndModifier tuple : aspectsPerBusiness) {
            ArrayList<ExtractedAspectAndModifier> extractedAspectAndModifierList = consolidation.get(tuple.getAspectLemma());
            if (extractedAspectAndModifierList == null){
                extractedAspectAndModifierList = new ArrayList<ExtractedAspectAndModifier>();
                extractedAspectAndModifierList.add(tuple);
                consolidation.put(tuple.getAspectLemma(), extractedAspectAndModifierList);
            }
            else{
                extractedAspectAndModifierList.add(tuple);
            }
          }
        }

        return consolidation;

    }

    public static ClusteredAspectMaps createClusteredAspectMaps(String pathToClusterResult, String pathToAspects, HashMap<String, ArrayList<ExtractedAspectAndModifier>> map) throws IOException {
        BufferedReader readerClusterResult = new BufferedReader(new FileReader(new File(pathToClusterResult)));
        BufferedReader readerAspects = new BufferedReader(new FileReader(new File(pathToAspects)));

        HashMap<Integer, ArrayList<ExtractedAspectAndModifier>> clusteridAspectsMap = new HashMap<Integer, ArrayList<ExtractedAspectAndModifier>>();
        HashMap<Integer, ExtractedAspectAndModifier> clusterIdCentroidMap = new HashMap<Integer, ExtractedAspectAndModifier>();

        //read aspects
        ArrayList<String> aspectLemmasList = new ArrayList<String>();
        String line;
        while((line = readerAspects.readLine()) != null){
            aspectLemmasList.add(line);
        }


        while((line = readerClusterResult.readLine()) != null){
            String[] split = line.split(",");
            String lemma = split[0];
            int clusterid = Integer.parseInt(split[1]);

            //add to clusterIdAspectsMap
            ArrayList<ExtractedAspectAndModifier> aspectAndModifiers = clusteridAspectsMap.get(clusterid);
            if(aspectAndModifiers == null){
                aspectAndModifiers = new ArrayList<ExtractedAspectAndModifier>();
                clusteridAspectsMap.put(clusterid, aspectAndModifiers);
            }
            ArrayList<ExtractedAspectAndModifier> extractedAspectAndModifiers = map.get(lemma);
            aspectAndModifiers.addAll(extractedAspectAndModifiers);

            //check if centroid found
            String aspectLemma = aspectLemmasList.get(clusterid);
            ExtractedAspectAndModifier centroid = map.get(aspectLemma).get(0);
            clusterIdCentroidMap.put(clusterid, centroid);
        }

        ClusteredAspectMaps clusteredAspectMaps = new ClusteredAspectMaps();
        clusteredAspectMaps.clusteridAspectsMap = clusteridAspectsMap;
        clusteredAspectMaps.clusterIdCentroidMap = clusterIdCentroidMap;

        return clusteredAspectMaps;
    }

    public static Map<ExtractedAspectAndModifier, Double> getMostPositiveAndNegativeClusters(ClusteredAspectMaps input){
        HashMap<Integer, ExtractedAspectAndModifier> clusterIdCentroidMap = input.clusterIdCentroidMap;
        HashMap<Integer, ArrayList<ExtractedAspectAndModifier>> clusteridAspectsMap = input.clusteridAspectsMap;

        LinkedHashMap<ExtractedAspectAndModifier, Double> clusterIdSentiment = new LinkedHashMap<ExtractedAspectAndModifier, Double>();
        for (Integer clusterid : clusteridAspectsMap.keySet()) {
            double sentimentScore = 0.0;
            for (ExtractedAspectAndModifier extractedAspectAndModifier : clusteridAspectsMap.get(clusterid)) {
                sentimentScore += extractedAspectAndModifier.getSentimentScore();
            }
//            sentimentScore = sentimentScore / clusteridAspectsMap.get(clusterid).size();
            clusterIdSentiment.put(clusterIdCentroidMap.get(clusterid), sentimentScore);
        }

        //sort clusterIdSentiment
        List<Map.Entry<ExtractedAspectAndModifier, Double>> entries =
                new ArrayList<>(clusterIdSentiment.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<ExtractedAspectAndModifier, Double>>() {
            public int compare(Map.Entry<ExtractedAspectAndModifier, Double> a, Map.Entry<ExtractedAspectAndModifier, Double> b){
                return a.getValue().compareTo(b.getValue());
            }
        });

        Map<ExtractedAspectAndModifier, Double> sortedMap = new LinkedHashMap<ExtractedAspectAndModifier, Double>();
        for (Map.Entry<ExtractedAspectAndModifier, Double> entry : entries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;

    }

    public static class ClusteredAspectMaps{
        public HashMap<Integer, ArrayList<ExtractedAspectAndModifier>> clusteridAspectsMap;
        public HashMap<Integer, ExtractedAspectAndModifier> clusterIdCentroidMap;
    }
}
