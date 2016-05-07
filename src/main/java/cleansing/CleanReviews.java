package cleansing;

/**
 * Created by Timo on 06.05.16.
 */
public class CleanReviews {
    private static final String regQuotationMarks = "\"";
    private static final String regNNs = "(?<![a-zA-Z])nn";

    public static String clean(String reviews){
        String cleaned = reviews;
        cleaned = cleaned.replaceAll(regQuotationMarks, "");
        cleaned = cleaned.replaceAll(regNNs, "");
        return cleaned;
    }

    public static void main(String[] args){
        String posSample = ")nnTheme";
        String negSample = "Dannys";


        System.out.println(clean(posSample));
        System.out.println(clean(negSample));

    }
}
