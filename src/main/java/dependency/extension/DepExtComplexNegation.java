package dependency.extension;

import dependency.SemanticGraphEdgeEvaluator;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Timo on 04.05.16.
 */
public class DepExtComplexNegation extends SemanticGraphEdgeEvaluator implements DependencyExtensionModifier {

    public static final String AUX = "aux";
    public static final String COP = "cop";

    private HashMap<String, ArrayList<String>> tmpAuxDependencies = new HashMap();
    private HashMap<String, String> tmpCopDependencies = new HashMap();
    private Set<String> implicitNegations = new HashSet<String>();

    public void evalSemanticGraphEdge(SemanticGraphEdge edge, String sentenceSentiment) {
        IndexedWord dep = edge.getDependent();
        IndexedWord gov = edge.getGovernor();
        GrammaticalRelation relation = edge.getRelation();

        if(relation.toString().equals(AUX)){
            ArrayList<String> auxs = tmpAuxDependencies.get(gov.word());
            if (auxs == null){
                auxs = new ArrayList<String>();
                this.tmpAuxDependencies.put(gov.word(), auxs);
            }

            auxs.add(dep.word());
        }
        else if (relation.toString().equals(COP)){
            this.tmpCopDependencies.put(gov.word(), dep.word());
        }
    }

    public void endOfSentence(String sentenceSentiment) {
        for (String copKey: this.tmpCopDependencies.keySet()){
            ArrayList<String> auxs = this.tmpAuxDependencies.get(copKey);
            if (auxs != null){
                if (auxs.size() == 2){
                    implicitNegations.add(copKey);
                }
            }
        }
    }

    public void clear() {
        this.tmpCopDependencies.clear();
        this.tmpAuxDependencies.clear();
        this.implicitNegations.clear();
    }

    public ArrayList<IndexedWord> getExtension(String word) {
        if (this.implicitNegations.contains(word)){
            IndexedWord w = new IndexedWord();
            w.setWord("not");
            w.setLemma("not");
            ArrayList<IndexedWord> l = new ArrayList<IndexedWord>();
            l.add(w);
            return l;
        }
        return null;
    }
}
