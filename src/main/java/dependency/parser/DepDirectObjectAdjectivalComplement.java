package dependency.parser;

import dependency.SemanticGraphEdgeEvaluator;
import dependency.extension.DependencyExtensionAspect;
import dependency.extension.DependencyExtensionModifier;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;
import model.ExtractedAspectAndModifier;
import model.GovernorDependent;

import java.util.ArrayList;
import java.util.List;

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
        IndexedWord gov = edge.getGovernor();
        GrammaticalRelation relation = edge.getRelation();

        if(relation.toString().equals(NSUBJ)){ //example: nsubj(good-11, music-3)
            this.saveTmpGovernorDependent(this.tmpNsubjDependencies, gov, dep);
        }
        else if(relation.toString().equals(XCOMP) || relation.toString().equals(ACOMP) || relation.toString().equals(DOBJ)){
            this.saveTmpGovernorDependent(this.tmpXcompDependencies, gov, dep);
        }
    }

    public void endOfSentence() {
        for (GovernorDependent nsubjDep: this.tmpNsubjDependencies){

            for (GovernorDependent xcompDep: this.tmpXcompDependencies){

                if(nsubjDep.gov.equals(xcompDep.gov) && correctPOSTags(nsubjDep.dep, xcompDep.dep)){

                    String aspectExtension = this.getExtensionsAspect(nsubjDep.dep);
                    String modifierExtension = this.getExtensionsModifier(xcompDep.gov);

                    ExtractedAspectAndModifier tuple = new ExtractedAspectAndModifier();
                    tuple.aspect = aspectExtension + nsubjDep.dep.word();
                    tuple.modifier = modifierExtension + xcompDep.dep.word();
                    result.add(tuple);
                }
            }
        }
    }

    public void clear() {
        this.tmpNsubjDependencies.clear();
        this.tmpXcompDependencies.clear();
    }

}
