package Topology;

import AuxGraph.*;
import Common.CommonResource;
import Gen.CoSerEG;
import Gen.EventType;
import Gen.ServiceEvent;
import Utils.Pair;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @auther：sherrystar
 * @package_name: Topology
 * @create_time: 2019/1/19
 * @describe:
 */
public class EventDealer {
    private static final Logger log = LoggerFactory.getLogger(EventDealer.class);

    public List<ServiceEvent> serviceList;
    DefaultDirectedWeightedGraph<Node, AccessEdge> graphG;
    List<Node> nodeList;
    List<Link> linkList;
    //find services based on id
//    Map<Integer,List<ServiceEvent>> serviceMap = new HashMap<Integer,List<ServiceEvent>>(); //<id,List<ServiceEvent>>
    Map<Integer,ServiceEvent> serviceMap = new HashMap<Integer,ServiceEvent>();
//    Map<Integer,ServiceEvent> ongoingServiceMap = new HashMap<Integer,ServiceEvent>();

    public EventDealer(List<ServiceEvent> serviceList, DefaultDirectedWeightedGraph<Node, AccessEdge> graphG,List<Node> nodeList, List<Link> linkList) {
        this.serviceList = serviceList;
        this.graphG = graphG;
        this.nodeList = nodeList;
        this.linkList = linkList;
    }

    //keys should be updated when key-service or key-update service arrives

    public void doDeal(){
        for (int i = 0; i < serviceList.size(); i++) {
            int serviceID = serviceList.get(i).getEventId();
            int src = serviceList.get(i).getSrc();
            int dst = serviceList.get(i).getDst();
            int transRate = serviceList.get(i).getTransmissionRate();
//            Path pa;
            ServiceEvent s = serviceList.get(i);
            switch(s.getEventType()){
                case SERVICE_ARRIVAL:
                    //updateResource(s.getArriveTime());
                    operationsForSERVICE_ARRIVAL(s);
                    break;
                case SERVICE_END:
/*                    if (s.eventId == 0){
                        System.out.println(s.flagForSuccess);
                    }*/
                    operationsForSERVICE_END(s);
                    break;
            }
        }
    }



    public void operationsForSERVICE_ARRIVAL(ServiceEvent s){

//      updateResource(s.getArriveTime());
        PathCalculate pc = new PathCalculate(nodeList,linkList,s.getEventId());
        Path p = pc.calculatePath(graphG,s.getSrc(),s.getDst()); //D算路，存下
        p.setGPathEdgeList();
        p.setGPathNodeList();
//        HashMap<Integer,ResourceStatus> IdResourceLogger = new HashMap<Integer,ResourceStatus>();
        int[] GPathNodeNum = p.getGPathNodeNum();
        //测试
//        p.printPath(p);

        //if exists use the one before else new one
/*        if(!serviceMap.containsKey(s.getEventId())){
            serlist = new ServiceEvent();
        } else {
            serlist = serviceMap.get(s.getEventId());
        }*/
        s.ownPath=p; //ServiceEvent extends Event
        //测试
        System.out.println(s.getEventId()+" "+s.obtainHappenTime()+" "
                +s.getEventType().toString()+" "+s.getSrc()+"-"+s.getDst()+" TR:"+s.getTransmissionRate());

        //generate virtual graph for a serviceEvent
        AuxPath aP = calcuAuxPath(s);

        if (aP.auGPath!=null){//super channel (all flexible node)
            List<AuAccessEdge> auAccessEdgeList = aP.auGPath.getEdgeList();
            for (int i = 0; i < auAccessEdgeList.size(); i++){
                if (auAccessEdgeList.get(i).getAuSource().isFixed||auAccessEdgeList.get(i).getAuDest().isFixed){
                    aP.containFixed = true;
                    break;
                }
            }
            if ((aP.containFixed && (s.transmissionRate<=CommonResource.GbpsNSTransponder))
                    ||(!aP.containFixed && (s.transmissionRate<=CommonResource.GbpsFSTransponder))){
                //can allocate resource with one path
                s.flagForSuccess = updateResource_agSinglePath(aP,s);
            }else if (aP.containFixed){//try allocate resource with multi paths
                //divide s into several s
                s.flagForSuccess = updateResource_agMultiPath(s);
            }
//            else if (!aP.containFixed){
//                //divide s into several s
//            }
        }else {//try multi channels,each channel's capacity <= CommonResource.GbpsNSTransponder
            //hence, do not need to consider boolean containFixed
            if (s.transmissionRate>CommonResource.GbpsNSTransponder){
                s.flagForSuccess = updateResource_agMultiPath(s);
            }else{//fail
                s.flagForSuccess = false;
            }
        }

        if (s.getEventId() == 500){
            System.out.println("500");
        }
        //资源分配,资源不足时，返回false;run once;
//        s.flagForSuccess = updateResource_newLP(p,s);

        if (s.flagForSuccess){
            CommonResource.ongoingServiceMap.put(s.eventId,s);
        }
        serviceMap.put(s.getEventId(),s);
    }



    public void operationsForSERVICE_END(ServiceEvent s){

        //update resource status,update remaining capacity of transponders and IP ports
                //updateResource(s.getArriveTime());
        //资源释放
        //Find the arrived service with the same eventId.
        ServiceEvent sArrive;
        for (int i = 0; i < serviceList.size(); i++) {
            sArrive = serviceList.get(i);
            if ((sArrive.eventId == s.eventId) && (sArrive.getEventType() == EventType.SERVICE_ARRIVAL)){
                s.ownPath = sArrive.ownPath;
                s.splitNum = sArrive.splitNum;
                if (s.splitNum>0){
                   s.subSerList = sArrive.subSerList;
                   for (int j = 0; j < s.splitNum; j++){
                       s.subSerList.get(j).ownAuPath = sArrive.subSerList.get(j).ownAuPath;
                       s.subSerList.get(j).OEOLogger = sArrive.subSerList.get(j).OEOLogger;
//                       s.subSerList.get(j).coSerEGList = sArrive.subSerList.get(j).coSerEGList;
                   }
                }else {
                    s.ownAuPath = sArrive.ownAuPath;
                    s.OEOLogger = sArrive.OEOLogger;
//                    s.coSerEGList = sArrive.coSerEGList;
                }
                s.flagForSuccess = sArrive.flagForSuccess;
                break;
            }
        }

        if (s.eventId==0){
            System.out.println("00000");
        }

        if (s.flagForSuccess){
            releaseResource(s);
            CommonResource.ongoingServiceMap.remove(s.eventId);
            System.out.println(s.eventId+"--Success");
        }else{
            System.out.println(s.eventId+"--failure");
        }

    }

