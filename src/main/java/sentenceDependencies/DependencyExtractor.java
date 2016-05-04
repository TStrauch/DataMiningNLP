package sentenceDependencies;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Pair;
import edu.stanford.nlp.util.Triple;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Created by Timo on 02.05.16.
 */
public class DependencyExtractor {

    public static String reviewPath = "data/reviews.txt";

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

        for(CoreMap sentence: sentences) {

            SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
//            SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);
//            SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class);

            System.out.println("Sentence: "+sentence.toString());
            System.out.println("DEPENDENCIES: "+dependencies.toList());
            System.out.println("DEPENDENCIES SIZE: "+dependencies.size());

            List<SemanticGraphEdge> edge_set1 = dependencies.edgeListSorted();
            int j=0;

            //use the following code-snippet to extract the dependencies we are looking for (see paper)
            for(SemanticGraphEdge edge : edge_set1) {
                j++;
                System.out.println("------EDGE DEPENDENCY: " + j);
                Iterator<SemanticGraphEdge> it = edge_set1.iterator();
                IndexedWord dep = edge.getDependent();
                String dependent = dep.word();
                int dependent_index = dep.index();
                IndexedWord gov = edge.getGovernor();
                String governor = gov.word();
                int governor_index = gov.index();
                GrammaticalRelation relation = edge.getRelation();

                System.out.println("No:" + j + " Relation: " + relation.toString() + " Dependent ID: " + dependent_index + " Dependent: " + dependent.toString() + " Governor ID: " + governor_index + " Governor: " + governor.toString());
            }
        }
    }
}
