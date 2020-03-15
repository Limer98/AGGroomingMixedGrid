package Topology;

import Common.CommonResource;
import Utils.ReadTopo;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @auther：sherrystar
 * @package_name: Topology
 * @create_time: 2019/1/18
 * @describe:
 */

public class TreeCalculate {

    //全局变量
    public List<Node> vertex;
    public List<Link> edge;
    public int serviceID;

    public TreeCalculate(List<Node> Nodes, List<Link> Links, int eventID){
        vertex = Nodes;
        edge = Links;
        serviceID = eventID;
    }

    //ggg must be new
    public Tree calculateTree(DefaultDirectedWeightedGraph ggg, int Source, List<Integer> Dest) {
       DefaultDirectedWeightedGraph<Node, AccessEdge> ChangedGraph = new Graph(vertex,edge).getG();
        Tree tr = new Tree();
/*        Path pa = new Path();

        for (int i = 0; i < Dest.size(); i++) {
            DijkstraShortestPath<Node, AccessEdge> dsp = new DijkstraShortestPath<Node, AccessEdge>(ChangedGraph,vertex.get(Source-1),vertex.get(Dest.get(i)-1));
            GraphPath<Node, AccessEdge> p = dsp.getPath();
            pa.nodes.add(p);
            List<AccessEdge> list = p.getEdgeList();
            for(AccessEdge accessEdge:list){
                if(ChangedGraph.containsEdge(vertex.get(Source-1),accessEdge.getDest()))
                {
                    ChangedGraph.removeAllEdges(vertex.get(Source-1),accessEdge.getDest());
                    ChangedGraph.setEdgeWeight(ChangedGraph.addEdge(vertex.get(Source-1),accessEdge.getDest()),0);

                }
                else{
                    ChangedGraph.setEdgeWeight(ChangedGraph.addEdge(vertex.get(Source-1),accessEdge.getDest()),0);
                }
            }
//            if(ChangedGraph.containsEdge(vertex.get(Source-1),vertex.get(Dest.get(i)-1)))
//            {
//                ChangedGraph.removeAllEdges(vertex.get(Source-1),vertex.get(Dest.get(i)-1));
//                ChangedGraph.setEdgeWeight(ChangedGraph.addEdge(vertex.get(Source-1),vertex.get(Dest.get(i)-1)),0);
//
//            }
//            else{
//                ChangedGraph.setEdgeWeight(ChangedGraph.addEdge(vertex.get(Source-1),vertex.get(Dest.get(i)-1)),0);
//            }


        }

        //到这里为止得到了pa，接下来要根据pa生成tr


        Set<AccessEdge>  edgeSet = new HashSet<AccessEdge>();

        for (int i = 0; i < pa.nodes.size(); i++) {
            List<AccessEdge> list = pa.nodes.get(i).getEdgeList();
            for (int j = 0; j < list.size(); j++) {
                if(list.get(j).getWeight()!=0)
                    edgeSet.add(list.get(j));
            }
        }




        TreeNode treeNode = new TreeNode(vertex.get(Source-1));
        genTree(treeNode,edgeSet);

        tr.resultTree = treeNode;*/
        return tr;
    }


    public void genTree(TreeNode treeNode,Set<AccessEdge> accessEdgeSet){


//        Set<AccessEdge> toRemove = new HashSet<AccessEdge>();
//        if(accessEdgeSet == null)
//            return;
        Node node = treeNode.value;

            for (AccessEdge accessEdge:accessEdgeSet) {
                if(accessEdge.getSource().nodeID == node.nodeID){
                    TreeNode t = new TreeNode(accessEdge.getDest());
//                t.parent = treeNode;
                    treeNode.nlist.add(t);
//                toRemove.add(accessEdge);

                }
            }


//        accessEdgeSet.removeAll(toRemove);

        if(treeNode.nlist==null)
            return;
        for(TreeNode treeNode1:treeNode.nlist){

            genTree(treeNode1,accessEdgeSet);
        }
    }


    public static void main(String[] args) {

        List<Node> nodeList =  new ArrayList<Node>();
        List<Link> linkList =  new ArrayList<Link>();
        ReadTopo topo = new ReadTopo(CommonResource.FILE_NAME);
        nodeList = topo.getNodeList();
        linkList = topo.getLinkList();
        DefaultDirectedWeightedGraph<Node, AccessEdge> graphG = new Graph(nodeList,linkList).getG();


        TreeCalculate treeCalculate = new TreeCalculate(nodeList,linkList,0);

        List<Integer> des = new ArrayList<Integer>();
        des.add(3);
        des.add(1);
        des.add(6);
        Tree tr = treeCalculate.calculateTree(graphG,11,des);
        System.out.println();

    }

}