    //(establish a new lightpath)for transponder and IP port resources of nodes
/*    public boolean updateResource_newLP(Path p, ServiceEvent s){
        Node source = p.gPath.getStartVertex();
        Node dest = p.gPath.getEndVertex();

        boolean flagFound1 = false;
        int requestResNum1 = CommonResource.calcuRequestResNum(source.isFixedNode,s.transmissionRate);
        for (int i = 0; i < source.resPerNode.size(); i++){
            if (source.resPerNode.get(i).TxResource >= requestResNum1){
                flagFound1 = true;
                break;
            }
        }
        boolean flagFound2 = false;
        int requestResNum2 = CommonResource.calcuRequestResNum(dest.isFixedNode,s.transmissionRate);
        for (int i = 0; i < dest.resPerNode.size(); i++){
            if (dest.resPerNode.get(i).RxResource >= requestResNum2){
                flagFound2 = true;
                break;
            }
        }
        if (flagFound1 & flagFound2){ //check and then update
            int srcTranID=-1;
            for (int i = 0; i < source.resPerNode.size(); i++){
                if (source.resPerNode.get(i).TxResource >= requestResNum1){
                    source.resPerNode.get(i).TxResource -= requestResNum1;
                    HashMap<Integer,ConsumCapType> temp = source.resPerNode.get(i).ResLogger;
                    temp.put(s.eventId,new ConsumCapType(requestResNum1,s.transmissionRate,true));
                    srcTranID = i;
                    break;
                }
            }
            int dstTranID=-1;
            for (int i = 0; i < dest.resPerNode.size(); i++){
                if (dest.resPerNode.get(i).RxResource >= requestResNum2){
                    dest.resPerNode.get(i).RxResource -= requestResNum2;
                    HashMap<Integer,ConsumCapType> temp = dest.resPerNode.get(i).ResLogger;
                    temp.put(s.eventId,new ConsumCapType(requestResNum2,s.transmissionRate,false));
                    dstTranID = i;
                    break;
                }
            }
            s.pairTransID = new Pair<>(srcTranID,dstTranID);
            s.pairTransUsedNum = new Pair<>(requestResNum1,requestResNum2);
        }

        return flagFound1 & flagFound2;
    }*/

    public boolean updateResource_agSinglePath(AuxPath aP,ServiceEvent s){

        aP.setAuGPathEdgeList();
        aP.setAuGPathNodeList();
        s.OEOLogger = new ArrayList<AuNode>();
        if (aP.auNodeList!=null){
            for (int i = 0; i < aP.auNodeList.size(); i++){
                if (aP.auNodeList.get(i).isFixed){
                    aP.containFixed = true;
                    break;
                }
            }
        }
//        List<ConsumCapType> tempList = new ArrayList<ConsumCapType>();
        /******** Tx/Rx that needs to allocate transponder/IP resources ********/
        for (int i = 0; i < aP.auNodeList.size(); i++){
            AuNode tempNode = aP.auNodeList.get(i);//add to path and then allocate
            //optical grooming or new lightpath
            if (tempNode.NodeType == 0 && tempNode.isResEnough){//Tx auNode
                AuNode upNode = aP.auNodeList.get(i-1);
                if (upNode.NodeType == 2 && upNode.nodeID == tempNode.nodeID){//aP中Tx node的上一个节点为对应的IP port
                    Node source = nodeList.get(tempNode.nodeID-1);
                    int srcTranID = tempNode.resourceStatus.transID;
                    int requestResNum = CommonResource.calcuRequestResNum(source.isFixedNode,s.transmissionRate);
                    source.resPerNode.get(srcTranID).TxResource -= requestResNum;

                    if (source.resPerNode.get(srcTranID).TxResource<0){
                       System.out.println("TxResource<0");
                    }

                    HashMap<Integer,List<ConsumCapType>> tempMap = source.resPerNode.get(srcTranID).ResLogger;
                    if (tempMap.containsKey(s.eventId)){
                        tempMap.get(s.eventId).add(new ConsumCapType(requestResNum,s.transmissionRate,true,requestResNum));
                    }else {
                        List<ConsumCapType> tempList = new ArrayList<ConsumCapType>();
                        tempList.add(new ConsumCapType(requestResNum,s.transmissionRate,true,requestResNum));
                        tempMap.put(s.eventId,tempList);
                    }
                    s.OEOLogger.add(tempNode);
                }
            }
            if (tempNode.NodeType == 1 && tempNode.isResEnough){//Rx auNode
                AuNode downNode = aP.auNodeList.get(i+1);
                if (downNode.NodeType == 3 && downNode.nodeID == tempNode.nodeID){//aP中Tx node的下一个节点为对应的IP port
                    Node destination = nodeList.get(tempNode.nodeID-1);
                    int dstTranID = tempNode.resourceStatus.transID;
                    int requestResNum = CommonResource.calcuRequestResNum(destination.isFixedNode,s.transmissionRate);
                    destination.resPerNode.get(dstTranID).RxResource -= requestResNum;

                    if (destination.resPerNode.get(dstTranID).RxResource<0){
                        System.out.println("RxResource<0");
                    }

                    HashMap<Integer,List<ConsumCapType>> tempMap = destination.resPerNode.get(dstTranID).ResLogger;
                    if (tempMap.containsKey(s.eventId)){
                        tempMap.get(s.eventId).add(new ConsumCapType(requestResNum,s.transmissionRate,false,requestResNum));
                    }else {
                        List<ConsumCapType> tempList = new ArrayList<ConsumCapType>();
                        tempList.add(new ConsumCapType(requestResNum,s.transmissionRate,false,requestResNum));
                        tempMap.put(s.eventId,tempList);
                    }
                    s.OEOLogger.add(tempNode);
                }
            }
        }
        /******** existing lightpath that needs to allocate transponder/IP resources ********/
        for (int i = 0; i < aP.auEdgeList.size(); i++){
            AuAccessEdge auAccessEdge = aP.auEdgeList.get(i);
            if (auAccessEdge.getAuSource().NodeType == 2 && auAccessEdge.getAuDest().NodeType == 3
                    && auAccessEdge.getAuSource().nodeID!=auAccessEdge.getAuDest().nodeID){
                //find the existing lightpath
//                System.out.println("EG edge");
//                AuNode auSrcNode = auAccessEdge.getAuSource();
//                AuNode auDstNode = auAccessEdge.getAuDest();
                Link link = new Link(auAccessEdge.getAuSource().auNodeID,auAccessEdge.getAuDest().auNodeID,auAccessEdge.getWeight());
                if (CommonResource.virLinkList.contains(link)){
                    int index = CommonResource.virLinkList.indexOf(link);
                    List<ServiceEvent> serOnLink = CommonResource.virLinkList.get(index).serviceOnLink;
                    //first fit
                    AuNode auNodeSrc = auAccessEdge.getAuSource();
                    AuNode auNodeDst = auAccessEdge.getAuDest();

//                    serOnLink.get(0).coSerEGList.add(new CoSerEG(auAccessEdge.getAuSource().nodeID,
//                            auAccessEdge.getAuDest().nodeID,s.eventId,s.transmissionRate,s.obtainEndTime()));
//                    s.coSerEGList.add(new CoSerEG(auAccessEdge.getAuSource().nodeID,
//                            auAccessEdge.getAuDest().nodeID,serOnLink.get(0).eventId,serOnLink.get(0).transmissionRate,serOnLink.get(0).obtainEndTime()));

                    //source node
                    AuNode auTransNodeSrc = updateResEG(serOnLink.get(0),s,auNodeSrc,auAccessEdge,true);

                    //destination node
                    AuNode auTransNodeDst = updateResEG(serOnLink.get(0),s,auNodeDst,auAccessEdge,false);

                    s.OEOLogger.add(auTransNodeSrc);
                    s.OEOLogger.add(auTransNodeDst);
//                    System.out.println("aaa");
//                    s.OEOLogger.addAll(serOnLink.get(0).OEOLogger);
                }
            }
        }
        s.ownAuPath = aP;
        return true;
    }

