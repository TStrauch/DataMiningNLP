package dependency.extension;

import dependency.SemanticGraphEdgeEvaluator;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Timo on 04.05.16.
 */
public class DepExtCompoundNoun extends SemanticGraphEdgeEvaluator implements DependencyExtensionAspect {

    public static final String COMPOUND = "compound";

    private HashMap<String, String> tmpCompoundDependencies = new HashMap();

    public void evalSemanticGraphEdge(SemanticGraphEdge edge) {
        IndexedWord dep = edge.getDependent();
        IndexedWord gov = edge.getGovernor();
        GrammaticalRelation relation = edge.getRelation();

        if(relation.toString().equals(COMPOUND)){
            this.tmpCompoundDependencies.put(gov.word(), dep.word());
        }
    }

    public void endOfSentence() {
        //no need to do anything here
    }

    public void clear() {
        this.tmpCompoundDependencies.clear();
    }

    public String getExtension(String word) {
        return this.tmpCompoundDependencies.get(word);
    }
}
