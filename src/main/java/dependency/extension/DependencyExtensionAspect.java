package dependency.extension;

import edu.stanford.nlp.ling.IndexedWord;

import java.util.ArrayList;

/**
 * Created by Timo on 04.05.16.
 */
public interface DependencyExtensionAspect {
    public ArrayList<IndexedWord> getExtension(String word);
}
