package cleansing;

/**
 * Created by Timo on 06.05.16.
 */
public class CleanReviews {
    private static final String regQuotationMarks = "\"";
    private static final String regNNs = "(?<![a-zA-Z])nn";
    private static final String repVerticalBar = "\\|";
    private static final String repEAcute = "xc3xa9"; //�
    private static final String repECircum = "xc3xaa"; //�
    private static final String repEGrave = "xc3xa8"; //�
    //private static final String repAAcute = ""; //�
    //private static final String repACircum = ""; //�
    private static final String repAGrave = "xc3xa0"; //�
    private static final String repUCircum = "xc3xbb"; //�
    private static final String repICircum = "xc3xae"; //�
    private static final String repCedille = "xc3xa7"; //�
    

    public static String clean(String reviews){
        String cleaned = reviews;
        cleaned = cleaned.replaceAll(regQuotationMarks, "");
        cleaned = cleaned.replaceAll(regNNs, "");
        cleaned = cleaned.replaceAll(repVerticalBar, " ");
        cleaned = cleaned.replaceAll(repEAcute, "�");
        cleaned = cleaned.replaceAll(repECircum, "�");
        cleaned = cleaned.replaceAll(repEGrave, "�");
        cleaned = cleaned.replaceAll(repAGrave, "�");
        cleaned = cleaned.replaceAll(repUCircum, "�");
        cleaned = cleaned.replaceAll(repICircum, "�");
        cleaned = cleaned.replaceAll(repCedille, "�");
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
