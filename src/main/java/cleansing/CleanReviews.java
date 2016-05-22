package cleansing;

/**
 * Created by Timo on 06.05.16.
 */
public class CleanReviews {
    private static final String regQuotationMarks = "\"";
    private static final String regNNs = "(?<![a-zA-Z])nn";
    private static final String repVerticalBar = "\\|";
    private static final String repEAcute = "xc3xa9"; //é
    private static final String repECircum = "xc3xaa"; //ê
    private static final String repEGrave = "xc3xa8"; //è
    //private static final String repAAcute = ""; //á
    //private static final String repACircum = ""; //â
    private static final String repAGrave = "xc3xa0"; //à
    private static final String repUCircum = "xc3xbb"; //û
    private static final String repICircum = "xc3xae"; //î
    private static final String repCedille = "xc3xa7"; //ç
    

    public static String clean(String reviews){
        String cleaned = reviews;
        cleaned = cleaned.replaceAll(regQuotationMarks, "");
        cleaned = cleaned.replaceAll(regNNs, "");
        cleaned = cleaned.replaceAll(repVerticalBar, " ");
        cleaned = cleaned.replaceAll(repEAcute, "é");
        cleaned = cleaned.replaceAll(repECircum, "ê");
        cleaned = cleaned.replaceAll(repEGrave, "è");
        cleaned = cleaned.replaceAll(repAGrave, "à");
        cleaned = cleaned.replaceAll(repUCircum, "û");
        cleaned = cleaned.replaceAll(repICircum, "î");
        cleaned = cleaned.replaceAll(repCedille, "ç");
        return cleaned;
    }

    public static void main(String[] args){
        String posSample = ")nnTheme";
        String negSample = "Dannys";
        String accentSample = "great.|nn \"In Montrxc3xa9al";


        System.out.println(clean(posSample));
        System.out.println(clean(negSample));
        System.out.println(clean(accentSample));

    }
}
