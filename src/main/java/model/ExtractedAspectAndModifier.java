package model;

import edu.stanford.nlp.ling.IndexedWord;

import java.util.ArrayList;

/**
 * Created by Timo on 04.05.16.
 */
public class ExtractedAspectAndModifier {

    private IndexedWord aspectIndexedWord;
    private IndexedWord modifierIndexWord;
    private String aspectExtensions;
    private String modifierExtensions;

    public String aspectLemma;

    public static ExtractedAspectAndModifier getMock(String word){
        IndexedWord aspect = new IndexedWord();
        aspect.setWord(word);
        aspect.setTag("NN");

        IndexedWord mod = new IndexedWord();
        mod.setWord(word);
        mod.setTag("JJ");

        ExtractedAspectAndModifier mock = new ExtractedAspectAndModifier(aspect, mod);
        mock.setExtensions("","");
        mock.aspectLemma = word;

        return mock;
    }

    public ExtractedAspectAndModifier(IndexedWord asp, IndexedWord mod){
        this.aspectIndexedWord = asp;
        this.modifierIndexWord = mod;
    }

    public void setExtensions(String aspExt, String modExt){
        this.aspectExtensions = aspExt;
        this.modifierExtensions = modExt;
    }

    public String getFullAspect(){
        return this.aspectExtensions + this.aspectIndexedWord.word();
    }
    public String getInitialAspect(){
        return this.aspectIndexedWord.word();
    }
    public String getInitialModifier(){
        return this.modifierIndexWord.word();
    }
    public String getFullModifier(){
        return this.modifierExtensions + this.modifierIndexWord.word();
    }
    public String getAspectPOS(){
        return this.aspectIndexedWord.tag();
    }
    public String getModifierPOS(){
        return this.modifierIndexWord.tag();
    }

    public String toString(){
        return "("+this.getFullModifier()+", "+this.getFullAspect()+")";
    }
    public String toString(String type){
        if(type.equals("lemma")){
            return "("+this.getFullModifier()+", "+this.getFullAspect()+", "+aspectLemma+")";
        }
        return "("+this.getFullModifier()+", "+this.getFullAspect()+")";
    }
}
