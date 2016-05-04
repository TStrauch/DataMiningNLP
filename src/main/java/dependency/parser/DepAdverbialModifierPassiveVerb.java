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

public class DepAdverbialModifierPassiveVerb extends SemanticGraphEdgeEvaluator {
    public ArrayList<ExtractedAspectAndModifier> result;

    private static final String NSUBJPASS = "nsubjpass";
    private static final String ADVMOD = "advmod";

    private ArrayList<GovernorDependent> tmpNsubjpassDependencies = new ArrayList<GovernorDependent>();
    private ArrayList<GovernorDependent> tmpAdvmodDependencies = new ArrayList<GovernorDependent>();

    public DepAdverbialModifierPassiveVerb(ArrayList<ExtractedAspectAndModifier> result){
        this.result = result;
    }

    public void evalSemanticGraphEdge(SemanticGraphEdge edge) {
        IndexedWord dep = edge.getDependent();
        IndexedWord gov = edge.getGovernor();
        GrammaticalRelation relation = edge.getRelation();

        if(relation.toString().equals(NSUBJPASS)){ //example: nsubj(good-11, music-3)
            this.saveTmpGovernorDependent(this.tmpNsubjpassDependencies, gov, dep);
        }
        else if(relation.toString().equals(ADVMOD)){
            this.saveTmpGovernorDependent(this.tmpAdvmodDependencies, gov, dep);
        }
    }

    public void endOfSentence() {
        for (GovernorDependent nsubjpassDep: this.tmpNsubjpassDependencies){

            for (GovernorDependent advmodDep: this.tmpAdvmodDependencies){

                if(nsubjpassDep.gov.equals(advmodDep.gov) && correctPOSTags(nsubjpassDep.dep, advmodDep.dep)){

                    String aspectExtension = this.getExtensionsAspect(nsubjpassDep.dep);
                    String modifierExtension = this.getExtensionsModifier(advmodDep.dep);

                    ExtractedAspectAndModifier tuple = new ExtractedAspectAndModifier();
                    tuple.aspect = aspectExtension + nsubjpassDep.dep.word();
                    tuple.modifier = modifierExtension + advmodDep.dep.word();
                    result.add(tuple);
                }
            }
        }
    }

    public void clear() {
        this.tmpNsubjpassDependencies.clear();
        this.tmpAdvmodDependencies.clear();
    }
}

