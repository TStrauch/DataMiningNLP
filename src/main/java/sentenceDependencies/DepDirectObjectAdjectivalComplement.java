package sentenceDependencies;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;

import java.util.ArrayList;

/**
 * Created by Timo on 04.05.16.
 */
public class DepDirectObjectAdjectivalComplement extends SemanticGraphEdgeEvaluator {
    public ArrayList<ExtractedAspectAndModifier> result;

    private static final String NSUBJ = "nsubj";
    private static final String XCOMP = "xcomp";
    private static final String DOBJ = "dobj";
    private static final String ACOMP = "acomp";

    private ArrayList<GovernorDependent> tmpNsubjDependencies = new ArrayList<GovernorDependent>();
    private ArrayList<GovernorDependent> tmpXcompDependencies = new ArrayList<GovernorDependent>();

    public DepDirectObjectAdjectivalComplement(ArrayList<ExtractedAspectAndModifier> result){
        this.result = result;
    }

    public void evalSemanticGraphEdge(SemanticGraphEdge edge) {
        IndexedWord dep = edge.getDependent();
        String dependent = dep.word();
        IndexedWord gov = edge.getGovernor();
        String governor = gov.word();
        GrammaticalRelation relation = edge.getRelation();

        if(relation.toString().equals(NSUBJ)){ //example: nsubj(good-11, music-3)
            this.saveTmpGovernorDependent(this.tmpNsubjDependencies, gov, dep);

            for (GovernorDependent tmp: this.tmpXcompDependencies){
                if (governor.equals(tmp.gov.word()) && correctPOSTags(dep, tmp.dep)){
                    ExtractedAspectAndModifier tuple = new ExtractedAspectAndModifier();
                    tuple.aspect = dependent;
                    tuple.modifier = tmp.dep.word();
                    result.add(tuple);
                }
            }

        }

        if(relation.toString().equals(XCOMP) || relation.toString().equals(ACOMP) || relation.toString().equals(DOBJ)){
            this.saveTmpGovernorDependent(this.tmpXcompDependencies, gov, dep);

            for (GovernorDependent tmp: this.tmpNsubjDependencies){
                if (governor.equals(tmp.gov.word()) && correctPOSTags(tmp.dep, dep)){
                    ExtractedAspectAndModifier tuple = new ExtractedAspectAndModifier();
                    tuple.aspect = tmp.dep.word();
                    tuple.modifier = dependent;
                    result.add(tuple);
                }
            }
        }
    }

    public void clear() {
        this.tmpNsubjDependencies.clear();
        this.tmpXcompDependencies.clear();
    }

    private void saveTmpGovernorDependent(ArrayList<GovernorDependent> list, IndexedWord gov, IndexedWord dep){
        GovernorDependent tmpTupel = new GovernorDependent();
        tmpTupel.gov = gov;
        tmpTupel.dep = dep;
        list.add(tmpTupel);
    }
}
