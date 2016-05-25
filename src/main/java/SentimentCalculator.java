import java.io.IOException;
import java.util.*;

import model.ExtractedAspectAndModifier;
import sentimentAnalysis.SWNExtractor;
import edu.stanford.nlp.ling.IndexedWord;

/*
 * Created by Natalie, 21.05.16 
 */

public class SentimentCalculator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static HashMap<String, ArrayList<ExtractedAspectAndModifier>> assignSentimenScore(HashMap<String, ArrayList<ExtractedAspectAndModifier>> allBusinesses) throws IOException{
		ArrayList<IndexedWord> allModifiers;
		LinkedHashMap<IndexedWord, Double> modsWithScore;
		ArrayList<ExtractedAspectAndModifier> aspectsWithScore;
		
		//Initialize Sentiment lexicon
		SWNExtractor dictionary = new SWNExtractor();
		
		//loop through allWords per Business
		for (HashMap.Entry<String, ArrayList<ExtractedAspectAndModifier>> allWords : allBusinesses.entrySet()){
		  //Initialize ArrayList of modified words for each business
		  aspectsWithScore = new ArrayList<ExtractedAspectAndModifier>();
			
		  for (ExtractedAspectAndModifier modAspect : allWords.getValue()){
			allModifiers = new ArrayList<IndexedWord>();
			modsWithScore = new LinkedHashMap<IndexedWord, Double>();
			
			//get all modifiers of modAspect into one data structure
			allModifiers.add(modAspect.getModifierIndexWord());
			allModifiers.addAll(modAspect.getModifierExtension());
			
			//loop over modifier words
			for(IndexedWord mod : allModifiers){
				//run a stemmer / lemmatizer / synonymizer / etc. on each word
		        //access the sentiment lexicon and get a value per word (maybe use different versions: 1. original, 2. stemmed, 3. synomized to make sure to get a result)
		        //take the sentiment values and combine them in a sensible way (e.g. weigh the right-most adjective higher etc)
				String word = mod.lemma();
				String wordTypeMarker = getWordType(mod); //based on mod.tag() 
				
				//calculate sentiment via Sentiment lexicon FOR MODIFIER words
				double score;
				if (wordTypeMarker.equals("")) {
					//No wordtype determined
					//TODO we could check with synonyms, original-version etc.?
					score = 0.0;
				} else {
					score = dictionary.extract(word, wordTypeMarker);					
				}
				
				// SAVE sentiment score with modifier as key
				modsWithScore.put(mod, score);
			}
			
			//calculate overall Sentiment Score for the aspect!
			modAspect.setSentimentScore(calculateFinalScore(modsWithScore));
			aspectsWithScore.add(modAspect);			
		  }
		  allBusinesses.put(allWords.getKey(), aspectsWithScore);
		}
		
		return allBusinesses;
	}
	
	private static Double calculateFinalScore(LinkedHashMap<IndexedWord, Double> modsWithScore){
		Boolean mainMod = true;
		Double finalScore = 0.0;
		Double nextScore = 0.0;
		String currentScorePolarity = ""; //can be positive or negative
		String nextScorePolarity = ""; //can be positive or negative
		
		//loop over modifier words
		for (Map.Entry<IndexedWord, Double> nextMod : modsWithScore.entrySet()){
			if (mainMod){
				finalScore = nextMod.getValue();
				mainMod = false;
			} else //extension modifiers
			{
				//Determine polarity of current scores
				currentScorePolarity = checkScorePolarity(finalScore);
				nextScore = nextMod.getValue();
				nextScorePolarity = checkScorePolarity(nextScore);
								
				//Handle exception case (s. paper 4a)
				if (currentScorePolarity.equals("positive") && nextScorePolarity.equals("negative")){
					finalScore = finalScore - (finalScore * Math.abs(nextScore));
				} else {
					finalScore = Math.abs(finalScore) + (1 - Math.abs(finalScore) * Math.abs(nextScore));					
					//Check polarity of previous "current" score and negate if necessary
					if(currentScorePolarity.equals("negative")){
						finalScore = finalScore * (-1);
					}
				}				
			}			
		}		
		
		return finalScore;
	}
	
	private static String checkScorePolarity(double score){
		if (score > 0.0){
			return "positive";
		} else if (score < 0.0){
			return "negative";
		} else {
			return "neutral";
		}
	}
	
	private static String getWordType(IndexedWord word){
		String wordType = "";
		
		//Do the mapping here
		if(word != null && word.tag() != null){
			if (isNoun(word)){
				wordType = "n";
			} else if(isVerb(word)){
				wordType = "v";
			} else if (isAdjective(word)){
				wordType = "a";
				//TODO what about satellites? -> "s" in SWN
			} else if (isAdverb(word)){
				wordType = "r";
			} else {
				//TODO what if no category can be assigned
				wordType = "";
			}
		}
		
		//TODO check if there might be an alternative to POS tagger?
		return wordType;
	}
		
	private static boolean isVerb(IndexedWord verb){
	    	return verb.tag().toString().equals("VB")
	    			||verb.tag().toString().equals("VBD")
	    			||verb.tag().toString().equals("VBG")
	    			||verb.tag().toString().equals("VBN")
	    			||verb.tag().toString().equals("VBP")
	    			||verb.tag().toString().equals("VBZ");
	    }

	    private static boolean isNoun(IndexedWord noun){
	        return noun.tag().toString().equals("NN")
	                || noun.tag().toString().equals("NNS")
	                || noun.tag().toString().equals("NNP")
	                || noun.tag().toString().equals("NNPS");

	    }

	    private static boolean isAdjective(IndexedWord adjective){
	        return adjective.tag().toString().equals("JJ")
	                || adjective.tag().toString().equals("JJR")
	                || adjective.tag().toString().equals("JJS")
	                || adjective.tag().toString().equals("JJ");
	    }

	    private static boolean isAdverb(IndexedWord adverb){
	        return adverb.tag().toString().equals("RB")
	                || adverb.tag().toString().equals("RBR")
	                || adverb.tag().toString().equals("RBS")
	                || adverb.tag().toString().equals("WRB");
	    }
	
}
