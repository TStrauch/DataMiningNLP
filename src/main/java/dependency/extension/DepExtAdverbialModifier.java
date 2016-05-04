package dependency.extension;

import dependency.SemanticGraphEdgeEvaluator;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;

import java.util.HashMap;

/**
 * Created by Timo on 04.05.16.
 */
public class DepExtAdverbialModifier extends SemanticGraphEdgeEvaluator implements  DependencyExtensionModifier{

    public static final String ADVMOD = "advmod";

    private HashMap<String, String> tmpAdvmodDependencies = new HashMap();

    public void evalSemanticGraphEdge(SemanticGraphEdge edge) {
        IndexedWord dep = edge.getDependent();
        IndexedWord gov = edge.getGovernor();
        GrammaticalRelation relation = edge.getRelation();

        if(relation.toString().equals(ADVMOD)){
            this.tmpAdvmodDependencies.put(gov.word(), dep.word());
        }
    }

    public void endOfSentence() {
        //no need to do anything here
    }

    public void clear() {
        this.tmpAdvmodDependencies.clear();
    }

    public String getExtension(String word) {
        return this.tmpAdvmodDependencies.get(word);
    }
}
