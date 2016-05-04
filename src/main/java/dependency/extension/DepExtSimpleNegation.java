package dependency.extension;

import dependency.SemanticGraphEdgeEvaluator;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;

import java.util.HashMap;

/**
 * Created by Timo on 04.05.16.
 */
public class DepExtSimpleNegation extends SemanticGraphEdgeEvaluator implements DependencyExtensionModifier, DependencyExtensionAspect {

    public static final String NEG = "neg";

    private HashMap<String, String> tmpNegDependencies = new HashMap();

    public void evalSemanticGraphEdge(SemanticGraphEdge edge) {
        IndexedWord dep = edge.getDependent();
        IndexedWord gov = edge.getGovernor();
        GrammaticalRelation relation = edge.getRelation();

        if(relation.toString().equals(NEG)){
            this.tmpNegDependencies.put(gov.word(), dep.word());
        }
    }

    public void endOfSentence() {
        //no need to do anything here
    }

    public void clear() {
        this.tmpNegDependencies.clear();
    }

    public String getExtension(String word) {
        if(this.tmpNegDependencies.get(word) != null){
            if (this.tmpNegDependencies.get(word).equals("no")){
                return "no";
            }
            else {
                return "not";
            }
        }
        return null;
    }
}
