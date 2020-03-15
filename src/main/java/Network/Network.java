package Network;
//定义网络拓扑结构

import Common.Algorithms;
import Gen.*;
import Topology.*;
import Utils.DoubleHash;
import Utils.ReadTopo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Serializable;
import java.util.*;

public class Network implements Serializable {
    private static final long serialVersionUID = 1L; //用于序列化和反序列化
    private int numSlots, numNodes, numLinks;
    private static int M = 4;//4*12.5=100Gbps
    private static DoubleHash<Integer, Integer, ArrayList<Path>> candidatePaths;
    private HashMap<ServiceEvent, Connection> ongoingConnections;
    private HashMap<Link, Collection<Connection>> linkIndexedConnections;
    public HashMap<Integer, Node> nodeIndex; //nodeID node
    private ReadTopo readTopo;

    public Network(ReadTopo readTopo){

        numNodes = readTopo.nodeList.size();
        numLinks = readTopo.linkList.size();

        nodeIndex = new HashMap<Integer, Node>();
        for (int i = 1; i <= numNodes; i++){
            nodeIndex.put(i, readTopo.getNodeList().get(i-1));
        }

        ongoingConnections = new HashMap<ServiceEvent, Connection>();
        linkIndexedConnections = new HashMap<Link, Collection<Connection>>();
    }

    public void setAllFixedNodes(){
        for (int i = 1; i<= numNodes; i++){
            nodeIndex.get(i).setFixedNode();
        }
    }

    public void setFlexNodes(int[] flexNodes){
        for (int i = 0; i < flexNodes.length; i++){
            nodeIndex.get(flexNodes[i]).setFlexNode();
        }
    }
    public int getNumNodes(){ return numNodes; }
    public int getNumLinks(){ return numLinks; }

    public void reset(){
        for(Link link : readTopo.linkList){
            link.reset();
        }
        ongoingConnections.clear();
        linkIndexedConnections.clear();
    }

    public NetworkState recordNetworkState(){
        NetworkState state = new NetworkState();
        state.linkSet = new ArrayList<Link>();
        for (Link link : readTopo.linkList){
            ((ArrayList<Link>) state.linkSet).add(new Link(link));
        }
        state.connectionSet = new ArrayList<Connection>();
        for (Connection connection : ongoingConnections.values()){
            ((ArrayList<Connection>) state.connectionSet).add(new Connection(connection));
        }
        return state;
    }

    public int getNumConnections(){ return ongoingConnections.size(); }

    private boolean[] assignSpectrum(int start, int bandwidth){
        boolean[] spectrumAssignment = new boolean[numSlots];
        for (int i = 0; i < numSlots; i++){
            if ((i >= start)&&(i < start+bandwidth)){
                spectrumAssignment[i] = true;
            }
            else{
                spectrumAssignment[i] = false;
            }
        }
        return spectrumAssignment;
    }

    public boolean provisionSuperChannel(ServiceEvent request){
        int start = numSlots;
        int minStart = numSlots;
        Path route = null;
        for (Path path : candidatePaths.get(request.src, request.dst).subList(0,6)){
            //假如路径上所有点都是灵活节点;
            if (path.goThroughFixedNode() == false){
                boolean[] commonSlots = path.getCommonSlots();
                start = Algorithms.firstFit_AllFlexNodes(commonSlots, request.bandwidth);
            }
            if (start < minStart){ //找到连续可用的满足需求的频谱块
                minStart = start;
                route = path;
            }
        }

        if (minStart < numSlots){
            Connection connection = new Connection(request, route, assignSpectrum(minStart,request.bandwidth));
            deploy(connection);//为连接分配资源
            return true;
        }
        return false;
    }

    public boolean singlePath_provision(ServiceEvent request){
        int minStart = numSlots;
        Path route = null;
        Node sourceNode = nodeIndex.get(request.src);
        for (Path path : candidatePaths.get(request.src, request.dst).subList(0,5)){
            boolean[] commonSlots = path.getCommonSlots();
            int start = numSlots;
            //假如路径上所有点都是灵活节点；
            if ((sourceNode.getIsFixedNode() == false)&&(path.goThroughFixedNode() == false)){
                start = Algorithms.firstFit_AllFlexNodes(commonSlots, request.bandwidth);
            }
            //源节点是灵活节点，但经过固定节点；
            if ((sourceNode.getIsFixedNode() == false)&&(path.goThroughFixedNode() == true)){
                start = Algorithms.firstFit_FromFlexGoThroughFixed(commonSlots, request.bandwidth);
            }
            //源节点是固定节点
            if (sourceNode.getIsFixedNode() == true){
                int bandwidth = M; //4*12.5=100Gbps
                start = Algorithms.firstFit_FromFixed(commonSlots, request.bandwidth);
            }
            if(start < minStart){
                minStart = start;
                route = path;
            }
        }
        if(minStart < numSlots){
            Connection connection = new Connection(request, route, assignSpectrum(minStart, request.bandwidth));
            deploy(connection);
            return true;
        }
        return false;
    }

