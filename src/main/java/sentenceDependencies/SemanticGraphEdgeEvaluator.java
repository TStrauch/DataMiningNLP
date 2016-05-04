package sentenceDependencies;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;

/**
 * Created by Timo on 04.05.16.
 */
public abstract class SemanticGraphEdgeEvaluator {
    public abstract void evalSemanticGraphEdge(SemanticGraphEdge edge);

    public abstract void clear();

    protected boolean correctPOSTags(IndexedWord gov, IndexedWord dep){
        return gov.tag().toString().equals("NN") && dep.tag().toString().equals("JJ");
    }
}