    public AuNode updateResEG(ServiceEvent serOnLink0,ServiceEvent s,AuNode auNode,AuAccessEdge auAccessEdge,boolean isTx){

        AuNode auTransNode = new AuNode(auNode,auNode.auNodeID-2,auNode.NodeType-2);
        int Index = serOnLink0.OEOLogger.indexOf(auTransNode);
        if (Index == -1){
            System.out.println("Index == -1");
        }
        AuNode auNode0 = serOnLink0.OEOLogger.get(Index);
//        auTransNode.resourceStatus = new ResourceStatus(auNode0.resourceStatus);
        auTransNode.resourceStatus = auNode0.resourceStatus;
        int nodeTranID = auNode0.resourceStatus.transID;
        Node node = nodeList.get(auNode0.nodeID-1);
//        int requestResNum = CommonResource.calcuRequestResNum(node.isFixedNode,s.transmissionRate);
//                    source.resPerNode.get(srcTranID).TxResource -= requestResNum;
//        HashMap<Integer,List<ConsumCapType>> tempMap = node.resPerNode.get(nodeTranID).ResLogger;
        HashMap<Integer,List<ConsumCapType>> tempMap = auTransNode.resourceStatus.ResLogger;

        int serLoggerIndex = 0;
        List<ConsumCapType> consumCapTypeList0 = tempMap.get(serOnLink0.eventId);
        for (int i = 0; i < consumCapTypeList0.size(); i++){//判断条件待改，首次加clubEG号
//            if (consumCapTypeList0.size()>1){
//                System.out.println("ss");
//            }
            //出现consumCapTypeList0.size()>1，都是按100Gbps分割的，不管固定节点或灵活节点
            if ((consumCapTypeList0.get(i).consumCap+s.transmissionRate)<= CommonResource.GbpsNSTransponder
            && consumCapTypeList0.get(i).isTx == isTx){
                if (consumCapTypeList0.get(i).clubEG == -1){
                    consumCapTypeList0.get(i).clubEG = serOnLink0.eventId;
                    serLoggerIndex = i;
                }else {
//                    System.out.println("lastOne.clubEG!=-1");
                }
            }
        }
        int coReqResNum = consumCapTypeList0.get(serLoggerIndex).consumNums;
//        if(lastOne.clubEG==-1){
//            lastOne.clubEG = serOnLink0.eventId;
//        }else {
//            System.out.println("lastOne.clubEG!=-1");
//        }


        if (tempMap.containsKey(s.eventId)){

            ConsumCapType conCapType = new ConsumCapType(0,s.transmissionRate,isTx,coReqResNum);
            conCapType.coServiceEG = new HashMap<Integer, CoSerEG>();
            conCapType.coServiceEG.put(serOnLink0.eventId,new CoSerEG(auAccessEdge.getAuSource().nodeID,
                    auAccessEdge.getAuDest().nodeID,serOnLink0.eventId,serOnLink0.transmissionRate));
            if (conCapType.clubEG==-1){
                conCapType.clubEG = consumCapTypeList0.get(serLoggerIndex).clubEG;
            }


            tempMap.get(s.eventId).add(conCapType);
//                        tempMapSrc.get(s.eventId).add(new ConsumCapType(0,s.transmissionRate,true,requestResNumSrc));
        }else {
            List<ConsumCapType> tempList = new ArrayList<ConsumCapType>();

            ConsumCapType conCapType = new ConsumCapType(0,s.transmissionRate,isTx,coReqResNum);
            conCapType.coServiceEG = new HashMap<Integer, CoSerEG>();
            conCapType.coServiceEG.put(serOnLink0.eventId,new CoSerEG(auAccessEdge.getAuSource().nodeID,
                    auAccessEdge.getAuDest().nodeID,serOnLink0.eventId,serOnLink0.transmissionRate));
            if (conCapType.clubEG==-1){
                conCapType.clubEG = consumCapTypeList0.get(serLoggerIndex).clubEG;
            }

            tempList.add(conCapType);
            tempMap.put(s.eventId,tempList);
        }

//        auNode0.resourceStatus.ResLogger.get(serOnLink0.eventId);

        return auTransNode;
    }


