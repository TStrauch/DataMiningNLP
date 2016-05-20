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

    public ArrayList<IndexedWord> getExtension(String word) {
        if(this.tmpNegDependencies.get(word) != null){
            if (this.tmpNegDependencies.get(word).equals("no")){
                IndexedWord w = new IndexedWord();
                w.setWord("no");
                w.setLemma("no");
                ArrayList<IndexedWord> l = new ArrayList<IndexedWord>();
                l.add(w);
                return l;
            }
            else {
                IndexedWord w = new IndexedWord();
                w.setWord("not");
                w.setLemma("not");
                ArrayList<IndexedWord> l = new ArrayList<IndexedWord>();
                l.add(w);
                return l;
            }
        }
        return null;
    }
}
