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
public class DepDirectObjectAdjectivalComplement extends SemanticGraphEdgeEvaluator {
    private static final String NSUBJ = "nsubj";
    private static final String XCOMP = "xcomp";
    private static final String DOBJ = "dobj";
    private static final String ACOMP = "acomp";

    private ArrayList<GovernorDependent> tmpNsubjDependencies = new ArrayList<GovernorDependent>();
    private ArrayList<GovernorDependent> tmpXcompDependencies = new ArrayList<GovernorDependent>();
    private ArrayList<ExtractedAspectAndModifier> result;

    public ArrayList<ExtractedAspectAndModifier> getResult() {
        return result;
    }

    public void setResult(ArrayList<ExtractedAspectAndModifier> result) {
        this.result = result;
    }

    public void evalSemanticGraphEdge(SemanticGraphEdge edge, String sentenceSentiment) {
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

    public void endOfSentence(String sentenceSentiment) {
        for (GovernorDependent nsubjDep: this.tmpNsubjDependencies){

            for (GovernorDependent xcompDep: this.tmpXcompDependencies){

                if(nsubjDep.gov.equals(xcompDep.gov) && correctPOSTags(nsubjDep.dep, xcompDep.dep)){

                    ArrayList<IndexedWord> extensionsAspect = this.getExtensionsAspect(nsubjDep.dep);
                    ArrayList<IndexedWord> extensionsModifier = this.getExtensionsModifier(xcompDep.gov);

                    ExtractedAspectAndModifier tuple = new ExtractedAspectAndModifier(nsubjDep.dep, xcompDep.dep);
                    tuple.setExtensions(extensionsAspect, extensionsModifier);

                    tuple.setSentenceSentiment(sentenceSentiment);
                    
                    this.result.add(tuple);
                }
            }
        }
    }

    public void clear() {
        this.tmpNsubjDependencies.clear();
        this.tmpXcompDependencies.clear();
    }

}
