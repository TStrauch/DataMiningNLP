import cleansing.CleanReviews;
import dependency.SemanticGraphEdgeEvaluator;
import dependency.extension.*;
import dependency.parser.DepAdjectivalModifier;
import dependency.parser.DepAdverbialModifierPassiveVerb;
import dependency.parser.DepComplementCopularVerb;
import dependency.parser.DepDirectObjectAdjectivalComplement;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.util.CoreMap;
import model.ExtractedAspectAndModifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by Timo on 02.05.16.
 */
public class DependencyExtractor {

    public static String reviewPath = "data/Phoenix_business -1bOb2izeJBZjHC7NWxiPA.csv";

    public static void main (String[] args) throws IOException {
        DependencyExtractor d = new DependencyExtractor();
//        System.out.println(d.pipe(new ArrayList<ExtractedAspectAndModifier>()));
    }

    public ArrayList<ExtractedAspectAndModifier> pipe(ArrayList<ExtractedAspectAndModifier> input, String reviewText) throws IOException {
        /**
         * STANFORD CORE NLP pipeline
         */
        StanfordCoreNLP pipeline;



        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, depparse, lemma");
        pipeline = new StanfordCoreNLP(props);

        // read data
//        BufferedReader reader = new BufferedReader(new FileReader(new File(reviewPath)));
//        String text = "";
//        String line = reader.readLine();
//        while (line != null){
//            text = text.concat(line + "\n");
//            line = reader.readLine();
//        }

        //run some cleansing operations
//        text = CleanReviews.clean(text);

        // create an empty Annotation just with the given text
        Annotation document = new Annotation(reviewText);

        // run all Annotators on this text
        pipeline.annotate(document);

        // these are all the sentences in this document
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);


        //create all DependencyExtensions
        DepExtCompoundNoun depExtCompoundNoun = new DepExtCompoundNoun();
        DepExtAdverbialModifier depExtAdverbialModifier = new DepExtAdverbialModifier();
        DepExtSimpleNegation depExtSimpleNegation = new DepExtSimpleNegation();
        DepExtComplexNegation depExtComplexNegation = new DepExtComplexNegation();

        //create all SemanticGraphEdgeEvaluators
        ArrayList<SemanticGraphEdgeEvaluator> evaluators = new ArrayList<SemanticGraphEdgeEvaluator>();

        //add the extensions first to make sure they are ready when needed
        evaluators.add(depExtCompoundNoun);
        evaluators.add(depExtAdverbialModifier);
        evaluators.add(depExtSimpleNegation);
        evaluators.add(depExtComplexNegation);

        evaluators.add(new DepAdjectivalModifier(input));
        evaluators.add(new DepDirectObjectAdjectivalComplement(input));
        evaluators.add(new DepComplementCopularVerb(input));
        evaluators.add(new DepAdverbialModifierPassiveVerb(input));


        //create all DependencyExtensionsAspects
        ArrayList<DependencyExtensionAspect> extensionsAspect = new ArrayList<DependencyExtensionAspect>();
        extensionsAspect.add(depExtCompoundNoun);
        extensionsAspect.add(depExtSimpleNegation);

        //create all DependencyExtensionsModifier
        ArrayList<DependencyExtensionModifier> extensionsModifier = new ArrayList<DependencyExtensionModifier>();
        extensionsModifier.add(depExtAdverbialModifier);
        extensionsModifier.add(depExtSimpleNegation);
        extensionsModifier.add(depExtComplexNegation);

        //make the dependency extensions available to the dependency parsers
        SemanticGraphEdgeEvaluator.dependencyExtensionAspects = extensionsAspect;
        SemanticGraphEdgeEvaluator.dependencyExtensionModifiers = extensionsModifier;


        //now extract the syntactical dependencies
        for(CoreMap sentence: sentences) {

//            SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
//            SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);
            SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class);

            List<SemanticGraphEdge> edge_set1 = dependencies.edgeListSorted();
//
            for(SemanticGraphEdge edge : edge_set1) {
                for (SemanticGraphEdgeEvaluator evaluator: evaluators){
                    evaluator.evalSemanticGraphEdge(edge);
                }
            }
            for (SemanticGraphEdgeEvaluator evaluator: evaluators){
                evaluator.endOfSentence();
            }
            for (SemanticGraphEdgeEvaluator evaluator: evaluators){
                evaluator.clear();
            }
        }

        return input;

//        System.out.println("Extracted Aspect-Modifier pairs:"+ ExtractionResult.getResult());






        //now use ws4j to calculate a similarity value for each aspect-pair
        //ressources:
        // http://stackoverflow.com/questions/17166298/docs-for-java-ws4j-library


//        Properties propsLemm = new Properties();
//        propsLemm.put("annotators", "tokenize, ssplit, pos, lemma");
//        pipeline = new StanfordCoreNLP(propsLemm);
//        Annotation lemmaAnnotation = new Annotation(ExtractionResult.getAllAspectsString());
//        pipeline.annotate(new Annotation(lemmaAnnotation));
//
//        List<CoreMap> lemmas = lemmaAnnotation.get(CoreAnnotations.SentencesAnnotation.class);
//        for (CoreMap lemma: lemmas){
//            System.out.println(lemma);
//        }

    }

}
