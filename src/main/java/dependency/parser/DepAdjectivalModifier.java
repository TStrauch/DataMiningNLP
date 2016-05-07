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
public class DepAdjectivalModifier extends SemanticGraphEdgeEvaluator {

    private static final String AMOD = "amod";
    private ArrayList<ExtractedAspectAndModifier> result;

    public DepAdjectivalModifier(ArrayList<ExtractedAspectAndModifier> input){
        this.result = input;
    }

    private ArrayList<GovernorDependent> tmpAmodDependencies = new ArrayList<GovernorDependent>();

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

                ExtractedAspectAndModifier tuple = new ExtractedAspectAndModifier(amodDep.gov, amodDep.dep);
                tuple.setExtensions(aspectExtension, modifierExtension);

                this.result.add(tuple);
            }
        }

    }

    public void clear() {
        this.tmpAmodDependencies.clear();
    }

}