    public boolean updateResource_agMultiPath(ServiceEvent s){//check before update resource
        boolean flagForSuccess = false;
        calcuAplitNum(s);

        for (int i = 0; i < s.splitNum; i++){
            AuxPath aPs = calcuAuxPath(s.subSerList.get(i));
            if (aPs.auGPath!=null){
                s.subSerList.get(i).ownAuPath = aPs;
                s.subSerList.get(i).flagForSuccess = updateResource_agSinglePath(s.subSerList.get(i).ownAuPath,s.subSerList.get(i));
            }else {
                s.subSerList.get(i).flagForSuccess = false;
            }
        }
        for (int i = 0; i < s.splitNum; i++){
            if (s.subSerList.get(i).flagForSuccess){
                flagForSuccess = true;
            }else {
                flagForSuccess = false;
            }
        }
        if (!flagForSuccess){
            releaseTempResource(s);
        }

//        for (int i = 0; i < s.splitNum; i++){
//            AuxPath aPs = calcuAuxPath(s.subSerList.get(i));
//            if (aPs.auGPath!=null){
//                s.subSerList.get(i).ownAuPath = aPs;
//                s.subSerList.get(i).flagForSuccess = true;
//            }else {
//                s.subSerList.get(i).flagForSuccess = false;
//            }
//        }

//        for (int i = 0; i < s.splitNum; i++){
//            if (s.subSerList.get(i).flagForSuccess){
//                flagForSuccess = true;
//            }else {
//                flagForSuccess = false;
//            }
//        }

//        if (flagForSuccess){
//            for (int i = 0; i < s.splitNum; i++){
//                updateResource_agSinglePath(s.subSerList.get(i).ownAuPath,s.subSerList.get(i));
//            }
//        }

        return flagForSuccess;
    }

    public boolean releaseResource(ServiceEvent s){

        /******* new *******/

//        Path p = s.ownPath;
//        Node srcNode = p.gPath.getStartVertex();
//        Node dstNode = p.gPath.getEndVertex();
//        releaseResPerNode(srcNode,s);
//        releaseResPerNode(dstNode,s);
        /******* --- *******/

        if (s.splitNum==0){
            for (int i = 0; i < s.OEOLogger.size(); i++){
                Node node = nodeList.get(s.OEOLogger.get(i).nodeID-1);
                int transID = s.OEOLogger.get(i).resourceStatus.transID;
                releaseResPerTransNode(node,s,transID);
//                releaseResPerNode(node,s);
            }
        }else {
            for (int i = 0; i < s.splitNum; i++){
                ServiceEvent ss = s.subSerList.get(i);
                for (int j = 0; j < ss.OEOLogger.size(); j++){
                    Node node = nodeList.get(ss.OEOLogger.get(j).nodeID-1);
                    int stransID = ss.OEOLogger.get(j).resourceStatus.transID;
                    releaseResPerTransNode(node,ss,stransID);
//                    releaseResPerNode(node,ss);
                }
            }
        }

        return true;
    }

    public boolean releaseTempResource(ServiceEvent s){//for assign multi lightpath failure

        if (s.splitNum==0){
            for (int i = 0; i < s.OEOLogger.size(); i++){
                Node node = nodeList.get(s.OEOLogger.get(i).nodeID-1);
                int transID = s.OEOLogger.get(i).resourceStatus.transID;
                releaseResPerTransNode(node,s,transID);
//                releaseResPerNode(node,s);
            }
        }else {
            for (int i = 0; i < s.splitNum; i++){
                ServiceEvent ss = s.subSerList.get(i);
                //main difference
                if (ss.OEOLogger==null){
                    continue;
                }
                for (int j = 0; j < ss.OEOLogger.size(); j++){
                    Node node = nodeList.get(ss.OEOLogger.get(j).nodeID-1);
                    int stransID = ss.OEOLogger.get(j).resourceStatus.transID;
                    releaseResPerTransNode(node,ss,stransID);
//                    releaseResPerNode(node,ss);
                }
            }
        }

        return true;
    }

