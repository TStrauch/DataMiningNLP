import edu.cmu.lti.jawjaw.pobj.POS;
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.lexical_db.data.Concept;
import edu.cmu.lti.ws4j.Relatedness;
import edu.cmu.lti.ws4j.impl.JiangConrath;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;
import model.ExtractedAspectAndModifier;
import model.SimilarityPair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Timo on 07.05.16.
 */
public class SimilarityCalculator {
    private List<SimilarityPair> result = Collections.synchronizedList(new ArrayList<SimilarityPair>());
    private ArrayList<ExtractedAspectAndModifier> extractedAspects;
    private ILexicalDatabase db = new NictWordNet();
    private JiangConrath jcn = new JiangConrath(db);

    public static void main(String[] args){
        ArrayList<ExtractedAspectAndModifier> testset = new ArrayList<ExtractedAspectAndModifier>();

        ExtractedAspectAndModifier one = ExtractedAspectAndModifier.getMock("car");
        ExtractedAspectAndModifier two = ExtractedAspectAndModifier.getMock("bike");
        ExtractedAspectAndModifier three = ExtractedAspectAndModifier.getMock("train");

        testset.add(one);
        testset.add(two);
        testset.add(three);

        List<SimilarityPair> pipe = new SimilarityCalculator().pipe(testset);
        for (SimilarityPair p : pipe){
            System.out.println(p);
        }

    }

    public SimilarityCalculator(){
        WS4JConfiguration.getInstance().setMFS(true);
    }


    public List<SimilarityPair> pipe(ArrayList<ExtractedAspectAndModifier> input){
        ArrayList<SimilarityPair> result = new ArrayList<SimilarityPair>();

        ILexicalDatabase db = new NictWordNet();
        JiangConrath jcn = new JiangConrath(db);
        WS4JConfiguration.getInstance().setMFS(true);

        int iteration = 0;
        int n = input.size() - 1;
        int total = n*(n+1)/2;
        long startTime = System.currentTimeMillis();
        for (int f = 0; f < input.size()-1; f++){
            for (int s = f+1; s < input.size(); s++){

                if (s == f){
                    continue;
                }

                ExtractedAspectAndModifier first = input.get(f);
                ExtractedAspectAndModifier second = input.get(s);

                String firstAspect = first.aspectLemma;
                String secondAspect = second.aspectLemma;

                double similarity = calcJCNSimilarity(db, jcn, firstAspect, secondAspect);

                SimilarityPair sp = new SimilarityPair(first, second, similarity);
                result.add(sp);

                iteration++;
                System.out.print("\r[SimilarityCalculator] "+iteration/total+"% ("+iteration+" / "+total+"); " +
                        "time elapsed: "+(System.currentTimeMillis()-startTime)/1000/60+"min");
            }
        }

        return result;
    }

    public  List<SimilarityPair> pipeParallel(ArrayList<ExtractedAspectAndModifier> input){
        extractedAspects = input;

        this.runThreads();

        while(working){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    private static double calcJCNSimilarity(ILexicalDatabase db, JiangConrath jcn, String word1, String word2){
        List<Concept> synsets1 = (List<Concept>)db.getAllConcepts(word1, POS.n.toString());
        List<Concept> synsets2 = (List<Concept>)db.getAllConcepts(word2, POS.n.toString());
        double res = 0.0;
        try{
            res = jcn.calcRelatednessOfSynset(synsets1.get(0), synsets2.get(0)).getScore();
        } catch(Exception e){}
        return res;
//        double maxScoreJCN = -1D;
//        for(Concept synset1: synsets1) {
//            for (Concept synset2: synsets2) {
//                Relatedness relatednessJCN = jcn.calcRelatednessOfSynset(synset1, synset2);
//                double scoreJCN = relatednessJCN.getScore();
//                if (scoreJCN > maxScoreJCN) {
//                    maxScoreJCN = scoreJCN;
//                }
//            }
//        }
//        if (maxScoreJCN == -1D) {
//            maxScoreJCN = 0.0;
//        }
//
//        return maxScoreJCN;
    }

    private long startTime;
    private int total;

    public void runThreads(){
        startTime = System.currentTimeMillis();
        int n = extractedAspects.size() - 1;
        total = n*(n+1)/2;

        Runnable r = new Runnable() {
            public void run() {
                int index;
                while((index = getLastIndex()) != -1){
                    for (int s = index+1; s < extractedAspects.size(); s++){

                        ExtractedAspectAndModifier first = extractedAspects.get(index);
                        ExtractedAspectAndModifier second = extractedAspects.get(s);

                        String firstAspect = first.aspectLemma;
                        String secondAspect = second.aspectLemma;

                        double similarity = calcJCNSimilarity(db, jcn, firstAspect, secondAspect);

                        SimilarityPair sp = new SimilarityPair(first, second, similarity);
                        result.add(sp);
                    }
                }
            }
        };

        new Thread(r).start();
        new Thread(r).start();
        new Thread(r).start();
        new Thread(r).start();
        new Thread(r).start();

    }

    private int lastIndex = -1;
    private boolean working = true;
    private synchronized int getLastIndex(){
        if (lastIndex < extractedAspects.size() - 2){
            working = true;
            lastIndex++;
            System.out.print("\r[SimilarityCalculator] ("+lastIndex+" / "+(extractedAspects.size()-2)+"); " +
                    "time elapsed: "+(System.currentTimeMillis()-startTime)/1000/60+"min");
            return lastIndex;
        }
        if (working == true) {
            System.out.println("\n[SimilarityCalculator] done");
        }
        working = false;
        return -1;
    }
}

/**
 *ArrayList<SimilarityPair> result = new ArrayList<SimilarityPair>();

 ILexicalDatabase db = new NictWordNet();
 JiangConrath jcn = new JiangConrath(db);
 WS4JConfiguration.getInstance().setMFS(true);

 int iteration = 0;
 int n = input.size() - 1;
 int total = n*(n-1)/2;
 long startTime = System.currentTimeMillis();
 for (int f = 0; f < input.size()-1; f++){
 for (int s = f+1; s < input.size(); s++){

 if (s == f){
 continue;
 }

 ExtractedAspectAndModifier first = input.get(f);
 ExtractedAspectAndModifier second = input.get(s);

 String firstAspect = first.aspectLemma;
 String secondAspect = second.aspectLemma;

 double similarity = calcJCNSimilarity(db, jcn, firstAspect, secondAspect);

 SimilarityPair sp = new SimilarityPair(first, second, similarity);
 result.add(sp);

 iteration++;
 System.out.print("\r[SimilarityCalculator] "+iteration/total+"% ("+iteration+" / "+total+"); " +
 "time elapsed: "+(System.currentTimeMillis()-startTime)/1000/60+"min");
 }
 }
 */