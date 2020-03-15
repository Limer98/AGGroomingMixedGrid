package Topology;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * Created by 1 on 2016/4/15.
 */
//must extends defaultWeightedEdge
public class AccessEdge extends DefaultWeightedEdge{

    public Node getSource(){
        return (Node)super.getSource();
    }

    public Node getDest(){

        return (Node)getTarget();

    }

//    public AuNode getAuSource(){
//        return (AuNode)super.getSource();
//    }
//
//    public AuNode getAuDest(){
//
//        return (AuNode) getTarget();
//
//    }

    @Override
    protected double getWeight() {
        return super.getWeight();
    }
}
