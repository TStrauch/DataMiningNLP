package sentenceDependencies;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.util.CoreMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by Timo on 02.05.16.
 */
public class DependencyExtractor {

    public static String reviewPath = "data/reviews.txt";

    public ArrayList<ExtractedAspectAndModifier> result = new ArrayList<ExtractedAspectAndModifier>();

    public static void main (String[] args) throws IOException {
        DependencyExtractor d = new DependencyExtractor();
        d.extract();
    }

    public void extract() throws IOException {
        /**
         * STANFORD CORE NLP pipeline
         */
        StanfordCoreNLP pipeline;



        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, depparse");
        pipeline = new StanfordCoreNLP(props);

        // read data
        BufferedReader reader = new BufferedReader(new FileReader(new File(reviewPath)));
        String text = "";
        String line = reader.readLine();
        while (line != null){
            text = text.concat(line + "\n");
            line = reader.readLine();
        }

        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);

        // run all Annotators on this text
        pipeline.annotate(document);

        // these are all the sentences in this document
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        //create all SemanticGraphEdgeEvaluators
        ArrayList<SemanticGraphEdgeEvaluator> evaluators = new ArrayList<SemanticGraphEdgeEvaluator>();
        evaluators.add(new DepAdjectivalModifier(result));
        evaluators.add(new DepDirectObjectAdjectivalComplement(result));

        for(CoreMap sentence: sentences) {

//            SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
//            SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);
            SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class);

            System.out.println("Sentence: "+sentence.toString());

            System.out.println("DEPENDENCIES: \n"+dependencies.toList());
//            System.out.println("DEPENDENCIES SIZE: "+dependencies.size());

            List<SemanticGraphEdge> edge_set1 = dependencies.edgeListSorted();
//
            for(SemanticGraphEdge edge : edge_set1) {
                for (SemanticGraphEdgeEvaluator evaluator: evaluators){
                    evaluator.evalSemanticGraphEdge(edge);
                }
            }
            for (SemanticGraphEdgeEvaluator evaluator: evaluators){
                evaluator.clear();
            }
        }

        System.out.println("Extracted Aspect-Modified pairs:"+ result);
    }

    public void depAdjectivalModifier(List<SemanticGraphEdge> edgeSet){

    }
}
