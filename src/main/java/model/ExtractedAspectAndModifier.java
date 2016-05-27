package model;

import edu.stanford.nlp.ling.IndexedWord;

import java.util.ArrayList;

/**
 * Created by Timo on 04.05.16.
 */
public class ExtractedAspectAndModifier {

    private IndexedWord aspectIndexedWord;
    private IndexedWord modifierIndexWord;
    private ArrayList<IndexedWord> aspectExtensions;
    private ArrayList<IndexedWord> modifierExtensions;
    private double aspectSentimentScore;
    private String sentenceSentiment;
    private String businessId;


    public static ExtractedAspectAndModifier getMock(String word){
        IndexedWord aspect = new IndexedWord();

        aspect.setWord(word);
        aspect.setTag("NN");

        IndexedWord mod = new IndexedWord();
        mod.setWord(word);
        mod.setTag("JJ");

        ExtractedAspectAndModifier mock = new ExtractedAspectAndModifier(aspect, mod);
        mock.setExtensions(new ArrayList<IndexedWord>() ,new ArrayList<IndexedWord>());

        return mock;
    }

    public ExtractedAspectAndModifier(IndexedWord asp, IndexedWord mod){
        this.aspectIndexedWord = asp;
        this.modifierIndexWord = mod;
        this.aspectSentimentScore = 0.0;
    }

    public void setExtensions(ArrayList<IndexedWord> aspExt, ArrayList<IndexedWord> modExt){
        this.aspectExtensions = aspExt;
        this.modifierExtensions = modExt;
    }

    public IndexedWord getAspectIndexedWord() {
        return aspectIndexedWord;
    }

    public IndexedWord getModifierIndexWord() {
        return modifierIndexWord;
    }

    public String getFullAspect(){String s = "";
        for (IndexedWord modifierExtension : this.aspectExtensions) {
            s += modifierExtension.word() + " " + s;
        }

        return s + this.aspectIndexedWord.word();
    }
    public String getInitialAspect(){
        return this.aspectIndexedWord.word();
    }
    public String getInitialModifier(){
        return this.modifierIndexWord.word();
    }
    public String getFullModifier(){
        String s = "";
        for (IndexedWord modifierExtension : this.modifierExtensions) {
            s += modifierExtension.word() + " " + s;
        }

        return s + this.modifierIndexWord.word();
    }
    
    public ArrayList<IndexedWord> getModifierExtension(){
    	return this.modifierExtensions;
    }
    
    public ArrayList<IndexedWord> getAspectExtension(){
    	return this.aspectExtensions;
    }
    
    public String getAspectPOS(){
        return this.aspectIndexedWord.tag();
    }
    public String getModifierPOS(){
        return this.modifierIndexWord.tag();
    }
    public String getAspectLemma(){ return this.aspectIndexedWord.lemma(); }
    public String getModifierLemma(){ return this.modifierIndexWord.lemma(); }

    public String toString(){
        return "("+this.getFullModifier()+", "+this.getFullAspect()+")";
    }
    public String toString(String type){
        if(type.equals("lemma")){
            return "("+this.getFullModifier()+", "+this.getFullAspect()+")";
        }
        return "("+this.getFullModifier()+", "+this.getFullAspect()+")";
    }
    
    public double getSentimentScore(){
    	return aspectSentimentScore;
    }
    
    public void setSentimentScore(double score){
    	aspectSentimentScore = score;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getSentenceSentiment() {
        return sentenceSentiment;
    }

    public void setSentenceSentiment(String sentenceSentiment) {
        this.sentenceSentiment = sentenceSentiment;
    }
}
