package sentenceDependencies;

/**
 * Created by Timo on 04.05.16.
 */
public class ExtractedAspectAndModifier {
    public String aspect;
    public String modifier;

    public String toString(){
        return "("+modifier+", "+aspect+")";
    }
}
