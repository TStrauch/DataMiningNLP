import edu.stanford.nlp.process.Morphology;
import model.ExtractedAspectAndModifier;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by Timo on 07.05.16.
 */
public class Util {

    public ArrayList<ExtractedAspectAndModifier> pipe(ArrayList<ExtractedAspectAndModifier> input){
        return input;
    }

    public static HashMap<String, ArrayList<ExtractedAspectAndModifier>> createConsolidatedDatastructure(ArrayList<ExtractedAspectAndModifier> input){
        HashMap<String, ArrayList<ExtractedAspectAndModifier>> consolidation = new HashMap<String, ArrayList<ExtractedAspectAndModifier>>();

          for (ExtractedAspectAndModifier tuple : input) {
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
                aspectAndModifiers = new ArrayList<>();
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

    public static HashMap<String, HashMap<Integer, ArrayList<ExtractedAspectAndModifier>>> buildBusinessClusterAspectIndex(ClusteredAspectMaps input){
        HashMap<Integer, ExtractedAspectAndModifier> clusterIdCentroidMap = input.clusterIdCentroidMap;
        HashMap<Integer, ArrayList<ExtractedAspectAndModifier>> clusteridAspectsMap = input.clusteridAspectsMap;

        //build up index of businessid --> clusterid --> list
        HashMap<String, HashMap<Integer, ArrayList<ExtractedAspectAndModifier>>> businessClusterAspectlist = new HashMap<>();

        for (Map.Entry<Integer, ArrayList<ExtractedAspectAndModifier>> clusterIdAspects : clusteridAspectsMap.entrySet()) {

            int clusterId = clusterIdAspects.getKey();

            for (ExtractedAspectAndModifier extractedAspectAndModifier : clusterIdAspects.getValue()) {

                String tmpBusinessId = extractedAspectAndModifier.getBusinessId();
                HashMap<Integer, ArrayList<ExtractedAspectAndModifier>> clusterAspectlist = businessClusterAspectlist.get(tmpBusinessId);
                if (clusterAspectlist == null){
                    clusterAspectlist = new HashMap<>();
                    businessClusterAspectlist.put(tmpBusinessId, clusterAspectlist);
                }

                ArrayList<ExtractedAspectAndModifier> aspectList = clusterAspectlist.get(clusterId);
                if (aspectList == null){
                    aspectList = new ArrayList<>();
                    clusterAspectlist.put(clusterId, aspectList);
                }

                //now we know there is a clusterAspectList and a aspectList
                aspectList.add(extractedAspectAndModifier);
            }

        }

        return businessClusterAspectlist;
    }

    public static Map<ExtractedAspectAndModifier, Double> orderClustersBySentimentPerBusiness(
            HashMap<Integer, ArrayList<ExtractedAspectAndModifier>> clusteridAspectsMap,
            HashMap<Integer, ExtractedAspectAndModifier> clusterIdCentroidMap){


        LinkedHashMap<ExtractedAspectAndModifier, Double> clusterIdSentiment = new LinkedHashMap<>();
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
