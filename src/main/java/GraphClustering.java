
import com.google.common.base.Function;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.io.PajekNetWriter;
import model.AspectSimilarityDistanceModel;
import model.graph.AspectEdge;
import model.graph.AspectVertex;
import org.apache.commons.collections15.Factory;
import org.jgrapht.Graph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.PrimMinimumSpanningTree;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Timo on 19.05.16.
 */
public class GraphClustering {
    AspectSimilarityDistanceModel aspectSimDistModel;
    public SimpleWeightedGraph<AspectVertex, AspectEdge> graph = new SimpleWeightedGraph<AspectVertex, AspectEdge>(AspectEdge.class);
    public SimpleWeightedGraph<AspectVertex, AspectEdge> minimumSpanningTree = new SimpleWeightedGraph<AspectVertex, AspectEdge>(AspectEdge.class);

    public GraphClustering(AspectSimilarityDistanceModel input){
        this.aspectSimDistModel = input;
    }

    public Graph<AspectVertex, AspectEdge> createSampledGraph(){
        this.graph = new SimpleWeightedGraph<AspectVertex, AspectEdge>(AspectEdge.class);

        HashMap<Integer, String> aspects = new HashMap<Integer, String>();

        for (int i = 0; i < aspectSimDistModel.getAspects().keySet().size()/10; i++){
            aspects.put(i, aspectSimDistModel.getAspects().get(i));
        }

        return this._constructGraph(aspects);

    }
    public Graph<AspectVertex, AspectEdge> createGraph(){
        this.graph = new SimpleWeightedGraph<AspectVertex, AspectEdge>(AspectEdge.class);
        HashMap<Integer, String> aspects = aspectSimDistModel.getAspects();


        return this._constructGraph(aspects);
    }
    private Graph<AspectVertex, AspectEdge> _constructGraph(HashMap<Integer, String> aspects){

        HashMap<Integer, AspectVertex> idAspectMap = new HashMap<Integer, AspectVertex>();

        for (Integer id : aspects.keySet()) {
            String word = aspects.get(id);
            AspectVertex aspectVertex = new AspectVertex(word);
            idAspectMap.put(id, aspectVertex);
            this.graph.addVertex(aspectVertex);
        }

        double[][] distances = aspectSimDistModel.getDistances();

        for (Integer idVertex1 : idAspectMap.keySet()) {
            for (Integer idVertex2 : idAspectMap.keySet()) {

                if (idVertex1 == idVertex2){
                    continue;
                }

                AspectEdge aspectEdge = new AspectEdge(distances[idVertex1][idVertex2], idAspectMap.get(idVertex1), idAspectMap.get(idVertex2));
                this.graph.addEdge(idAspectMap.get(idVertex1), idAspectMap.get(idVertex2), aspectEdge);
                this.graph.setEdgeWeight(aspectEdge, aspectEdge.getWeight());
            }
        }

        return this.graph;
    }

    public void createMinimumSpanningTree(){
        PrimMinimumSpanningTree<AspectVertex, AspectEdge> spanningTree = new PrimMinimumSpanningTree<AspectVertex, AspectEdge>(this.graph);
        Set<AspectEdge> edgeSet = spanningTree.getMinimumSpanningTreeEdgeSet();

        this.minimumSpanningTree = new SimpleWeightedGraph<AspectVertex, AspectEdge>(AspectEdge.class);

        for (AspectEdge aspectEdge : edgeSet) {
            AspectVertex aspectVertex1 = aspectEdge.getAspectVertex1();
            AspectVertex aspectVertex2 = aspectEdge.getAspectVertex2();

            if (!this.minimumSpanningTree.containsVertex(aspectVertex1)){
                this.minimumSpanningTree.addVertex(aspectVertex1);
            }
            if (!this.minimumSpanningTree.containsVertex(aspectVertex2)){
                this.minimumSpanningTree.addVertex(aspectVertex2);
            }

            this.minimumSpanningTree.addEdge(aspectVertex1, aspectVertex2, aspectEdge);
            this.minimumSpanningTree.setEdgeWeight(aspectEdge, aspectEdge.getWeight());
        }


    }

    public void savePajekNetFile(String path) throws IOException {
//        Function<AspectVertex, String> mappingUser = new Function<AspectVertex, String>() {
//            public String apply(AspectVertex aspectVertex) {
//                return aspectVertex.getWord();
//            }
//        };
//
//        Function<AspectEdge, Number> mappingEdge = new Function<AspectEdge, Number>() {
//            public Number apply(AspectEdge aspectEdge) {
//                return aspectEdge.getWeight();
//            }
//        };
//
//
//        PajekNetWriter<AspectVertex, AspectEdge> pajekNetWriter = new PajekNetWriter<AspectVertex, AspectEdge>();
//        pajekNetWriter.save(this.graph, path, mappingUser, mappingEdge);
    }

    public void savePajekNetMinimumSpanningTree(String path) throws IOException {

        //convert the current spanning tree into the JUNG graph format to use the pajek export functions
        UndirectedSparseGraph<AspectVertex, AspectEdge> tmpGraph = new UndirectedSparseGraph<AspectVertex, AspectEdge>();

        for (AspectVertex aspectVertex : this.minimumSpanningTree.vertexSet()) {
            tmpGraph.addVertex(aspectVertex);
        }

        for (AspectEdge aspectEdge : this.minimumSpanningTree.edgeSet()) {
            tmpGraph.addEdge(aspectEdge, aspectEdge.getAspectVertex1(), aspectEdge.getAspectVertex2(), EdgeType.UNDIRECTED);
        }

        //now export it
        Function<AspectVertex, String> mappingUser = new Function<AspectVertex, String>() {
            public String apply(AspectVertex aspectVertex) {
                return aspectVertex.getWord();
            }
        };

        Function<AspectEdge, Number> mappingEdge = new Function<AspectEdge, Number>() {
            public Number apply(AspectEdge aspectEdge) {
                return aspectEdge.getWeight();
            }
        };


        PajekNetWriter<AspectVertex, AspectEdge> pajekNetWriter = new PajekNetWriter<AspectVertex, AspectEdge>();
        pajekNetWriter.save(tmpGraph, path, mappingUser, mappingEdge);

    }
}
