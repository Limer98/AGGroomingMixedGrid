package Topology;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther：sherrystar
 * @package_name: Topology
 * @create_time: 2019/1/18
 * @describe:
 */

//for generate a weightedgraph加权图
public class Graph {

    public List<Node> vertex;
    public List<Link> edge;


    DefaultDirectedWeightedGraph<Node, AccessEdge> g;


    //generate a graph
    public Graph(List<Node> Nodes, List<Link> Links) {

        vertex = Nodes;

        edge = Links;


        g = new DefaultDirectedWeightedGraph<Node,AccessEdge>(AccessEdge.class);
        //set link and node
        for (int i = 0; i < edge.size(); i++) {
            Node src = vertex.get(edge.get(i).srcSeq-1);
            Node dst = vertex.get(edge.get(i).dstSeq-1);
            g.addVertex(src);
            g.addVertex(dst);
            //g.addEdge(src,dst) return AccessEdge;
            AccessEdge a = g.addEdge(src, dst);
            if(a!=null)
                g.setEdgeWeight(a,edge.get(i).weight);
        }
    }



//    public boolean isMatchVertexEdge() {
//        int x = 0;
//        int y = 0;
//        int z = 0;
//        for (int i = 0; i <edge.size(); i++) {
//            for (int j = 0; j <vertex.size(); j++) {
//                if (vertex.get(j).nodeID ==edge.get(i).srcSeq) {
//                    x = x + 1;
//                }
//                if (vertex.get(j).nodeID ==edge.get(i).dstSeq) {
//                    y = y + 1;
//                }
//            }
//            if (x == 1 && y == 1) {
//                z = z + 1;
//            } else return false;
//        }
//        if (z ==edge.size()) {
//            return true;
//        } else return false;
//    }

    //return g
    public DefaultDirectedWeightedGraph<Node, AccessEdge> getG() {
        return g;
    }
}
