package AuxGraph;

import Topology.Link;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

import java.util.List;

public class AuxPathCalculate {
    //全局变量
    public List<AuNode> vertex;         //List名为vertex，它的内容是auNode类的几个属性,全局变量
    public List<Link> edge;
    public int serviceID;              //申请资源的业务ID



    //构造函数——初始化全局变量

    public AuxPathCalculate(List<AuNode> auNodes, List<Link> auLinks, int eventID){
        vertex = auNodes;
        edge = auLinks;
        serviceID = eventID;
    }

    //方法1：D算路,Path类型（后有class Path）,入参为图g/g1、源节点、目的节点；返回k条最短路径，形式List<GraphPath<Node, Node>>
    public AuxPath calculateAuxPath(DefaultDirectedWeightedGraph ggg, int Source, int Dest) {
        AuxPath pa = new AuxPath();
        DijkstraShortestPath<AuNode, AuAccessEdge> dsp = new DijkstraShortestPath<AuNode, AuAccessEdge>(ggg,vertex.get(Source-1),vertex.get(Dest-1)); //vertex数组是从0开始
        GraphPath<AuNode, AuAccessEdge> p = dsp.getPath();
//        pa.nodes.add(p); //计算出来的路径
        pa.auGPath = p;
        return pa;
    }
}
