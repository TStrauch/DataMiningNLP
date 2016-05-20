import edu.stanford.nlp.process.Morphology;
import model.ExtractedAspectAndModifier;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Timo on 07.05.16.
 */
public class ResultIntermediateNLP {

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
}
