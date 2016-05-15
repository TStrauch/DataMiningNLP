import edu.stanford.nlp.process.Morphology;
import model.ExtractedAspectAndModifier;

import java.util.ArrayList;

/**
 * Created by Timo on 07.05.16.
 */
public class ResultIntermediateNLP {

    public ArrayList<ExtractedAspectAndModifier> pipe(ArrayList<ExtractedAspectAndModifier> input){
        return new ResultIntermediateNLP().lemmatize(input);
    }

    private ArrayList<ExtractedAspectAndModifier> lemmatize(ArrayList<ExtractedAspectAndModifier> input){
        Morphology morphology = new Morphology();
        for (ExtractedAspectAndModifier tuple: input){
            String tag = tuple.getAspectPOS();
            String word = tuple.getInitialAspect();
            tuple.aspectLemma = morphology.lemma(word, tag);
        }

        return input;
    }
}
