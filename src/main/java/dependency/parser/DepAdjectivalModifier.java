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
public class DepAdjectivalModifier extends SemanticGraphEdgeEvaluator {
    public ArrayList<ExtractedAspectAndModifier> result;

    private static final String AMOD = "amod";

    private ArrayList<GovernorDependent> tmpAmodDependencies = new ArrayList<GovernorDependent>();

    public DepAdjectivalModifier(ArrayList<ExtractedAspectAndModifier> result){
        this.result = result;
    }

    public void evalSemanticGraphEdge(SemanticGraphEdge edge){
        IndexedWord dep = edge.getDependent();
        IndexedWord gov = edge.getGovernor();
        GrammaticalRelation relation = edge.getRelation();

        if (relation.toString().equals(AMOD)){
            this.saveTmpGovernorDependent(this.tmpAmodDependencies, gov, dep);
        }

    }

    public void endOfSentence() {
        for (GovernorDependent amodDep: this.tmpAmodDependencies){
            if(correctPOSTags(amodDep.gov,amodDep.dep)){

                String aspectExtension = this.getExtensionsAspect(amodDep.gov);
                String modifierExtension = this.getExtensionsModifier(amodDep.dep);

                ExtractedAspectAndModifier tuple = new ExtractedAspectAndModifier();
                tuple.aspect = aspectExtension + amodDep.gov.word();
                tuple.modifier = modifierExtension + amodDep.dep.word();


                result.add(tuple);
            }
        }

    }

    public void clear() {
        this.tmpAmodDependencies.clear();
    }

}
