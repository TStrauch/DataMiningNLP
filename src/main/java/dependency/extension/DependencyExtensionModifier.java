package dependency.extension;

import edu.stanford.nlp.ling.IndexedWord;

/**
 * Created by Timo on 04.05.16.
 */
public interface DependencyExtensionModifier {
    public String getExtension(String word);
}

