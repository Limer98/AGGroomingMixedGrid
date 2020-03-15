package Gen;

import Common.CommonResource;
import Utils.Pair;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * ҵ���������࣬����Poissionģ��
 * Created by root on 16-4-14.
 */
public class ServerGenerator extends Generator{
    // node number in graph
    private int nodesNum;
    private double mu;//��ȥ��
    private double rou;//ҵ����
    // service number need to be generated
    private int serviceNum;//����ҵ������
    // in each service, the min and max wavelength number
    private int selectNumMin;
    private int selectNumMax;

    private void genEvent(int id){
        double arriveTime = genArrivalTime(mu*rou);
        double holdTime = genHoldTime(mu);
        //���������ԭ�޽ڵ�
        Pair<Integer, Integer> srcDst = genRandomSrcDst();
        int transmissionRate = genRandomTransmissionRate(selectNumMin,selectNumMax);
        //��ͨҵ��ﵽ
        ServiceEvent event = new ServiceEvent(EventType.SERVICE_ARRIVAL, id, arriveTime,
                holdTime, srcDst.getFirst(), srcDst.getSecond(), transmissionRate);
        eventQueue.add(event);

        //������ȥҵ��
        ServiceEvent eventEnd = new ServiceEvent(event);
        eventEnd.setEventType(EventType.SERVICE_END);
        eventQueue.add(eventEnd);

        currentTime = arriveTime;
    }

    /**
     * generate random source node and destination node by using identifier to mark node.
     * @return Pair
     */

    //�õ�1-NodeNUMber֮������ԭ�޽ڵ�
    private Pair<Integer, Integer> genRandomSrcDst(){
        int src = (int)(Math.random()*nodesNum+1);
        int dst = src;
        do {
            dst = (int)(Math.random()*nodesNum+1);
        }while(dst == src);//������dst��src�����
        return new Pair<Integer, Integer>(src, dst);
    }

    /**
     * generate int number ranged [min, max)
     * @param min
     * @param max
     * @return
     */

    //select transmissionRate
    private int genRandomTransmissionRate(int min, int max){ //transmissionRateSet����ı߽� 1-4
        int m = genRandomInt(min, max);
        return CommonResource.transmissionRateSet[m];
    }

    /**
     * generate event queue according to mu, rou, and other vars.
     * @return
     */
    public List<ServiceEvent> genEventQueue(){
        //�Ȱ�����ҵ�����ɷ����б�����,ÿ��ѭ����id��ӦΪi������ҵ�񶼷Ž�ȥ
        for(int i=0; i<serviceNum; i++){
            genEvent(i);
        }
        //���б��������
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