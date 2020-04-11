package Topology;

import Common.CommonResource;
import Gen.ServiceEvent;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @auther：sherrystar
 * @package_name: Topology
 * @create_time: 2019/1/19
 * @describe:
 */
public class SimulationResult {
    public Map<Integer,ServiceEvent> serviceMap;

    Boolean isTree;

    public SimulationResult(Map<Integer, ServiceEvent> serviceMap,Boolean isTree) {

        this.serviceMap = serviceMap;
        this.isTree = isTree;
    }

   /* public double calcuConsumEnergy(){
        int totalNumNSTx = 0;
        int totalNumNSRx = 0;
        int totalNumFSTx = 0;
        int totalNumFSRx = 0;
        int consumCapFSTx = 0;
        int consumCapFSRx = 0;
        int totalNumIPout = 0;
        int totalNumIPin = 0;
        double totalConsumEnergyTrans;
        double totalConsumEnergyIP;
        double totalConsumEnergy;
//        int totalCapa = CommonResource.GbpsNSTransponder*CommonResource.numNSTransponderPerNode;
        int subTransCapa = CommonResource.GbpsFSTransponder/CommonResource.numSubTransponderPerT;
        for (int t = 0; t < CommonResource.nodeList.size(); t++){
            Node node = CommonResource.nodeList.get(t);

            if (node.getIsFixedNode()){
                for (int i = 0; i < CommonResource.numTransponderPerNode; i++){
                    if (node.TxResource[i][0]<CommonResource.GbpsNSTransponder){
                        totalNumNSTx ++;
                    }
                    if (node.RxResource[i][0]<CommonResource.GbpsNSTransponder){
                        totalNumNSRx ++;
                    }
                }
            }else{
                boolean flagTx = false;
                boolean flagRx = false;
                for (int i = 0; i < CommonResource.numTransponderPerNode; i++){
                    for (int j = 0; j < CommonResource.numSubTransponderPerT; j++){
                        if (node.TxResource[i][j]<subTransCapa){
                            flagTx = true;
                            consumCapFSTx += (subTransCapa-node.TxResource[i][j]);
                        }
                        if (node.RxResource[i][j]<subTransCapa){
                            flagRx = true;
                            consumCapFSRx += (subTransCapa-node.RxResource[i][j]);
                        }
                    }
                    if (flagTx){
                        totalNumFSTx ++;
                        flagTx = false;
                    }
                    if (flagRx){
                        totalNumFSRx ++;
                        flagRx = false;
                    }
                }
            }
            for (int i = 0; i < CommonResource.numIPPortPerNode; i++){
                if (node.IPoutResource[i]<CommonResource.GbpsIPPort){
                    totalNumIPout ++;
                }
                if (node.IPinResource[i]<CommonResource.GbpsIPPort){
                    totalNumIPin ++;
                }
            }

        }
        totalConsumEnergyTrans = totalNumNSTx*185.5+totalNumNSRx*185.5+0.5*(1.683*consumCapFSTx+91.333*totalNumFSTx)
                +0.5*(1.683*consumCapFSRx+91.333*totalNumFSRx);
        totalConsumEnergyIP = totalNumIPout*280+totalNumIPin*280;
        totalConsumEnergy = totalConsumEnergyTrans+totalConsumEnergyIP;

        return totalConsumEnergy;
    }*/

