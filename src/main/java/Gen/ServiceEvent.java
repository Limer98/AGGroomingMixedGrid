package Gen;

import AuxGraph.AuNode;
import AuxGraph.AuxPath;
import Topology.ResourceStatus;
import Utils.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 16-4-15.
 *
 */
public class ServiceEvent extends Event implements Cloneable {

    public int src;
    public int dst;
    public int transmissionRate;//the same transmissionRate, maybe different requiredSlotsNum(flex/fixed)

//    public int requestResNum;
    public AuxPath ownAuPath;
    public int bandwidth;
    public int splitNum = 0; //建立多通道
    public int splitID = 0; //对于多通道，标志为第几个子业务
    public List<ServiceEvent> subSerList;
    public List<AuNode> OEOLogger;//record O-E-O node:nodeID,used transponder resourceStatus
    public double energyConsumed;
    public int virHops = 0;//该请求的虚拟跳数
    public int physHops = 0;//该请求的物理跳数

    public void setVirHops(){
        this.virHops = this.OEOLogger.size()/2;
    }
    public int getVirHops(){
        return this.virHops;
    }

    public int getSrc() {
        return src;
    }
    public void setSrc(int src) {
        this.src = src;
    }

    public int getDst() {
        return dst;
    }
    public void setDst(int dst) {
        this.dst = dst;
    }

    public int getTransmissionRate(){ return transmissionRate; }
    public void setTransmissionRate(int transmissionRate){ this.transmissionRate = transmissionRate; }

    public ServiceEvent(EventType eventType, int eventId, double arriveTime, double holdTime, int src, int dst, int transmissionRate) {
        super(eventType, eventId, arriveTime, holdTime);
        this.src = src;
        this.dst = dst;
        this.transmissionRate = transmissionRate;
    }

    public ServiceEvent(){

    }

/*    public ServiceEvent(EventType eventType, int eventId, double arriveTime, double holdTime, int src, List<Integer> dst, int requiredWaveNum) {
        super(eventType, eventId, arriveTime, holdTime);
        this.src = src;
        this.dst = dst;
        this.requiredKeys = requiredWaveNum;
    }

    public ServiceEvent(Event e, int src, int dst, int requiredKeys) {
        super(e);
        this.src = src;
        this.dst = dst;
        this.requiredKeys = requiredKeys;
    }*/

    public void setBandwidth(int bandwidth){ this.bandwidth = bandwidth; }

    public ServiceEvent(ServiceEvent e) {
        super(e.getEventType(),e.getEventId(),e.getArriveTime(),e.getHoldTime());
        this.src = e.src;
        this.dst = e.dst;
        this.transmissionRate = e.transmissionRate;

        //this.requiredKeys = e.requiredKeys;
    }
    public ServiceEvent(ServiceEvent e,int transmissionRate) {
        super(e.getEventType(),e.getEventId(),e.getArriveTime(),e.getHoldTime());
        this.src = e.src;
        this.dst = e.dst;
        this.transmissionRate = transmissionRate;

        //this.requiredKeys = e.requiredKeys;
    }

}
//class CoSerEG{
//    int coSerID;
//    int transmission;
//    public CoSerEG(int coSerID,int transmission){
//        this.coSerID = coSerID;
//        this.transmission = transmission;
//    }
//}
