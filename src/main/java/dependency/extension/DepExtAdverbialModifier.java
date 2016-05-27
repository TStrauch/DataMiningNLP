package dependency.extension;

import dependency.SemanticGraphEdgeEvaluator;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Timo on 04.05.16.
 */
public class DepExtAdverbialModifier extends SemanticGraphEdgeEvaluator implements  DependencyExtensionModifier{

    public static final String ADVMOD = "advmod";

    private HashMap<String, ArrayList<IndexedWord>> tmpAdvmodDependencies = new HashMap();

    public void evalSemanticGraphEdge(SemanticGraphEdge edge, String sentenceSentiment) {
        IndexedWord dep = edge.getDependent();
        IndexedWord gov = edge.getGovernor();
        GrammaticalRelation relation = edge.getRelation();

        if(relation.toString().equals(ADVMOD)){
            ArrayList<IndexedWord> indexedWords = this.tmpAdvmodDependencies.get(gov.word());
            if (indexedWords == null){
                indexedWords = new ArrayList<IndexedWord>();
                this.tmpAdvmodDependencies.put(gov.word(), indexedWords);
            }
            indexedWords.add(dep);
        }
    }

    public void endOfSentence(String sentenceSentiment) {
        //no need to do anything here
    }

    public void clear() {
        this.tmpAdvmodDependencies.clear();
    }

    public ArrayList<IndexedWord> getExtension(String word) {
        return this.tmpAdvmodDependencies.get(word);
    }
}
