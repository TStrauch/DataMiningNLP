package dependency;

import dependency.extension.DependencyExtensionAspect;
import dependency.extension.DependencyExtensionModifier;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import model.GovernorDependent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timo on 04.05.16.
 */
public abstract class SemanticGraphEdgeEvaluator {
    public static ArrayList<DependencyExtensionAspect> dependencyExtensionAspects;
    public static ArrayList<DependencyExtensionModifier> dependencyExtensionModifiers;

    public abstract void evalSemanticGraphEdge(SemanticGraphEdge edge);

    public abstract void endOfSentence();

    public abstract void clear();



    protected boolean correctPOSTags(IndexedWord noun, IndexedWord adjAdv){
        return isNoun(noun) && (isAdjective(adjAdv) || isAdverb(adjAdv));
    }

    protected void saveTmpGovernorDependent(ArrayList<GovernorDependent> list, IndexedWord gov, IndexedWord dep){
        GovernorDependent tmpTupel = new GovernorDependent();
        tmpTupel.gov = gov;
        tmpTupel.dep = dep;
        list.add(tmpTupel);
    }

    protected String getExtensionsAspect(IndexedWord aspect){
        String aspectExtension = "";
        for (DependencyExtensionAspect extAspect: this.dependencyExtensionAspects){
            String tmpext = extAspect.getExtension(aspect.word());
            if (tmpext != null) {
                aspectExtension = extAspect.getExtension(aspect.word()) + " " + aspectExtension;
            }
        }
        return aspectExtension;
    }
    protected String getExtensionsModifier(IndexedWord modifier){
        String modifierExtension = "";
        for (DependencyExtensionModifier extModifier: this.dependencyExtensionModifiers){
            String tmpext = extModifier.getExtension(modifier.word());
            if (tmpext != null) {
                modifierExtension = extModifier.getExtension(modifier.word()) + " " + modifierExtension;
            }
        }
        return modifierExtension;
    }



    private boolean isNoun(IndexedWord noun){
        return noun.tag().toString().equals("NN")
                || noun.tag().toString().equals("NNS")
                || noun.tag().toString().equals("NNP")
                || noun.tag().toString().equals("NNPS");

    }

    private boolean isAdjective(IndexedWord adjective){
        return adjective.tag().toString().equals("JJ")
                || adjective.tag().toString().equals("JJR")
                || adjective.tag().toString().equals("JJS")
                || adjective.tag().toString().equals("JJ");
    }

    private boolean isAdverb(IndexedWord adverb){
        return adverb.tag().toString().equals("RB")
                || adverb.tag().toString().equals("RBR")
                || adverb.tag().toString().equals("RBS");
    }
}
