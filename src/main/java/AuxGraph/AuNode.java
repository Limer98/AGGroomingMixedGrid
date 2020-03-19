package AuxGraph;

import Topology.ResourceStatus;

public class AuNode {
    public int auNodeID; //location in the auNodeList
    public int nodeID;
    public boolean isFixed;
    public boolean isSub;
    public boolean isEG = false;
    public boolean isResEnough;
    public int NodeType;
    public ResourceStatus resourceStatus; // not listResourceStatus
//    public int numTransponder;//record the number of consumed transponder
//    public int subTransponder;//record the rest subTransponder

    //NodeType
    public static int TypeTx = 0;
    public static int TypeRx = 1;
    public static int TypeIPout = 2;
    public static int TypeIPin = 3;

    public AuNode(int nodeID,boolean isFixed){
        this.nodeID = nodeID;
        this.isFixed = isFixed;
    }

    public AuNode( AuNode auNode,int auNodeID,int NodeType){
        this.nodeID = auNode.nodeID;
        this.isFixed = auNode.isFixed;
        this.auNodeID = auNodeID;
        this.NodeType = NodeType;
    }
//    public AuNode( int auNodeID, int nodeID,boolean isFixed,int NodeType){
//        this.auNodeID = auNodeID;
//        this.nodeID = nodeID;
//        this.isFixed = isFixed;
//        this.NodeType = NodeType;
//    }

    public void setAuNodePara(int auNodeID,boolean flagSubFound,boolean flagResEnough,int NodeType){
        this.auNodeID = auNodeID;
        this.isSub = flagSubFound;
        this.isResEnough = flagResEnough;
        this.NodeType = NodeType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuNode auNode = (AuNode) o;

        return auNodeID == auNode.auNodeID;
    }

    @Override
    public int hashCode() {
        return auNodeID;
    }
}
