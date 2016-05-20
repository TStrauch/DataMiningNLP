package model.graph;

import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * Created by Timo on 19.05.16.
 */
public class AspectEdge extends DefaultWeightedEdge {
    private double weight;
    private AspectVertex aspectVertex1;
    private AspectVertex aspectVertex2;

    public AspectEdge(double weight, AspectVertex aspectVertex1, AspectVertex aspectVertex2){
        this.weight = weight;
        this.aspectVertex1 = aspectVertex1;
        this.aspectVertex2 = aspectVertex2;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public AspectVertex getAspectVertex1() {
        return aspectVertex1;
    }

    public void setAspectVertex1(AspectVertex aspectVertex1) {
        this.aspectVertex1 = aspectVertex1;
    }

    public AspectVertex getAspectVertex2() {
        return aspectVertex2;
    }

    public void setAspectVertex2(AspectVertex aspectVertex2) {
        this.aspectVertex2 = aspectVertex2;
    }
}