    public void releaseResPerNode(Node node,ServiceEvent s){
        for (int i = 0; i < node.resPerNode.size(); i++){
            if (node.resPerNode.get(i).ResLogger.containsKey(s.eventId)){
                List<ConsumCapType> capTypeList = node.resPerNode.get(i).ResLogger.get(s.eventId);
                for (int j = 0; j < capTypeList.size(); j++){
                    ConsumCapType capType = capTypeList.get(j);
                    int cap = capType.consumNums;
                    if (capType.isTx){
                        node.resPerNode.get(i).TxResource += cap;
                    }else {
                        node.resPerNode.get(i).RxResource += cap;
                    }
                    node.resPerNode.get(i).ResLogger.remove(s.eventId);
                }
//                break;//if find a logger satisfying the requirements, break;
            }
        }
    }
    public void releaseResPerTransNode(Node node,ServiceEvent s,int transID) {

        List<ConsumCapType> capTypeList = node.resPerNode.get(transID).ResLogger.get(s.eventId);


        for (int j = 0; j < capTypeList.size(); j++) {
            ConsumCapType capType = capTypeList.get(j);
            /** new electrical grooming **/
            boolean flagCoSerExist = false;
//
//            if ()
//
//            if (s.coSerEGList != null) {
//                for (int q = 0; q < s.coSerEGList.size(); q++) {
//                    if (s.obtainEndTime() < s.coSerEGList.get(q).departTime) {
//                        flagCoSerExist = true;
//                    }
//                }
//                if (flagCoSerExist) {
////                    int cap = capType.consumNums;
////                    if (capType.isTx){
////                        node.resPerNode.get(transID).TxResource += 0;
////                    }else {
////                        node.resPerNode.get(transID).RxResource += 0;
////                    }
////                    node.resPerNode.get(transID).ResLogger.get(s.eventId).remove(capType);
//                } else {
//                    int cap = capType.consumNums;
////                    int capEG;
////                    if (cap == 0){
////                        capEG = capType.consumNumsEG;
////                    }else{
////                        capEG = capType.consumNums;
////                    }
//                    int capEG = capType.consumNumsEG;
//                    if (capType.isTx) {
//                        node.resPerNode.get(transID).TxResource += capEG;
//                    } else {
//                        node.resPerNode.get(transID).RxResource += capEG;
//                    }
//                    if (node.resPerNode.get(transID).TxResource > 8 || node.resPerNode.get(transID).RxResource > 8) {
//                        System.out.println("Resource>8");
//                    }
//                }
//            }
//            node.resPerNode.get(transID).ResLogger.get(s.eventId).remove(capType);

            /** --- **/

//            int cap = capType.consumNums;
//            if (capType.isTx){
//                node.resPerNode.get(transID).TxResource += cap;
//            }else {
//                node.resPerNode.get(transID).RxResource += cap;
//            }
//            node.resPerNode.get(transID).ResLogger.get(s.eventId).remove(capType);
//        }
            if (node.resPerNode.get(transID).ResLogger.get(s.eventId).size() == 0) {
                node.resPerNode.get(transID).ResLogger.remove(s.eventId);
            }
        }
    }
/*    public boolean checkElecGrooming(Node node,ServiceEvent s,ServiceEvent currentSer){
        for (int i = 0; i < node.resPerNode.size(); i++){
            if (node.resPerNode.get(i).ResLogger.containsKey(s.eventId)){
                ConsumCapType capType = node.resPerNode.get(i).ResLogger.get(s.eventId);
                int cap = capType.consumNums;
                if (capType.isTx){
                    return node.resPerNode.get(i).TxResource;
                }else {
                    return node.resPerNode.get(i).RxResource;
                }
            }
        }
    }*/

    public List<AuNode> genAuNodeList(List<Node> nodeList,ServiceEvent s){
        List<AuNode> auNodeList = new ArrayList<AuNode>();
        int auNodeID = 0;
        for (Node node:nodeList){ //per node
            int requestResNum = CommonResource.calcuRequestResNum(node.isFixedNode,s.transmissionRate);
            int givenResNum;
            if (!node.isFixedNode){
                givenResNum = CommonResource.numSubTransponderPerT;
            }else{
                givenResNum = 1;
            }
            //nodeID:1 2 3 4 5  6  7  8 9  10 11 12 13 14
            //Tx:    1 5 9
            //Rx:    2 6 10
            //IPout: 3 7 11
            //IPin:  4 8 12

            //Tx=4*nodeID-3
            AuNode auNode0 = setAuTRNodePerService(node,requestResNum,givenResNum,auNodeID,true);
            ++ auNodeID;
            auNodeList.add(auNode0);

            //Rx=4*nodeID-2
            AuNode auNode1 = setAuTRNodePerService(node,requestResNum,givenResNum,auNodeID,false);
            ++ auNodeID;
            auNodeList.add(auNode1);

            //IP port out=4*nodeID-1
            AuNode auNode2 = setAuIPNodePerService(node,auNodeID,true);
            ++ auNodeID;
            auNodeList.add(auNode2);

            //IP port in=4*nodeID
            AuNode auNode3 = setAuIPNodePerService(node,auNodeID,false);
            ++ auNodeID;
            auNodeList.add(auNode3);
        }

        return auNodeList;
    }

    public void calcuAplitNum(ServiceEvent s){
        s.splitNum = (int)Math.ceil((double)s.transmissionRate/CommonResource.GbpsNSTransponder);
        s.subSerList = new ArrayList<ServiceEvent>();
        for (int j = 0; j < s.splitNum-1; j++){
            s.subSerList.add(new ServiceEvent(s,CommonResource.GbpsNSTransponder));
        }
        s.subSerList.add(new ServiceEvent(s,s.transmissionRate-CommonResource.GbpsNSTransponder*(s.splitNum-1)));
//        System.out.println("split service: "+s.splitNum);
    }

    public int calAuNodeID(int nodeID,int type){
        int auNodeID;
        switch (type){
            case 0: auNodeID = 4*nodeID-3;break;//Tx
            case 1: auNodeID = 4*nodeID-2;break;//Rx
            case 2: auNodeID = 4*nodeID-1;break;//IP out
            case 3: auNodeID = 4*nodeID;break;//IP in
            default: auNodeID = -1;
        }
        return auNodeID;
    }

    public AuxPath calcuAuxPath(ServiceEvent s){
        //List<Link> vLinkList = genVirLinkList(CommonResource.ongoingServiceMap);
        //remove the same ones
        List<Link> auIPLinkList = genVirLinkList_noSame(CommonResource.ongoingServiceMap,s);
        //add to the commonResource
        CommonResource.virLinkList = auIPLinkList;
        //generate auxiliary graph for a serviceEvent
        List<AuNode> auNodeList = genAuNodeList(nodeList,s);//consider the required transponder resources
        List<Link> auLinkList = genAuLinkList(nodeList,auNodeList,linkList,auIPLinkList,s);
        DefaultDirectedWeightedGraph<AuNode, AuAccessEdge> auxG = new AuGraph(auNodeList,auLinkList).getAuxG();
        AuxPathCalculate aPC = new AuxPathCalculate(auNodeList,auLinkList,s.getEventId());
        AuxPath aP = aPC.calculateAuxPath(auxG,calAuNodeID(s.getSrc(),2),calAuNodeID(s.getDst(),3));//IPout-->IPin
        return aP;
    }

