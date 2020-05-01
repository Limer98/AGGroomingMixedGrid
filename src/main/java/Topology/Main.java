package Topology;

import Common.CommonResource;
import Gen.ServiceEvent;
import Network.Network;
import Utils.ReadTopo;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
//        Map<Integer,ServiceEvent> serviceMap;
//        List<Node> nodeList =  new ArrayList<Node>();
        List<Link> linkList =  new ArrayList<Link>();
        //read topo and get node and linklist
        ReadTopo topo = new ReadTopo(CommonResource.FILE_NAME);
        Network network = new Network(topo);
        network.setAllFixedNodes();
        network.setFlexNodes(CommonResource.flexNodes);
        CommonResource.nodeList = topo.getNodeList();
        CommonResource commonResource = new CommonResource();
/*        for (int i = 0; i < network.getNumNodes(); i++){
            System.out.println(nodeList.get(i).nodeID+" "+nodeList.get(i).isFixedNode);
        }*/
        //Resource initiation
        for (int i = 0; i < CommonResource.nodeList.size(); i++){
            CommonResource.nodeList.get(i).initNodeResource();
        }

        linkList = topo.getLinkList();
        //read serList from txt
        double rou = 200;
//        for (int c = 0; c < 15; c++){
        for (CommonResource.ALGORITHM_TYPE = 0; CommonResource.ALGORITHM_TYPE < 4; CommonResource.ALGORITHM_TYPE++) {
            String[] algorithmTxt = {"EMG", "MOG", "MEG", "MVH"};
            //Resource initiation
            for (int i = 0; i < CommonResource.nodeList.size(); i++){
                CommonResource.nodeList.get(i).initNodeResource();
            }
            CommonResource.resultsList.clear();
            rou = 200;
            for (int c = 0; c < 1; c++){//different trafficload
                CommonResource.resultsListTemp.clear();
                //Resource initiation
                for (int i = 0; i < CommonResource.nodeList.size(); i++){
                    CommonResource.nodeList.get(i).initNodeResource();
                }
                for (int i = 0; i < CommonResource.RUNTIMES_NUMBER; i++) {//40*5000=200000=100000*2
                    Map<Integer, ServiceEvent> serviceMap;
                    String fileName = "100000-nodeNum14-mu0.04-rou" + rou + "-TR{10,40,100,120,160,200,400}.txt";
//                    String fileName = "testCRs.txt";
                    List<ServiceEvent> serviceList = commonResource.readSerListFromTXTsplit(fileName, i);
                    EventDealer eventDealer = new EventDealer(serviceList, CommonResource.nodeList, linkList);
                    eventDealer.doDeal();
                    serviceMap = eventDealer.getServiceMap();
                    SimulationResult simulationResult = new SimulationResult(serviceMap, false);
                    simulationResult.outputResToFile(rou);
                }
                //统计结果_平均能耗每请求
                int count=0;
                int sum=0;
                double energyConsumed = 0;
                int totalVirHops = 0;
                int totalPhysHops = 0;
                for (Result result:CommonResource.resultsListTemp) {
                    count += result.count;
                    sum += result.sum;
                    energyConsumed += result.energyConsumed;
                    totalVirHops += result.totalVirHops;
                    totalPhysHops += result.totalPhysHops;
                }
                Result resFinal = new Result(rou,count,sum,energyConsumed/count,(double)totalVirHops/count,(double)totalPhysHops/count);
                CommonResource.resultsList.add(resFinal);
                //统计结果_总能耗每节点
                System.out.println(algorithmTxt);
//                System.out.println("Start_energy distribution per node");
                for (Node node:CommonResource.nodeList) {
                    System.out.println(node.nodeID+"\t"+node.energy);
                }
//                System.out.println("End_energy distribution per node");
                //增加trafficload
                rou = rou + 20;
            }
//            String[] string = {"EMG", "MOG", "MEG", "MVH"};
            String fpName = algorithmTxt[CommonResource.ALGORITHM_TYPE] + CommonResource.numConnectionRequest + "_" + rou + ".txt";
            commonResource.writeArrayToTxt(fpName);//一种算法，输出一次结果，trafficload变化
        }

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
