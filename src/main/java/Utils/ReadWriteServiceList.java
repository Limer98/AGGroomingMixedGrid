package Utils;

import Common.CommonResource;
import Gen.EventType;
import Gen.Generator;
import Gen.ServerGenerator;
import Gen.ServiceEvent;
import Topology.Result;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadWriteServiceList {
    public List<ServiceEvent> readSerListFromTXT(String fileName){
        List<ServiceEvent> serviceEventList = new ArrayList<ServiceEvent>();

        BufferedReader inputStream = null;
        boolean isFirstLine = true;
        try {
            inputStream = new BufferedReader(new FileReader(fileName));
            String line;
            while((line = inputStream.readLine()) != null){
                String[] nodeRow = line.split("\\s+");
                //EventType eventType, int eventId, double arriveTime, double holdTime, int src, int dst, int transmissionRate
                ServiceEvent ser = new ServiceEvent();
                if (isFirstLine){
                    isFirstLine = false;//第一行是说明，所以读的时候要跳过
                    continue;
                }
                for (int j = 0; j < nodeRow.length; j++) {
                    switch (j){
                        case 0: {
                            if (nodeRow[j].equals("SERVICE_ARRIVAL")){
                                ser.setEventType(EventType.SERVICE_ARRIVAL);
                            }else if (nodeRow[j].equals("SERVICE_END")){
                                ser.setEventType(EventType.SERVICE_END);
                            }
                            break;
                        }
                        case 1: ser.eventId = Integer.parseInt(nodeRow[j]);break;
                        case 2: ser.setArriveTime(Double.parseDouble(nodeRow[j]));break;
                        case 3: ser.setHoldTime(Double.parseDouble(nodeRow[j]));break;
                        case 4: ser.setSrc(Integer.parseInt(nodeRow[j]));break;
                        case 5: ser.setDst(Integer.parseInt(nodeRow[j]));break;
                        case 6: ser.setTransmissionRate(Integer.parseInt(nodeRow[j]));break;
                        default:break;
                    }
                }
                serviceEventList.add(ser);
            }
        } catch(Exception e){
            System.out.println(e);
        }

        return serviceEventList;
    }
    public void writeSerListToTXT(String fileName,List<ServiceEvent> serList){
        System.out.println(fileName);
        int rowNum = serList.size();
        int columnNum = 7;
        //EventType eventType, int eventId, double arriveTime, double holdTime, int src, int dst, int transmissionRate
        try {
            FileWriter fw = new FileWriter(fileName);

            fw.write("eventType, eventId , arriveTime, holdTime, src, dst, transmissionRate"+"\n");
            for (int i = 0; i < rowNum; i++) {
                for (int j = 0; j < columnNum; j++){
                    switch (j){
                        case 0: fw.write(serList.get(i).getEventType()+ "\t"); break;
                        case 1: fw.write(serList.get(i).getEventId()+ "\t"); break;
                        case 2: fw.write(serList.get(i).getArriveTime()+ "\t"); break;
                        case 3: fw.write(serList.get(i).getHoldTime()+ "\t"); break;
                        case 4: fw.write(serList.get(i).getSrc()+ "\t"); break;
                        case 5: fw.write(serList.get(i).getDst()+ "\t"); break;
                        case 6: fw.write(serList.get(i).getTransmissionRate()+ "\t"); break;
                        default: break;
                    }
                }
                fw.write("\n");
            }
            fw.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public List<ServiceEvent> readSerListFromTXTsplit(String fileName,int num){
        List<ServiceEvent> serviceEventList = new ArrayList<ServiceEvent>();

        BufferedReader inputStream = null;
        boolean isFirstLine = true;
        int num1 = 2000*num;
        int num2 = 2000*(num+1);
        try {
            inputStream = new BufferedReader(new FileReader(fileName));
            String line;
            int c = 0;
            while((line = inputStream.readLine()) != null){
                if (c>=num1&&c<num2){
                    String[] nodeRow = line.split("\\s+");
                    //EventType eventType, int eventId, double arriveTime, double holdTime, int src, int dst, int transmissionRate
                    ServiceEvent ser = new ServiceEvent();
                    if (isFirstLine){
                        isFirstLine = false;//第一行是说明，所以读的时候要跳过
                        continue;
                    }
                    for (int j = 0; j<nodeRow.length; j++) {
                        switch (j){
                            case 0: {
                                if (nodeRow[j].equals("SERVICE_ARRIVAL")){
                                    ser.setEventType(EventType.SERVICE_ARRIVAL);
                                }else if (nodeRow[j].equals("SERVICE_END")){
                                    ser.setEventType(EventType.SERVICE_END);
                                }
                                break;
                            }
                            case 1: ser.eventId = Integer.parseInt(nodeRow[j]);break;
                            case 2: ser.setArriveTime(Double.parseDouble(nodeRow[j]));break;
                            case 3: ser.setHoldTime(Double.parseDouble(nodeRow[j]));break;
                            case 4: ser.setSrc(Integer.parseInt(nodeRow[j]));break;
                            case 5: ser.setDst(Integer.parseInt(nodeRow[j]));break;
                            case 6: ser.setTransmissionRate(Integer.parseInt(nodeRow[j]));break;
                            default:break;
                        }
                    }
                    serviceEventList.add(ser);
                }
                c++;
            }
        } catch(Exception e){
            System.out.println(e);
        }

        return serviceEventList;
    }
    //print result of a service
    public void writeArrayToTxt(String string, List<Result> resultsList) {
//        System.out.println(string);
        int count=0;
        int sum=0;
        double energyConsumed = 0;
        int totalVirHops = 0;
        int totalPhysHops = 0;
        int rowNum = resultsList.size();//行
        int columnNum = 6;//列
        try {
            FileWriter fw = new FileWriter(string);
            fw.write("rou"+"\t"+"count"+"\t"+"serviceMap.size()"+"\t"+"energyConsumed"+"\t"+"totalVirHops"+"\t"+"totalPhysHops"+"\n");
            for (int i = 0; i < rowNum; i++){
                fw.write(resultsList.get(i).rou+"\t");
                fw.write(resultsList.get(i).count+"\t");
                fw.write(resultsList.get(i).sum+"\t");
                fw.write(resultsList.get(i).energyConsumed+"\t");
                fw.write(resultsList.get(i).totalVirHops+"\t");
                fw.write(resultsList.get(i).totalPhysHops+"\t");
                fw.write("\n");
                count += resultsList.get(i).count;
                sum += resultsList.get(i).sum;
                energyConsumed += resultsList.get(i).energyConsumed;
                totalVirHops += resultsList.get(i).totalVirHops;
                totalPhysHops += resultsList.get(i).totalPhysHops;
            }
            fw.write(count+"\t"+sum+"\t"+energyConsumed/count+"\t"+(double)totalVirHops/count+"\t"+(double)totalPhysHops/count+"\n");
            fw.close();//close the file after writing
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
/*    //产生不同到达离去率的请求，TRset相同
    public List<ServiceEvent> readSerListFromTXT1(String fileName,double mu, double rou){
        List<ServiceEvent> serviceEventList = new ArrayList<ServiceEvent>();
        BufferedReader inputStream = null;
        boolean isFirstLine = true;
        double departTime=0;
        try {
            inputStream = new BufferedReader(new FileReader(fileName));
            String line;
            boolean flagArrive = false;
            while((line = inputStream.readLine()) != null){
                String[] nodeRow = line.split("\\s+");
                //EventType eventType, int eventId, double arriveTime, double holdTime, int src, int dst, int transmissionRate
                ServiceEvent ser = new ServiceEvent();
                Generator serGen = new Generator();
                if (isFirstLine){
                    isFirstLine = false;//第一行是说明，所以读的时候要跳过
                    continue;
                }
                for (int j = 0; j < nodeRow.length; j++) {
                    switch (j){
                        case 0: {
                            if (nodeRow[j].equals("SERVICE_ARRIVAL")){
                                ser.setEventType(EventType.SERVICE_ARRIVAL);
                                flagArrive = true;
                            }else if (nodeRow[j].equals("SERVICE_END")){
                                ser.setEventType(EventType.SERVICE_END);
                                flagArrive = false;
                            }
                            break;
                        }
                        case 1: ser.eventId = Integer.parseInt(nodeRow[j]);break;
                        case 2: {
                            if (flagArrive){
                                double arrivalTime = serGen.genArrivalTime(mu*rou);
                                ser.setArriveTime(arrivalTime);
                                Generator.currentTime = arrivalTime;
                            }else{
                                ser.setArriveTime(Generator.currentTime);
                            }
                            break;
                        }
                        case 3:
                        {
                            if (flagArrive){
                                departTime = serGen.genHoldTime(mu);
                                ser.setHoldTime(departTime);
                            }else{
                                ser.setHoldTime(departTime);
                            }
                            break;
                        }
                        case 4: ser.setSrc(Integer.parseInt(nodeRow[j]));break;
                        case 5: ser.setDst(Integer.parseInt(nodeRow[j]));break;
                        case 6: ser.setTransmissionRate(Integer.parseInt(nodeRow[j]));break;
                        default:break;
                    }
                }
                serviceEventList.add(ser);
            }
        } catch(Exception e){
            System.out.println(e);
        }

        return serviceEventList;
    }

    //产生相同到达离去率的请求，TRset不同
    public List<ServiceEvent> readSerListFromTXT2(String fileName){
        List<ServiceEvent> serviceEventList = new ArrayList<ServiceEvent>();
        BufferedReader inputStream = null;
        boolean isFirstLine = true;
        int transRate = 0;
        try {
            inputStream = new BufferedReader(new FileReader(fileName));
            String line;
            boolean flagArrive = false;
            while((line = inputStream.readLine()) != null){
                String[] nodeRow = line.split("\\s+");
                //EventType eventType, int eventId, double arriveTime, double holdTime, int src, int dst, int transmissionRate
                ServiceEvent ser = new ServiceEvent();
                ServerGenerator serGen = new ServerGenerator();
                if (isFirstLine){
                    isFirstLine = false;//第一行是说明，所以读的时候要跳过
                    continue;
                }
                for (int j = 0; j < nodeRow.length; j++) {
                    switch (j){
                        case 0: {
                            if (nodeRow[j].equals("SERVICE_ARRIVAL")){
                                ser.setEventType(EventType.SERVICE_ARRIVAL);
                                flagArrive = true;
                            }else if (nodeRow[j].equals("SERVICE_END")){
                                ser.setEventType(EventType.SERVICE_END);
                                flagArrive = false;
                            }
                            break;
                        }
                        case 1: ser.eventId = Integer.parseInt(nodeRow[j]);break;
                        case 2: ser.setArriveTime(Double.parseDouble(nodeRow[j]));break;
                        case 3: ser.setHoldTime(Double.parseDouble(nodeRow[j]));break;
                        case 4: ser.setSrc(Integer.parseInt(nodeRow[j]));break;
                        case 5: ser.setDst(Integer.parseInt(nodeRow[j]));break;
                        case 6: {
                            if (flagArrive){
                                transRate = serGen.genRandomTransmissionRate(0, CommonResource.transmissionRateSet.length);
                                ser.setTransmissionRate(transRate);
                            }else{
                                ser.setTransmissionRate(transRate);
                            }
                            break;
                        }
                        default:break;
                    }
                }
                serviceEventList.add(ser);
            }
        } catch(Exception e){
            System.out.println(e);
        }

        return serviceEventList;
    }*/

}
    //code in main
   /* public static void main(String[] args) {
        Long startTime = System.currentTimeMillis();
        Map<Integer,ServiceEvent> serviceMap;
        List<Link> linkList =  new ArrayList<Link>();
        //read topo and get node and linklist
        ReadTopo topo = new ReadTopo(CommonResource.FILE_NAME);
        Network network = new Network(topo);
        network.setAllFixedNodes();
        network.setFlexNodes(CommonResource.flexNodes);
        CommonResource.nodeList = topo.getNodeList();
        CommonResource commonResource = new CommonResource();
        //Resource initiation
        for (int i = 0; i < CommonResource.nodeList.size(); i++){
            CommonResource.nodeList.get(i).initNodeResource();
        }

        linkList = topo.getLinkList();
        DefaultDirectedWeightedGraph<Node, AccessEdge> graphG = new Graph(CommonResource.nodeList,linkList).getG(); //默认有向加权图
        ServerGenerator serGen = new ServerGenerator();
        // 产生不同到达离去率的请求，TRset相同
        double rou = 340;
//        for (int i = 0; i < 1;i++){//产生没有排序的请求序列
//            ServerGenerator serverG = new ServerGenerator(CommonResource.NODE_NUMBER,0.04,rou,
//                    CommonResource.numConnectionRequest,0, CommonResource.transmissionRateSet.length);
//            List<ServiceEvent> serviceList = serverG.genEventQueueArrival();
//            String fileName;
//            fileName = "noSort"+CommonResource.numConnectionRequest+"-nodeNum"+CommonResource.NODE_NUMBER+"-mu0.04-rou"+rou+"-TR{10,40,100,120,160,200}.txt";
//            commonResource.writeSerListToTXT(fileName,serviceList);
//            rou = rou+10;
//        }
        for (int i = 0; i < 1;i++){//rou increase times
            Generator.currentTime = 0;
            List<ServiceEvent> serviceList = commonResource.readSerListFromTXT1(
                    "noSort50000-nodeNum14-mu0.04-rou200.0-TR{10,40,100,120,160,200}.txt",0.04,rou);
//            serviceList = serGen.sortEventQueue(serviceList);
            //按发生时间排序
            Collections.sort(serviceList, new EventComparator());

            String fileName;
            fileName = CommonResource.numConnectionRequest+"-nodeNum"+CommonResource.NODE_NUMBER+"-mu0.04-rou"+rou+"-TR{10,40,100,120,160,200}.txt";
            commonResource.writeSerListToTXT(fileName,serviceList);
            rou = rou+10;
        }

        //产生相同到达离去率的请求，TRset的比例不同
//        for (int i = 0; i < 7; i++){//TRset change times
//            int moreTR = 10;
//            int[] TRSet = {10,10,10,10,10,40,100,120,160,200,400};
//            switch (i){
//                case 0: {
//                    CommonResource.transmissionRateSet = Arrays.copyOf(TRSet, 11); moreTR = 10;
//                    break;
//                }
//                case 1: {
//                    TRSet = new int[]{10,40,40,40,40,40,100,120,160,200,400}; moreTR = 40;
//                    CommonResource.transmissionRateSet = Arrays.copyOf(TRSet, 11);break;
//                }
//                case 2: {
//                    TRSet = new int[]{10,40,100,100,100,100,100,120,160,200,400}; moreTR = 100;
//                    CommonResource.transmissionRateSet = Arrays.copyOf(TRSet, 11);break;
//                }
//                case 3: {
//                    TRSet = new int[]{10,40,100,120,120,120,120,120,160,200,400}; moreTR = 120;
//                    CommonResource.transmissionRateSet = Arrays.copyOf(TRSet, 11);break;
//                }
//                case 4: {
//                    TRSet = new int[]{10,40,100,120,160,160,160,160,160,200,400}; moreTR = 160;
//                    CommonResource.transmissionRateSet = Arrays.copyOf(TRSet, 11);break;
//                }
//                case 5: {
//                    TRSet = new int[]{10,40,100,120,160,200,200,200,200,200,400}; moreTR = 200;
//                    CommonResource.transmissionRateSet = Arrays.copyOf(TRSet, 11);break;
//                }
//                case 6: {
//                    TRSet = new int[]{10,40,100,120,160,200,400,400,400,400,400}; moreTR = 400;
//                    CommonResource.transmissionRateSet = Arrays.copyOf(TRSet, 11);break;
//                }
//                default:break;
//            }
//
//            List<ServiceEvent> serviceList = commonResource.readSerListFromTXT2(
//                    "noSort50000-nodeNum14-mu0.04-rou200.0-TR{10,40,100,120,160,200,400}.txt");
//            //按发生时间排序
//            Collections.sort(serviceList, new EventComparator());
//
//            String fileName;
//            fileName = CommonResource.numConnectionRequest+"-nodeNum"+CommonResource.NODE_NUMBER+"-mu0.04-rou200.0-TR{"+moreTR+"multi5}.txt";
//            commonResource.writeSerListToTXT(fileName,serviceList);
//        }

//        EventDealer eventDealer = new EventDealer(serviceList,graphG,CommonResource.nodeList,linkList);
//        eventDealer.doDeal();
//
//        serviceMap = eventDealer.getServiceMap();
////        for (int i = 0; i < serviceMap.size(); i++) {
////            System.out.println(serviceMap.get(i).get(0).flagForSuccess);
////        }
//
//       SimulationResult simulationResult = new SimulationResult(serviceMap,false);
////        SimulationResult simulationResultTree = new SimulationResult(serviceMap,true);
//        simulationResult.outputResToFile();
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
    }*/