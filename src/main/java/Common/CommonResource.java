package Common;

import Gen.EventType;
import Gen.ServiceEvent;
import Topology.Link;
import Topology.Node;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @auther：sherrystar
 * @package_name: Common
 * @create_time: 2019/1/17
 * @describe:
 */
public class CommonResource {

   public static int NODE_NUMBER = 14;
   public static String FILE_NAME = "NSFnet.txt";
   public static int[] flexNodes = {2, 6, 10, 12, 13, 5, 9, 1, 14, 3, 11};//other nodes are fixedNodes
//{1,2,3,5,6,9,10,11,12,13}
//   public static int[] flexNodes = {};
   public static double CURRENT_TIME = 0;
   public static Integer[][] defaultTopo;
   public static Integer[][] defaultResource;

   public static int numConnectionRequest = 50;
   public static int numSlots = 320; //1THz. granularity of FS is 12.5GHz(flex)
   public static int[] transmissionRateSet = {115}; //Gbps [10,2x-10]

   //A 400Gbps sliceable optical transponder can be divided into three sub-transponders.
//   public static int numIPPortPerNode = 15;
//   public static int numTransponderPerNode = 15; //100Gbps 15
   public static int numNSTransponderPerNode = 100; //100Gbps 15
   public static int numFSTransponderPerNode = 100; //400Gbps 15
   public static int numSubTransponderPerT = 8;//400Gbps/50Gbps=8
//   public static int GbpsIPPort = 400;
   public static int GbpsNSTransponder = 100;
   public static int GbpsFSTransponder = 400;
   public static double GbpsSubFSTrans = (double)GbpsFSTransponder/(double)numSubTransponderPerT;

   public static int[][] topo;
   public static List<Node> nodeList;
   public static Map<Integer,ServiceEvent> ongoingServiceMap = new HashMap<Integer,ServiceEvent>();//eventID,s
   public static List<Link> virLinkList;

   public static int KeyCyclePeriod = 10;
   public static double ShiftTimeForInsert = 0.00000001;
   public static int[][] keyResource;
   public static int[][] keyResourceTree;
   public static double genKeysPerTime = 4;
   public static int themostKeys = 1000;

   public static List<Integer> genNewSameList(List<Integer> o){
      List<Integer> m = new ArrayList<Integer>();
      for (int i = 0; i < o.size(); i++) {
         m.add(o.get(i));
      }
      return m;
   }

/*   public static int calcuRequestResNum(boolean isFixedNode, int transmissionRate){ //还未判断路径上有无固定节点，要建几条光路
      int requestResNum;
      if (isFixedNode){
         requestResNum = 1;
      }else{
         requestResNum = (int)Math.ceil((double)transmissionRate/CommonResource.GbpsSubFSTrans);
      }
      return requestResNum;
   }*/

   public static int calcuRequestResNum(boolean isFixedNode, int transmissionRate){ //还未判断路径上有无固定节点，要建几条光路
      int requestResNum;
      if (isFixedNode){
         requestResNum = (int)Math.ceil((double)transmissionRate/CommonResource.GbpsNSTransponder);
      }else{
         requestResNum = (int)Math.ceil((double)transmissionRate/CommonResource.GbpsSubFSTrans);
      }
      return requestResNum;
   }

   public static double calcuResourceSlot(boolean isFixedNode){
      double resourceSlot;

      if (isFixedNode){
         resourceSlot = GbpsNSTransponder;
      }else{
         resourceSlot = GbpsSubFSTrans;
      }

      return resourceSlot;
   }

   public static List<ServiceEvent> readSerListFromTXT(String fileName){
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
   public static void writeSerListToTXT(String fileName,List<ServiceEvent> serList){
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
}
