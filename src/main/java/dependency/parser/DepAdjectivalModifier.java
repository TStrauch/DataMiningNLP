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


    public ArrayList<ExtractedAspectAndModifier> getResult() {
        return result;
    }

    public void setResult(ArrayList<ExtractedAspectAndModifier> result) {
        this.result = result;
    }

    private ArrayList<GovernorDependent> tmpAmodDependencies = new ArrayList<GovernorDependent>();

    public void evalSemanticGraphEdge(SemanticGraphEdge edge, String sentenceSentiment){
        IndexedWord dep = edge.getDependent();
        IndexedWord gov = edge.getGovernor();
        GrammaticalRelation relation = edge.getRelation();

        if (relation.toString().equals(AMOD)){
            this.saveTmpGovernorDependent(this.tmpAmodDependencies, gov, dep);
        }

    }

    public void endOfSentence(String sentenceSentiment) {
        for (GovernorDependent amodDep: this.tmpAmodDependencies){
            if(correctPOSTags(amodDep.gov,amodDep.dep)){

                ArrayList<IndexedWord> extensionsAspect = this.getExtensionsAspect(amodDep.gov);
                ArrayList<IndexedWord> extensionsModifier = this.getExtensionsModifier(amodDep.dep);

                ExtractedAspectAndModifier tuple = new ExtractedAspectAndModifier(amodDep.gov, amodDep.dep);
                tuple.setExtensions(extensionsAspect, extensionsModifier);

                tuple.setSentenceSentiment(sentenceSentiment);

                this.result.add(tuple);
            }
        }

    }

    public void clear() {
        this.tmpAmodDependencies.clear();
    }

}
