package AuxGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

public class AuAccessEdge extends DefaultWeightedEdge {
    public int physHops = 0;

    public AuNode getAuSource(){
        return (AuNode)super.getSource();
    }

    public AuNode getAuDest(){

        return (AuNode) getTarget();

    }
//    public List<AuAccessEdge> getAuAccessEdge(){
//        return (List<AuAccessEdge>)
//    }

    @Override
    public double getWeight() {
        return super.getWeight();
    }
}
