package sentenceDependencies;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;

import java.util.ArrayList;

/**
 * Created by Timo on 04.05.16.
 */
public class DepAdjectivalModifier extends SemanticGraphEdgeEvaluator{
    public ArrayList<ExtractedAspectAndModifier> result;

    private static final String AMOD = "amod";

    public DepAdjectivalModifier(ArrayList<ExtractedAspectAndModifier> result){
        this.result = result;
    }

    public void evalSemanticGraphEdge(SemanticGraphEdge edge){
        IndexedWord dep = edge.getDependent();
        String dependent = dep.word();
        IndexedWord gov = edge.getGovernor();
        String governor = gov.word();
        GrammaticalRelation relation = edge.getRelation();

        if (relation.toString().equals(AMOD)){
            if(correctPOSTags(gov,dep)){
                ExtractedAspectAndModifier tuple = new ExtractedAspectAndModifier();
                tuple.aspect = governor;
                tuple.modifier = dependent;
                result.add(tuple);
            }
        }

    }

    public void clear() {
        //no need to clear anything
    }

}
