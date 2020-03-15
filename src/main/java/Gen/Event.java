package Gen;

import Topology.Path;
import Topology.Tree;


/**
 * Created by root on 16-4-15.
 * 服务相关的事件类，包括事件类型，事件id，事件到达时间以及维持时间。
 */

//对于量子业务，没有产生离去业务的操作，与服务业务只是类型不同 id相同，而且获取发生时间的时候，只需要到达时间
public class Event {

    public Boolean flagForSuccess = false;
    public Boolean flagForSuccessTree = true;
//    public Path ownPath;
    public Path ownPath;
    private EventType eventType;
    public int eventId;
    private double arriveTime;//到达时间
    private double holdTime;//持续时间
    public Tree resultTree = null;

    public double obtainEndTime(){
        return arriveTime+holdTime;
    }

    //直接调用这个函数就可以了，对于到达业务，就是startTime，对于离去业务就是endtime
    public double obtainHappenTime(){
        if(eventType==EventType.SERVICE_END){
            return obtainEndTime();
        }else {
            return arriveTime;
        }
    }

    public Event() {
    }

    public Event(EventType eventType, int eventId, double arriveTime, double holdTime) {
        this.eventType = eventType;
        this.eventId = eventId;
        this.arriveTime = arriveTime;
        this.holdTime = holdTime;
    }

    public Event(Event e) {
        this.eventType = e.eventType;
        this.eventId = e.eventId;
        this.arriveTime = e.arriveTime;
        this.holdTime = e.holdTime;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public double getArriveTime() {
        return arriveTime;
    }

    public void setArriveTime(double arriveTime) {
        this.arriveTime = arriveTime;
    }

    public double getHoldTime() {
        return holdTime;
    }

    public void setHoldTime(double holdTime) {
        this.holdTime = holdTime;
    }

}
