package dependency.parser;

import dependency.SemanticGraphEdgeEvaluator;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;
import model.ExtractedAspectAndModifier;
import model.GovernorDependent;

import java.util.ArrayList;

/**
 * Created by Timo on 04.05.16.
 */
public class DepComplementCopularVerb extends SemanticGraphEdgeEvaluator {
    private static final String NSUBJ = "nsubj";
    private static final String COP = "cop";

    private ArrayList<GovernorDependent> tmpNsubjDependencies = new ArrayList<GovernorDependent>();
    private ArrayList<GovernorDependent> tmpCopDependencies = new ArrayList<GovernorDependent>();
    private ArrayList<ExtractedAspectAndModifier> result;

    public DepComplementCopularVerb(ArrayList<ExtractedAspectAndModifier> input){
        this.result = input;
    }

    public void evalSemanticGraphEdge(SemanticGraphEdge edge) {
        IndexedWord dep = edge.getDependent();
        IndexedWord gov = edge.getGovernor();
        GrammaticalRelation relation = edge.getRelation();

        if(relation.toString().equals(NSUBJ)){ //example: nsubj(good-11, music-3)
            this.saveTmpGovernorDependent(this.tmpNsubjDependencies, gov, dep);
        }
        else if(relation.toString().equals(COP)){
            this.saveTmpGovernorDependent(this.tmpCopDependencies, gov, dep);
        }
    }

    public void endOfSentence() {
        for (GovernorDependent nsubjDep: this.tmpNsubjDependencies){

            for (GovernorDependent copDep: this.tmpCopDependencies){

                if(nsubjDep.gov.equals(copDep.gov) && correctPOSTags(nsubjDep.dep, nsubjDep.gov)){

                    String aspectExtension = this.getExtensionsAspect(nsubjDep.dep);
                    String modifierExtension = this.getExtensionsModifier(nsubjDep.gov);

                    ExtractedAspectAndModifier tuple = new ExtractedAspectAndModifier(nsubjDep.dep, nsubjDep.gov);
                    tuple.setExtensions(aspectExtension, modifierExtension);

                    this.result.add(tuple);
                }
            }
        }
    }

    public void clear() {
        this.tmpNsubjDependencies.clear();
        this.tmpCopDependencies.clear();
    }

}