    //以连接请求个数为变量
    //numRequest change
    public void outputResToFile(double rou){
        int sum = serviceMap.size();
        int sumService = 0;
        int count = 0;
        double energyConsumed = 0;
        int totalVirHops = 0;
        int totalPhysHops = 0;
        boolean flag;
        Iterator iter = serviceMap.entrySet().iterator();
        while (iter.hasNext()){
            HashMap.Entry entry = (HashMap.Entry) iter.next();
//            Object serID = entry.getKey();
            Object serInMap = entry.getValue();
            ServiceEvent ser = (ServiceEvent) serInMap;
            if(isTree){
               flag = ser.flagForSuccessTree;
            }
            else{
                flag = ser.flagForSuccess;
            }
            if (flag){
                count++;
                energyConsumed += ser.energyConsumed;
                totalVirHops += ser.virHops;
                totalPhysHops += ser.physHops;
            }
        }
//        for (int i = 0; i < serviceMap.size(); i++) {
//
//            if(isTree){
//               flag = serviceMap.get(i).flagForSuccessTree;
//            }
//            else{
//                if (serviceMap.get(i) == null){
//                    System.out.println("serviceMap.get(i) == null");
//                }
//                flag = serviceMap.get(i).flagForSuccess;
//            }
//            if (flag){
//                count++;
//                energyConsumed += serviceMap.get(i).energyConsumed;
//                totalVirHops += serviceMap.get(i).virHops;
//                totalPhysHops += serviceMap.get(i).physHops;
//            }
//        }

//        double successRatio = (double)count/sum;
//        double aveEnergy = energyConsumed/count;
//        double aveVirHops = (double) totalVirHops/count;
//        double avePhysHops = (double) totalPhysHops/count;
//        System.out.println(rou+"\t"+aveEnergy+"\t"+successRatio+"\t"+energyConsumed+"\t"+aveVirHops+"\t"+avePhysHops);
        System.out.println(rou+"\t"+count+"\t"+serviceMap.size()+"\t"+energyConsumed+"\t"+totalVirHops+"\t"+totalPhysHops);
        CommonResource.resultsList.add(new Result(rou,count,serviceMap.size(),energyConsumed,totalVirHops,totalPhysHops));
//        String fpName = "result1";
//        writeArrayToTxt(fpName,rou,count,serviceMap.size(),energyConsumed,totalVirHops,totalPhysHops);
//        writeArrayToTxt(fileName,rou,successRatio,energyConsumed,aveEnergy);
    }

//    public void writeArrayToTxt(String string, double rou, int count, int sum, double energyConsumed, double totalVirHops, double totalPhysHops) {
////        System.out.println(string);
//        int rowNum = serviceMap.size();//行
//        int columnNum = 6;//列
//        try {
//            FileWriter fw = new FileWriter(string);
//            for (int i = 0; i < rowNum; i++){
//                for (int j = 0; j < columnNum;j++){
//                    fw.write(rou+"\t");
//                    fw.write(count+"\t");
//                    fw.write(sum+"\t");
//                    fw.write(energyConsumed+"\t");
//                    fw.write(totalVirHops+"\t");
//                    fw.write(totalPhysHops+"\t");
//                    fw.write("\n");
//                }
//            }
//
////            fw.write("successRadio:"+"\n");
///*            for (int i = 0; i < successRadio.size(); i++) {
//                fw.write(successRadio.get(i)+"\t");
//            }*/
////            for (int i = 0; i < rowNum; i++) {
////                for (int j = 0; j < columnNum; j++)
////                    fw.write(successRadio.get(i)+ "\t");
////                fw.write("\n");
////            }
//
////            fw.close();
//        }
//        catch (IOException e){
//            e.printStackTrace();
//        }
//    }
/*    public void writeArrayToTxt(int[][] data, String string,ArrayList<Double> successRadio) {
        System.out.println(string);
        int rowNum = data.length;
        int columnNum = data[0].length;
        try {
            FileWriter fw = new FileWriter(string);
            for (int i = 0; i < rowNum; i++) {
                for (int j = 0; j < columnNum; j++)
                    fw.write(data[i][j]+ "\t");
                fw.write("\n");
            }

            fw.write("successRadio:");
            for (int i = 0; i < successRadio.size(); i++) {
                fw.write(successRadio.get(i)+"\t");
            }

            fw.write("\n");
            fw.write("totalSuccessRadio:");
//            for (int i = 0; i <totalSuccessRadio.size(); i++) {
//                fw.write(totalSuccessRadio.get(i)+"\t");
//            }

            fw.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }*/

}