    public List<Link> genAuLinkList(List<Node> nodeList,List<AuNode> auNodeList,List<Link> linkList,List<Link> auIPLinkList,ServiceEvent s){
        //判断是否充足的slots资源，否的话，删去对应链路(本程序中认为slot资源充足)
        List<Link> auLinkList = new ArrayList<Link>();
        for (int i = 0; i < linkList.size(); i++){
            int src = linkList.get(i).srcSeq; //nodeID
            int dst = linkList.get(i).dstSeq; //nodeID
            //Tx1-->Rx2,potential lightpaths
            Link auLinkT1toR2 = new Link(calAuNodeID(src,0),calAuNodeID(dst,1),0.001);
            auLinkList.add(auLinkT1toR2);
        }
        for (int i = 0; i < nodeList.size(); i++){
            int nodeID = nodeList.get(i).nodeID;
            //Rx1-->Tx1
            Link auLinkR1toT1 = new Link(calAuNodeID(nodeID,1),calAuNodeID(nodeID,0),0.0001);
            auLinkList.add(auLinkR1toT1);
            //In1-->Out1
            Link auLinkI1toO1 = new Link(calAuNodeID(nodeID,3),calAuNodeID(nodeID,2),0.0001);
            auLinkList.add(auLinkI1toO1);
            //Out1-->Tx1
            AuNode tempTx = auNodeList.get(calAuNodeID(nodeID,0)-1);
            if (tempTx.isResEnough){
                double weightO1toT1 = setWeightofOTRI(tempTx,s.transmissionRate);
                Link auLinkO1toT1 = new Link(calAuNodeID(nodeID,2),calAuNodeID(nodeID,0),weightO1toT1);//weight not designed
                auLinkList.add(auLinkO1toT1);
            }
            //Rx1-->In1
            AuNode tempRx = auNodeList.get(calAuNodeID(nodeID,1)-1);
            if (tempRx.isResEnough){
                double weightR1toI1 = setWeightofOTRI(tempRx,s.transmissionRate);
                Link auLinkR1toI1 = new Link(calAuNodeID(nodeID,1),calAuNodeID(nodeID,3),weightR1toI1);//weight not designed
                auLinkList.add(auLinkR1toI1);
            }
        }
        auLinkList.addAll(auIPLinkList);
//        for (int i = 0; i < auIPLinkList.size(); i++){//Existing lightpaths
//            auLinkList.add(auIPLinkList.get(i));
//        }

        return auLinkList;
    }

    public double setWeightofOTRI(AuNode auNode,int transmissionRate){
        double weightO1toT1; //the same as weightR1toI1
        if (auNode.isFixed){
            weightO1toT1 = 185.5;
        }else {
            if (auNode.isSub){
                weightO1toT1 = 0.8415*transmissionRate;
            }else {
                weightO1toT1 = 0.8415*transmissionRate+45.6665;
            }
        }
        return weightO1toT1;
    }

    public List<Link> genVirLinkList(Map<Integer,ServiceEvent> ongoingServiceMap){
        List<Link> vLinkList = new ArrayList<Link>();
        double weight = 1;

        if (ongoingServiceMap != null) {
            Iterator iterSer = ongoingServiceMap.entrySet().iterator();
            while (iterSer.hasNext()){
                HashMap.Entry entry = (HashMap.Entry) iterSer.next();
//                Object serID = entry.getKey();
//                int sID = (Integer)serID;
                Object ser = entry.getValue();
                ServiceEvent s = (ServiceEvent)ser;
                Link link = new Link(s.getSrc(), s.getDst(), weight);
                link.serviceOnLink.add(s);
                vLinkList.add(link);
            }
        }
//        Iterator iterSer = ongoingServiceMap.entrySet().iterator();
//        while (iterSer.hasNext()){
//            HashMap.Entry entry = (HashMap.Entry) iterSer.next();
////            Object serID = entry.getKey();
//            Object ser = entry.getValue();
////            int sID = (Integer)serID;
//            ServiceEvent s = (ServiceEvent)ser;
//            int src = s.getSrc();
//            int dst = s.getDst();
//
//            Node nodeSrc = nodeList.get(src-1);
//            Node nodeDst = nodeList.get(dst-1);
//
//            if (nodeList.get(src-1).isFixedNode && nodeList.get(dst-1).isFixedNode){
//                weight = 0.001;
//            }else if ((!nodeList.get(src-1).isFixedNode && nodeList.get(dst-1).isFixedNode)
//                    || (nodeList.get(src-1).isFixedNode && !nodeList.get(dst-1).isFixedNode)){
//                weight =
//            }
//            Link link = new Link(s.getSrc(),s.getDst(),weight);
//            vLinkList.add(link);
//        }

        return vLinkList;
    }

    public List<Link> genVirLinkList_noSame(Map<Integer,ServiceEvent> ongoingServiceMap,ServiceEvent currentSer){
        List<Link> vLinkList = new ArrayList<Link>();
//        double weight = 1;

        if (ongoingServiceMap != null) {
            Iterator iterSer = ongoingServiceMap.entrySet().iterator();
            while (iterSer.hasNext()){
                HashMap.Entry entry = (HashMap.Entry) iterSer.next();
//                Object serID = entry.getKey();
//                int sID = (Integer)serID;
                Object ser = entry.getValue();
                ServiceEvent s = (ServiceEvent)ser;//in ongoingServiceMap

                if (s.OEOLogger==null){
                    for (int splitID = 0; splitID < s.splitNum; splitID++){
                        genVirLinkList_splitSer(splitID,vLinkList,s.subSerList.get(splitID),currentSer);
                    }
                }else{
                    genVirLinkList_splitSer(0,vLinkList,s,currentSer);
                }
            }
        }
        return vLinkList;
    }

