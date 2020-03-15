package Topology;

import Common.CommonResource;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther：sherrystar
 * @package_name: Topology
 * @create_time: 2019/1/17
 * @describe:
 */
public class Node {

    public int nodeID;
    public boolean isFixedNode;
    public List<ResourceStatus> resPerNode; //a ResourceStatue means a new transponder
//    public List<ResourceStatus> resPerNode1;

    public Node(int nodeID){
        this.nodeID = nodeID;
        isFixedNode = true;

        /******* new *******/

        resPerNode = new ArrayList<ResourceStatus>();
//        resPerNode1 = new ArrayList<ResourceStatus>();

        /******* --- *******/
    }

    public void initNodeResource(){

        /******* new *******/

        int numTrans;
        int numSubTrans;
        if (this.getIsFixedNode()){
            numTrans = CommonResource.numNSTransponderPerNode;
            numSubTrans = 1;
        }else{
            numTrans = CommonResource.numFSTransponderPerNode;
            numSubTrans = CommonResource.numSubTransponderPerT;
        }
        for (int i = 0; i < numTrans; i++){
//            ResourceStatus resourceStatus = new ResourceStatus();
//            resourceStatus.transID = i;
//            resourceStatus.TxResource = numSubTrans;
//            resourceStatus.RxResource = numSubTrans;
            ResourceStatus resourceStatus = new ResourceStatus(i,numSubTrans,numSubTrans);
            resPerNode.add(resourceStatus);
            //test: address of resourceStatus1 != address of resourceStatus
//            ResourceStatus resourceStatus1 = new ResourceStatus();
//            resourceStatus1.transID = i;
//            resourceStatus1.TxResource = numSubTrans;
//            resourceStatus1.RxResource = numSubTrans;
//            resPerNode1.add(resourceStatus1);
        }

        /******* --- *******/
    }

    public boolean getIsFixedNode(){ return isFixedNode; }
    public void setFixedNode(){ isFixedNode = true; }//设置固定节点为true.
    public void setFlexNode(){ isFixedNode = false; }//设置灵活节点为false


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        return nodeID == node.nodeID;
    }

    @Override
    public int hashCode() {
        return nodeID;
    }
}
