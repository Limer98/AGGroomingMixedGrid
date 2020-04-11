package AuxGraph;

import Topology.Link;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

import java.util.List;

public class AuGraph {
    public List<AuNode> vertex;
    public List<Link> edge;

    DefaultDirectedWeightedGraph<AuNode, AuAccessEdge> auxG;

    //generate an auxiliary graph
    public AuGraph(List<AuNode> auNodes,List<Link> links){
        vertex = auNodes;
        edge = links;
        auxG = new DefaultDirectedWeightedGraph<AuNode,AuAccessEdge>(AuAccessEdge.class);
        //set link and node
//        for (int i = 0; i < edge.size(); i++) {
//            AuNode src = vertex.get(edge.get(i).srcSeq-1);
//            AuNode dst = vertex.get(edge.get(i).dstSeq-1);
//            auxG.addVertex(src);
//            auxG.addVertex(dst);
//            //auxG.addEdge(src,dst) return AccessEdge;
//            AuAccessEdge a = auxG.addEdge(src, dst);
//            if(a!=null)
//                auxG.setEdgeWeight(a,edge.get(i).weight);
//        }
        for (Link edgei:edge) {
            AuNode src = vertex.get(edgei.srcSeq-1);
            AuNode dst = vertex.get(edgei.dstSeq-1);
            auxG.addVertex(src);
            auxG.addVertex(dst);
            //auxG.addEdge(src,dst) return AccessEdge;
            AuAccessEdge a = auxG.addEdge(src, dst);
            if(a!=null)
                auxG.setEdgeWeight(a,edgei.weight);
        }
    }

    //return auxG
    public DefaultDirectedWeightedGraph<AuNode,AuAccessEdge> getAuxG() {
        return auxG;
    }
}