    public int calcuTotalTRforEG(ServiceEvent serInMap,AuNode auNode,boolean isTx){
        int totalTR = 0;
        List<ConsumCapType> consumCapTypeList = auNode.resourceStatus.ResLogger.get(serInMap.eventId);
        int clubEG = -1;
        for (int i = 0; i < consumCapTypeList.size(); i++){//判断条件待改
            if (consumCapTypeList.get(i).isTx == isTx && consumCapTypeList.get(i).clubEG!=-1){
                clubEG = consumCapTypeList.get(i).clubEG;
//                totalTR += consumCapTypeList.get(i)
            }
        }
        if (clubEG == -1){
            totalTR = serInMap.transmissionRate;
        }else {
            Iterator iter = auNode.resourceStatus.ResLogger.entrySet().iterator();
            while (iter.hasNext()){
                HashMap.Entry entry = (HashMap.Entry) iter.next();
//                    Object numConsumTransponder = entry.getKey();
                Object capTypeList = entry.getValue();
                List<ConsumCapType> tempCapTypeList = (List<ConsumCapType>)capTypeList;
                for (int j = 0; j < tempCapTypeList.size(); j++){
                    if (tempCapTypeList.get(j).clubEG == clubEG
                            && tempCapTypeList.get(j).isTx == isTx){
                        totalTR += tempCapTypeList.get(j).consumCap;
                    }
                }
            }
        }
        return totalTR;
    }

    public void genVirLinkList_splitSer(int splitID,List<Link> vLinkList,ServiceEvent serInMap,ServiceEvent currentSer){
        boolean flagSrc = false;
        boolean flagDst = false;
        if (serInMap.OEOLogger == null){
            System.out.println("serInMap.OEOLogger == null");
        }
        /*** new ***/
        int totalTR = serInMap.transmissionRate;
//        for (int t = 0; t < serInMap.coSerEGList.size(); t++){
//            if (currentSer.obtainHappenTime()<serInMap.coSerEGList.get(t).departTime){
//                totalTR += serInMap.coSerEGList.get(t).transmission;
//            }
//        }
        /*** --- ***/
        for (int i = 0; i < serInMap.OEOLogger.size(); i++){
//            if (serInMap.OEOLogger.size() == 1){
//                System.out.println(serInMap.eventId+"--serInMap.OEOLogger.size() == 1");
//            }
            AuNode tempAuNode = serInMap.OEOLogger.get(i);
//          int nodeTransID = tempAuNode.resourceStatus.transID;
            double nodeResSlot = CommonResource.calcuResourceSlot(tempAuNode.isFixed);
            List<ConsumCapType> tempList= tempAuNode.resourceStatus.ResLogger.get(serInMap.eventId);
            int tempListID = 0;
            if (tempList == null){
                System.out.println("tempList == null");
            }
            if (tempList.size()>1){
                tempListID = splitID;
            }
            if (tempAuNode.NodeType == 0){//Tx
//                double nodeEGCap = (tempAuNode.resourceStatus.TxResource
//                        +tempAuNode.resourceStatus.ResLogger.get(serInMap.eventId).get(tempListID).consumNums)*nodeResSlot;
                double nodeEGCap = tempAuNode.resourceStatus.ResLogger.get(serInMap.eventId).get(tempListID).consumNumsEG*nodeResSlot;
//                if (nodeEGCap == 0){
//                    System.out.println("nodeEGCap == 0");
//                }
                totalTR = calcuTotalTRforEG(serInMap,tempAuNode,true);
//                if (nodeEGCap>= (serInMap.transmissionRate+currentSer.transmissionRate)){
                if (nodeEGCap>= (totalTR+currentSer.transmissionRate)){
                    //satisfy electrical grooming constraint
                    flagSrc = true;
                }
            }
            if (tempAuNode.NodeType == 1){//Rx
//                double nodeEGCap = (tempAuNode.resourceStatus.RxResource
//                        +tempAuNode.resourceStatus.ResLogger.get(serInMap.eventId).get(tempListID).consumNums)*nodeResSlot;
                double nodeEGCap = tempAuNode.resourceStatus.ResLogger.get(serInMap.eventId).get(tempListID).consumNumsEG*nodeResSlot;
                totalTR = calcuTotalTRforEG(serInMap,tempAuNode,false);
//                if (nodeEGCap>= (serInMap.transmissionRate+currentSer.transmissionRate)){
                if (nodeEGCap>= (totalTR+currentSer.transmissionRate)){
                    //satisfy electrical grooming constraint
                    flagDst = true;
                }
            }
            if (flagSrc&flagDst){//此时tempAuNode是dstNode
                flagSrc = false;
                flagDst = false;
                AuNode srcNode = serInMap.OEOLogger.get(i-1);
                //set weight
                double weight;
                if (srcNode.isFixed & tempAuNode.isFixed){//both fixed node
                    weight = 0.001;
                }else if (!srcNode.isFixed & !tempAuNode.isFixed){//both flexible node
                    weight = 1.683*currentSer.transmissionRate;
                }else {//a fixed and a flexible node
                    weight = 0.8415*currentSer.transmissionRate;
                }
                //add link
//                Link link = new Link(serInMap.getSrc(), serInMap.getDst(), weight);
//                Link link = new Link(srcNode.nodeID, tempAuNode.nodeID, weight);//in physical graph
                Link link = new Link(calAuNodeID(srcNode.nodeID,2), calAuNodeID(tempAuNode.nodeID,3), weight);//in auxiliary graph
//                link.containFixed = serInMap.ownAuPath.containFixed;
                if (vLinkList.contains(link)){
                    int index = vLinkList.indexOf(link);
                    vLinkList.get(index).serviceOnLink.add(serInMap);
                }else {
                    link.serviceOnLink.add(serInMap);
                    vLinkList.add(link);
                }
            }
        }
    }