    public boolean provision(List<ServiceEvent> eventQueue, ServiceEvent request){
        boolean provisionSuceess = false;
        //建立单通道；
        if(request.bandwidth <= 4){
            provisionSuceess = singlePath_provision(request);
        }
        //建立多通道；
        else{
            //假如源节点是灵活节点；
            Node sourceNode = nodeIndex.get(request.src);
            if(sourceNode.getIsFixedNode() == false){
                //首先尝试超通道；
                if(provisionSuperChannel(request)){
                    return true;
                }
                //如果超通道不行，则建立多通道；
                else{
                    //例如，带宽为6则分成2个通道；带宽为10则分为3个通道；每个通道为100G，占用3个Slots;
                    int bandwidth = request.bandwidth;
                    int splitNum = 0;
                    if(bandwidth == 6) {splitNum = 2;}
                    if(bandwidth == 10) {splitNum = 3;}
//					System.out.println("totoal bandwidth is " + bandwidth + ", try to build " + splitNum + " paths!");
                    HashMap<Integer, ServiceEvent> splitRequest = new HashMap<Integer, ServiceEvent>();
                    int beenSplitProvision = 0;
                    for(int pathNum = 1; pathNum <= splitNum; pathNum++){
                        ServiceEvent splitedRequest = new ServiceEvent(request);
                        splitedRequest.setBandwidth(3);
                        splitedRequest.splitNum = pathNum;
                        splitRequest.put(pathNum, splitedRequest);
                        provisionSuceess = singlePath_provision(splitRequest.get(pathNum));
                        if(provisionSuceess){
                            beenSplitProvision++;
                            eventQueue.add(new ServiceEvent(EventType.SERVICE_END,request.eventId,
                                    request.getArriveTime(),request.getHoldTime(),
                                    request.src,request.dst,request.transmissionRate)); //加上bandwidth, splitNum
                            continue;
                        }
                        else{
                            for(int i = 1; i <= beenSplitProvision; i++){
                                release(splitRequest.get(i));
                            }
//							System.out.println("However, failed!!!");
                            break;
                        }
                    }
                }
            }

            //假如源节点是固定节点；
            else{
                //例如，带宽为6则分成2个通道；带宽为10则分为4个通道；每个通道为100G，占用4个Slots;
                int bandwidth = request.bandwidth;
                int splitNum = 0;
                if(bandwidth == 6) {splitNum = 2;}
                if(bandwidth == 10) {splitNum = 4;}
//				System.out.println("totoal bandwidth is " + bandwidth + ", try to build " + splitNum + " paths!");
                HashMap<Integer, ServiceEvent> splitRequest = new HashMap<Integer, ServiceEvent>();
                int beenSplitProvision = 0;
                for(int pathNum = 1; pathNum <= splitNum; pathNum++){
                    ServiceEvent splitedRequest = new ServiceEvent(request);
                    splitedRequest.setBandwidth(4);
                    splitedRequest.splitNum = pathNum;
                    splitRequest.put(pathNum, splitedRequest);
                    provisionSuceess = singlePath_provision(splitRequest.get(pathNum));
                    if(provisionSuceess){
                        beenSplitProvision++;
                        eventQueue.add(new ServiceEvent(EventType.SERVICE_END,request.eventId,
                                request.getArriveTime(),request.getHoldTime(),
                                request.src,request.dst,request.transmissionRate));
                        continue;
                    }
                    else{
                        for(int i = 1; i <= beenSplitProvision; i++){
                            release(splitRequest.get(i));
                        }
//						System.out.println("However, failed!!!");
                        break;
                    }
                }
            }
        }
        return provisionSuceess;
    }

    public void deploy(Connection connection){
/*        for (Link link : connection.route){
            link.mask(connection.spectrumAssignment);
            if (!linkIndexedConnections.containsKey(link)){
                linkIndexedConnections.put(link, new HashSet<Connection>());
                linkIndexedConnections.get(link).add(connection);
            }
        }*/
        ongoingConnections.put(connection.getRequest(), connection);
        //用于测试
//        System.out.print("deploy: ");
//        connection.getRequest().printRequest(connection.getRequest());
//        connection.route.printPath(connection.route);
    }

    public Connection release(ServiceEvent request){
        if (!ongoingConnections.containsKey(request)){
            return null;
        }
        Connection connection = ongoingConnections.get(request);
/*        for (Link link : connection.route){
            link.unmask(connection.spectrumAssignment);
            linkIndexedConnections.get(link).remove(connection);
        }*/
        ongoingConnections.remove(request);
//        System.out.print("release: ");
//        request.printRequest(request);
        return connection;
    }

/*    public static void main(String[] args){
        Network network = new Network(10);
        System.out.println(network.numNodes); //14
        System.out.println(network.numLinks); //42
//        int node = network.readTopo.linkList.get(4);
//        boolean isFixedNode = network.nodeIndex.get(14).getIsFixedNode();
//        System.out.println(node);
//        System.out.println(isFixedNode);
    }*/

}
