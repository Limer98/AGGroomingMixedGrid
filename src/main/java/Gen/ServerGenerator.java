package Gen;

import Common.CommonResource;
import Utils.Pair;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 业务发生器的类，采用Poission模型
 * Created by root on 16-4-14.
 */
public class ServerGenerator extends Generator{
    // node number in graph
    private int nodesNum;
    private double mu;//离去率
    private double rou;//业务量
    // service number need to be generated
    private int serviceNum;//仿真业务总数
    // in each service, the min and max wavelength number
    private int selectNumMin;
    private int selectNumMax;

    private void genEvent(int id){
        double arriveTime = genArrivalTime(mu*rou);
        double holdTime = genHoldTime(mu);
        //生成随机的原宿节点
        Pair<Integer, Integer> srcDst = genRandomSrcDst();
        int transmissionRate = genRandomTransmissionRate(selectNumMin,selectNumMax);
        //普通业务达到
        ServiceEvent event = new ServiceEvent(EventType.SERVICE_ARRIVAL, id, arriveTime,
                holdTime, srcDst.getFirst(), srcDst.getSecond(), transmissionRate);
        eventQueue.add(event);

        //生成离去业务
        ServiceEvent eventEnd = new ServiceEvent(event);
        eventEnd.setEventType(EventType.SERVICE_END);
        eventQueue.add(eventEnd);

        currentTime = arriveTime;
    }

    /**
     * generate random source node and destination node by using identifier to mark node.
     * @return Pair
     */

    //得到1-NodeNUMber之间的随机原宿节点
    private Pair<Integer, Integer> genRandomSrcDst(){
        int src = (int)(Math.random()*nodesNum+1);
        int dst = src;
        do {
            dst = (int)(Math.random()*nodesNum+1);
        }while(dst == src);//结束后dst和src不相等
        return new Pair<Integer, Integer>(src, dst);
    }

    /**
     * generate int number ranged [min, max)
     * @param min
     * @param max
     * @return
     */

    //select transmissionRate
    private int genRandomTransmissionRate(int min, int max){ //transmissionRateSet数组的边界 1-4
        int m = genRandomInt(min, max);
        return CommonResource.transmissionRateSet[m];
    }

    /**
     * generate event queue according to mu, rou, and other vars.
     * @return
     */
    public List<ServiceEvent> genEventQueue(){
        //先把所有业务生成放在列表里面,每次循环把id对应为i的所有业务都放进去
        for(int i=0; i<serviceNum; i++){
            genEvent(i);
        }
        //对列表进行排序
        Collections.sort(eventQueue, new EventComparator());
        return eventQueue;
    }


    public ServerGenerator(int nodesNum, double mu, double rou, int serviceNum, int selectNumMin, int selectNumMax) {
        this.nodesNum = nodesNum;
        this.mu = mu;
        this.rou = rou;
        this.serviceNum = serviceNum;
        this.selectNumMin = selectNumMin;
        this.selectNumMax = selectNumMax;
    }

    /**
     * usage demo
     * @param args no sense.
     */

    //average holding time = 1/mu
    public static void main(String[] args){
        ServerGenerator generator = new ServerGenerator(20, 0.04,3,100,0,4);
        List<ServiceEvent> queue = generator.genEventQueue();

        ServiceEvent s;
        for (int i = 0; i < queue.size(); i++) {
            s = queue.get(i);
            System.out.println(s.getEventId()+" "+s.obtainHappenTime()+" "+s.getEventType().toString()+" "+s.getSrc()+"-"+s.getDst()+" TR:"+s.getTransmissionRate());

        }
    }
}

/**
 * Comparator class for sorting List<Event>
 */
class EventComparator implements Comparator<Event> {
    public int compare(Event o1, Event o2) {
        double time1 = o1.obtainHappenTime();
        double time2 = o2.obtainHappenTime();
        Double dou = time1;
        return dou.compareTo(time2);
    }
}