    public AuNode setAuTRNodePerService(Node node,int requestResNum,int givenResNum,int auNodeID,boolean isTx){
        AuNode auNode = new AuNode(node.nodeID,node.isFixedNode);
        boolean flagSubFound = false;
        boolean flagResEnough = false;
        for (int i = 0; i < node.resPerNode.size(); i++){
            int availNum;
            if (isTx){
                availNum = node.resPerNode.get(i).TxResource;
            }else {
                availNum = node.resPerNode.get(i).RxResource;
            }
            if ((availNum >= requestResNum) && (availNum < givenResNum)){
                flagSubFound = true;
                flagResEnough = true;
                auNode.resourceStatus = node.resPerNode.get(i);//node.resPerNode.get(i)值发生变化，auNode.resourceStatus也变化（相同地址，引用关系）
                break;
            }
        }
        if (!flagSubFound){
            for (int i = 0; i < node.resPerNode.size(); i++){
                int availNum;
                if (isTx){
                    availNum = node.resPerNode.get(i).TxResource;
                }else {
                    availNum = node.resPerNode.get(i).RxResource;
                }
                if ((availNum >= requestResNum) && (availNum == givenResNum)){
                    flagResEnough = true;
                    auNode.resourceStatus = node.resPerNode.get(i);
                    break;
                }
            }
        }
        ++ auNodeID;
        int NodeType;
        if (isTx){
            NodeType = AuNode.TypeTx;
        }else {
            NodeType = AuNode.TypeRx;
        }
        auNode.setAuNodePara(auNodeID,flagSubFound,flagResEnough,NodeType);
        return auNode;
    }

    public AuNode setAuIPNodePerService(Node node,int auNodeID,boolean isOut){
        AuNode auNode = new AuNode(node.nodeID,node.isFixedNode);
        boolean flagSubFound = false;//for transponder
        boolean flagResEnough = false;//for transponder
        ++ auNodeID;
        int NodeType;
        if (isOut){
            NodeType = AuNode.TypeIPout;
        }else {
            NodeType = AuNode.TypeIPin;
        }
        auNode.setAuNodePara(auNodeID,flagSubFound,flagResEnough,NodeType);
        return auNode;
    }

//查看路径p上是不是有足够的资源，如果有足够的资源，返回true
/*    public boolean checkResource(Path p,int requiredKeys){
        boolean result = true;


        int aux[][] = new int[CommonResource.NODE_NUMBER][CommonResource.NODE_NUMBER];
        for (int i = 0; i < aux.length; i++) {
            for (int j = 0; j < aux[0].length; j++) {
                aux[i][j] = 0;
            }
        }
        for (int i = 0; i < p.nodes.size(); i++) {

            GraphPath<Node, AccessEdge> gpath = p.nodes.get(i);
            List<AccessEdge> edgeList = gpath.getEdgeList();

            for (int j = 0; j < edgeList.size(); j++) {
                AccessEdge accessEdge = edgeList.get(j);
                aux[accessEdge.getSource().nodeID-1][accessEdge.getDest().nodeID-1]+=requiredKeys;

            }
        }
        for (int i = 0; i < aux.length; i++) {
            for (int j = 0; j < aux[0].length; j++) {
                if(aux[i][j]>CommonResource.keyResource[i][j])
                return false;
            }
        }

        return result;
    }*/

    public boolean checkResourceTree(Tree p,int requiredKeys){
        boolean result = true;


        int aux[][] = new int[CommonResource.NODE_NUMBER][CommonResource.NODE_NUMBER];
        for (int i = 0; i < aux.length; i++) {
            for (int j = 0; j < aux[0].length; j++) {
                aux[i][j] = 0;
            }
        }

        traverseTree(p.resultTree,aux,requiredKeys);
        for (int i = 0; i < aux.length; i++) {
            for (int j = 0; j < aux[0].length; j++) {
                if(aux[i][j]>CommonResource.keyResourceTree[i][j])
                    return false;
            }
        }

        return result;
    }

    public void traverseTree(TreeNode tn,int[][] aux,int requiredKeys){
        if(tn==null)
            return;
        Node src = tn.value;
        List<TreeNode> desList = tn.nlist;
        for (int i = 0; i < desList.size(); i++) {
            Node des = desList.get(i).value;
            aux[src.nodeID-1][des.nodeID-1]+=requiredKeys;
            traverseTree(desList.get(i),aux,requiredKeys);
        }

    }
    //分配资源
/*    public void allocateResource(Path p ,int requiredKeys){


        for (int i = 0; i < p.nodes.size(); i++) {

            GraphPath<Node, AccessEdge> gpath = p.nodes.get(i);
            List<AccessEdge> edgeList = gpath.getEdgeList();
            for (int j = 0; j < edgeList.size(); j++) {
                AccessEdge accessEdge = edgeList.get(j);

                CommonResource.keyResource[accessEdge.getSource().nodeID-1][accessEdge.getDest().nodeID-1]-=requiredKeys;
            }
        }

        if(!checkForNoMinus())
            log.warn("no minus");

    }*/

    public void allocateResourceTree(Tree tr ,int requiredKeys){

        int aux[][] = new int[CommonResource.NODE_NUMBER][CommonResource.NODE_NUMBER];
        for (int i = 0; i < aux.length; i++) {
            for (int j = 0; j < aux[0].length; j++) {
                aux[i][j] = 0;
            }
        }

        traverseTree(tr.resultTree,aux,requiredKeys);

        for (int i = 0; i < aux.length; i++) {
            for (int j = 0; j < aux[0].length; j++) {
                CommonResource.keyResourceTree[i][j]-=aux[i][j];
            }
        }

    }

    //check whether there exists minus resource
    public boolean checkForNoMinus(){
        for (int i = 0; i < CommonResource.NODE_NUMBER; i++) {
            for (int j = 0; j < CommonResource.NODE_NUMBER; j++) {
                if(CommonResource.keyResource[i][j]<0)
                    return false;
            }
        }
        return true;
    }

    public Map<Integer,ServiceEvent> getServiceMap(){
        return this.serviceMap;
    }


}
