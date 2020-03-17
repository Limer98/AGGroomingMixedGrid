package Common;

import Gen.ServiceEvent;
import Topology.Link;
import Topology.Node;

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

   public static double CURRENT_TIME = 0;
   public static Integer[][] defaultTopo;
   public static Integer[][] defaultResource;

   public static int numConnectionRequest = 1000;
   public static int numSlots = 320; //1THz. granularity of FS is 12.5GHz(flex)
   public static int[] transmissionRateSet = {25,50,150,200}; //Gbps
   //A 400Gbps sliceable optical transponder can be divided into three sub-transponders.
//   public static int numIPPortPerNode = 15;
//   public static int numTransponderPerNode = 15; //100Gbps 15
   public static int numNSTransponderPerNode = 15; //100Gbps 15
   public static int numFSTransponderPerNode = 15; //400Gbps 15
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
}
