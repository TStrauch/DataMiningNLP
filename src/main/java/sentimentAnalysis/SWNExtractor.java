package sentimentAnalysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class SWNExtractor {
	private String pathToSWN = "data/SentiWordNet_3.0.0_20130122.txt";
    private HashMap<String, Double> dict;
    
    public SWNExtractor() throws IOException{
    	//The main sentiment dictionary representation
    	dict = new HashMap<String, Double>();
    	
    	//from string to a list of doubles
    	HashMap<String, HashMap<Integer, Double>> tempDict = new HashMap<String, HashMap<Integer, Double>>();
    	
    	BufferedReader csv = null;
    	try{
        	//read SentiWordNet dump
            csv =  new BufferedReader(new FileReader(pathToSWN));
            int lineNumber = 0;
            String line;         
            
            //TODO: escape comments!!
            while((line = csv.readLine()) != null) {
            	lineNumber++;
            	
            	//skip if it's a comment
            	if (!line.trim().startsWith("#")){
            		//tab-separation: Split line into: 0-POS 1-ID 2-PosScore 3-NegScore 4-SynsetTerms 5-Gloss
            		String[] data = line.split("\t");
            		
            		//check line validity
            		if (data.length != 6){
            			throw new IllegalArgumentException("Incorrect tabulation format in file, line : " + lineNumber);
            		}
       		
            		String wordTypeMarker = data[0];
            		//calculate one score out of two: subtract negative from positive score
            		Double synsetScore = Double.parseDouble(data[2])-Double.parseDouble(data[3]);
            		
            		//Synset Terms splitted by space
            		String[] synTerms = data[4].split(" ");
            		
            		//Go through all terms of current synset
            		for(String word: synTerms) {
            			//Get synterm and synterm rank
            			String[] synTermAndRank = word.split("#");
            			String synTerm = synTermAndRank[0] + "#" + wordTypeMarker;
            			
            			int synTermRank = Integer.parseInt(synTermAndRank[1]);
            			//Create a map of term -> {score of synSet-Rank1, score of synSet-Rank2, etc.}
            			
            			//Check if term is already in Dictionary, if not: add map
            			if(!tempDict.containsKey(synTerm)) {
            				tempDict.put(synTerm, new HashMap<Integer, Double>());
            			}
            			
            			//Add new synset link with new rank to synterm map
            			tempDict.get(synTerm).put(synTermRank, synsetScore);
            			
            		}
                }
            }
            
            //Calculate weighted average per word!
            for (HashMap.Entry<String, HashMap<Integer, Double>> entry : tempDict.entrySet()) {
            	
                String word = entry.getKey();
                HashMap<Integer, Double> synSetScoreMap = entry.getValue();
                
                //Weight the synsets according to their rank
                //Score = 1/2*first + 1/3*second ...
                //sum = 1/1 + 1/2 + 1/3
                double score = 0.0;
                double sum = 0.0;
                for (Map.Entry<Integer, Double> setScore : synSetScoreMap.entrySet()) {
                	score += setScore.getValue() / (double) setScore.getKey();
                	sum += 1.0 / (double) setScore.getKey();
                }
                score /= sum;
                
                dict.put(word, score);
            }
        }
        catch(Exception e){
        	e.printStackTrace();
        }  
    	finally {
    		if (csv != null){
    			csv.close();
    		}
    	}
    }
    
    public double extract(String word, String pos){
    	if (dict.get(word + "#" + pos) == null){
    		return 0.0;
    	}
    	return dict.get(word + "#" + pos);
    	
    	 /*Double total = new Double(0);
         if(_dict.get(word+"#n") != null)
              total = _dict.get(word+"#n") + total;
         if(_dict.get(word+"#a") != null)
             total = _dict.get(word+"#a") + total;
         if(_dict.get(word+"#r") != null)
             total = _dict.get(word+"#r") + total;
         if(_dict.get(word+"#v") != null)
             total = _dict.get(word+"#v") + total;
         return total;*/
    }
    
    public static void main(String args[]) throws IOException{
    	SWNExtractor swnTest = new SWNExtractor();
		
		System.out.println("good#a "+swnTest.extract("good", "a"));
		System.out.println("bad#a "+swnTest.extract("bad", "a"));
		System.out.println("blue#a "+swnTest.extract("blue", "a"));
		System.out.println("blue#n "+swnTest.extract("blue", "n"));
		System.out.println("suck#v "+swnTest.extract("suck", "v"));
		System.out.println("awesome#a "+swnTest.extract("awesome", "a"));
    }
}
