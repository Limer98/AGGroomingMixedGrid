package AuxGraph;

import org.jgrapht.GraphPath;

import java.util.ArrayList;
import java.util.List;

public class AuxPath {
    public boolean containFixed = false;
    //这里面保存的是源节点到宿节点路径
    public GraphPath<AuNode, AuAccessEdge> auGPath;

    public List<AuAccessEdge> auEdgeList;
    public List<AuNode> auNodeList;
//    public HashMap<Integer, AuNode> nodeList;//auNodeID,AuNode

    public AuxPath() {
        //none;
    }

    public AuxPath(GraphPath<AuNode, AuAccessEdge> auGPath){
        this.auGPath = auGPath;
    }

    public List<AuAccessEdge> getAuGPathEdgeList(){
        return auEdgeList;
    }

    public void setAuGPathEdgeList(){
//        this.auEdgeList = new ArrayList<AccessEdge>();
//        Object a = auGPath.getEdgeList();
        this.auEdgeList = (List<AuAccessEdge>) auGPath.getEdgeList();
    }
    public List<AuNode> calAuGPathNodeList(){
        List<AuNode> auNodeList1 = new ArrayList<AuNode>();
        for (int i = 0; i < this.auEdgeList.size(); i++) {
            AuAccessEdge auAccessEdge = this.auEdgeList.get(i);
            auNodeList1.add(auAccessEdge.getAuSource());
        }
        AuAccessEdge auAccessEdge = this.auEdgeList.get(this.auEdgeList.size()-1);
        auNodeList1.add(auAccessEdge.getAuDest());
        return auNodeList1;
    }
    public void setAuGPathNodeList(){
        this.auNodeList = calAuGPathNodeList();
    }
    public List<AuNode> getAuGPathNodeList(){ //nodeID, node
        return this.auNodeList;
    }

/*    public HashMap<Integer, AuNode> getAuGPathNodeList(){ //nodeID, node
        HashMap<Integer, AuNode> nodeList1 = new LinkedHashMap<Integer, AuNode>();
        for (int i = 0; i < edgeList.size(); i++) {
            AccessEdge accessEdge = edgeList.get(i);
            nodeList1.put(accessEdge.getAuSource().auNodeID,accessEdge.getAuSource());
        }
        AccessEdge accessEdge = edgeList.get(edgeList.size()-1);
        nodeList1.put(accessEdge.getAuDest().auNodeID,accessEdge.getAuDest());
        return nodeList1;
    }*/
}
