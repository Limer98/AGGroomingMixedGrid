package Topology;

import Common.CommonResource;
import Network.Network;
import Utils.ReadTopo;
import org.jgrapht.GraphPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class Path{
//    private static final long serialVersionUID = 1L;

    //这里面保存的是源节点到宿节点路径
//    List<GraphPath<Node, AccessEdge>> nodes = new ArrayList<GraphPath<Node, AccessEdge>>();     //链表，有下一跳
    public GraphPath<Node, AccessEdge> gPath;

    public List<AccessEdge> edgeList;
    public HashMap<Integer, Node> nodeList;

    public Path() {
        //none;
    }

    public Path(GraphPath<Node, AccessEdge> gPath){
        this.gPath = gPath;
    }

    public List<AccessEdge> getGPathEdgeList(){
        List<AccessEdge> edgeList = gPath.getEdgeList();
        return edgeList;
    }

    public void setGPathEdgeList(){
        this.edgeList = this.getGPathEdgeList();
    }
    public void setGPathNodeList(){
        this.nodeList = this.getGPathNodeList();
    }

    public HashMap<Integer, Node> getGPathNodeList(){ //nodeID, node
        HashMap<Integer, Node> nodeList1 = new LinkedHashMap<Integer, Node>();
        for (int i = 0; i < edgeList.size(); i++) {
            AccessEdge accessEdge = edgeList.get(i);
            nodeList1.put(accessEdge.getSource().nodeID,accessEdge.getSource());
        }
        AccessEdge accessEdge = edgeList.get(edgeList.size()-1);
        nodeList1.put(accessEdge.getDest().nodeID,accessEdge.getDest());
        return nodeList1;
    }

    public int[] getGPathNodeNum(){
        int[] NodeNumSet = new int[edgeList.size()+1];
//        HashMap<Integer, Node> GPathNodeList = this.getGPathNodeList();
        for(int i = 0; i < edgeList.size(); i++){
            AccessEdge accessEdge = edgeList.get(i);
            NodeNumSet[i]=accessEdge.getSource().nodeID;
        }
        AccessEdge accessEdge = edgeList.get(edgeList.size()-1);
        NodeNumSet[edgeList.size()] = accessEdge.getDest().nodeID;
        return NodeNumSet;
    }


    public void printPath(Path path){
        System.out.print("the route is: ");
        int[] NodeNumSet = path.getGPathNodeNum();
        for(int i = 0; i < NodeNumSet.length-1; i++){
            System.out.print(NodeNumSet[i] + "--");
        }
        System.out.println(NodeNumSet[NodeNumSet.length-1]);
        //test
/*        HashMap<Integer, Node> nodeList1 = this.getGPathNodeList();
        for(int i = 0; i <= nodeList1.size(); i++){
            System.out.println(nodeList1.get(i) + "--");
        }*/

    }

    public boolean goThroughFixedNode(){
        boolean goThroughFixedNode = false;
        for(Node node : nodeList.values()){
            goThroughFixedNode |= node.getIsFixedNode();
        }
        return goThroughFixedNode;
    }

    public boolean goThroughAllFixedNode(){
        boolean goThroughAllFixedNode = true;
        for (Node node : nodeList.values()){
            goThroughAllFixedNode &= node.getIsFixedNode();
        }
        return goThroughAllFixedNode;
    }

    //获得路径上遵从频谱一致性的频谱块
    public boolean[] getCommonSlots(){
        boolean[] slots = new boolean[CommonResource.numSlots];
//        boolean[] slots = this.get(0).getSlots();//第一条链路slots分布情况
//        for (Link link : this){
//            boolean[] currentLinkSlots = link.getSlots();
//            for (int i = 0; i < slots.length; i++){
//                slots[i] |= currentLinkSlots[i]; //或操作，获得路径的占用情况
//            }
//        }
        return slots; //初始值为false，取或后有一个slot值位true，则返回true
    }

}
