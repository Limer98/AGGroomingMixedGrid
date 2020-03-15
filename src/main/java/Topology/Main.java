package Topology;

import Common.CommonResource;
import Gen.ServerGenerator;
import Gen.ServiceEvent;
import Network.Network;
import Utils.ReadTopo;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

import java.net.CookieHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @auther：sherrystar
 * @package_name: Topology
 * @create_time: 2019/1/19
 * @describe:
 */
public class Main {

    public static void main(String[] args) {
        Long startTime = System.currentTimeMillis();
        Map<Integer,ServiceEvent> serviceMap;
//        List<Node> nodeList =  new ArrayList<Node>();
        List<Link> linkList =  new ArrayList<Link>();
        //read topo and get node and linklist
        ReadTopo topo = new ReadTopo(CommonResource.FILE_NAME);
        Network network = new Network(topo);
        network.setAllFixedNodes();
        network.setFlexNodes(CommonResource.flexNodes);
        CommonResource.nodeList = topo.getNodeList();
/*        for (int i = 0; i < network.getNumNodes(); i++){
            System.out.println(nodeList.get(i).nodeID+" "+nodeList.get(i).isFixedNode);
        }*/
        //Resource initiation
        for (int i = 0; i < CommonResource.nodeList.size(); i++){
            CommonResource.nodeList.get(i).initNodeResource();
        }

        linkList = topo.getLinkList();
        DefaultDirectedWeightedGraph<Node, AccessEdge> graphG = new Graph(CommonResource.nodeList,linkList).getG(); //默认有向加权图

        ServerGenerator serverG = new ServerGenerator(CommonResource.NODE_NUMBER,0.04,100,
                CommonResource.numConnectionRequest,0, CommonResource.transmissionRateSet.length);
        List<ServiceEvent> serviceList = serverG.genEventQueue();

        EventDealer eventDealer = new EventDealer(serviceList,graphG,CommonResource.nodeList,linkList);
        eventDealer.doDeal();

        serviceMap = eventDealer.getServiceMap();
/*
        for (int i = 0; i < serviceMap.size(); i++) {
            System.out.println(serviceMap.get(i).get(0).flagForSuccess);
        }*/

       SimulationResult simulationResult = new SimulationResult(serviceMap,false);
//        SimulationResult simulationResultTree = new SimulationResult(serviceMap,true);
        simulationResult.outputResToFile();
//        simulationResultTree.outputResToFile();
        Long endTime = System.currentTimeMillis();

// 计算并打印耗时
        Long tempTime = (endTime - startTime);
        System.out.println("consumedTime: "+
                (((tempTime/86400000)>0)?((tempTime/86400000)+"d"):"")+
                ((((tempTime/86400000)>0)||((tempTime%86400000/3600000)>0))?((tempTime%86400000/3600000)+"h"):(""))+
                ((((tempTime/3600000)>0)||((tempTime%3600000/60000)>0))?((tempTime%3600000/60000)+"m"):(""))+
                ((((tempTime/60000)>0)||((tempTime%60000/1000)>0))?((tempTime%60000/1000)+"s"):(""))+
                ((tempTime%1000)+"ms"));
    }
}
