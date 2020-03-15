package Topology;

import Common.CommonResource;
import Gen.ServiceEvent;

import java.io.FileWriter;
import java.io.IOException;
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
    public void outputResToFile(){
        int sum = serviceMap.size();
        int sumService = 0;
        int count = 0;//calcute partial
        int countTotal = 0;//count total success
        int step = 50;
        double aTemp;
        boolean flag;
        ArrayList<Double> successRadio = new ArrayList<Double>();
        ArrayList<Double> totalSuccessRadio = new ArrayList<Double>();
        for (int i = 0; i < serviceMap.size(); i++) {

            if(isTree){
               flag = serviceMap.get(i).flagForSuccessTree;
            }
            else{
                flag = serviceMap.get(i).flagForSuccess;
            }
            if (flag){
                count++;
            }

            if((i+1)%step==0){
                aTemp = (double)count/(double)(i+1);
                successRadio.add(aTemp);
            }

        }
        String fileName;
        if(isTree)
            fileName = "Tree"+sum+"resD.txt";
        else
            fileName =  sum+"resD.txt";

        System.out.println(count +" "+ sum);
//        System.out.println(countTotal +" "+ sumService);
//        double aveEnergy = calcuConsumEnergy()/count;
//        System.out.println("aveEnergy: "+aveEnergy);

        writeArrayToTxt(fileName,successRadio);
    }

    public void writeArrayToTxt( String string,ArrayList<Double> successRadio) {
        System.out.println(string);
        int rowNum = successRadio.size();
        int columnNum = 1;
        try {
            FileWriter fw = new FileWriter(string);

            fw.write("successRadio:"+"\n");
/*            for (int i = 0; i < successRadio.size(); i++) {
                fw.write(successRadio.get(i)+"\t");
            }*/
            for (int i = 0; i < rowNum; i++) {
                for (int j = 0; j < columnNum; j++)
                    fw.write(successRadio.get(i)+ "\t");
                fw.write("\n");
            }

            fw.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
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