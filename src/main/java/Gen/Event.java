package Gen;

import Topology.Path;
import Topology.Tree;


/**
 * Created by root on 16-4-15.
 * ������ص��¼��࣬�����¼����ͣ��¼�id���¼�����ʱ���Լ�ά��ʱ�䡣
 */

//��������ҵ��û�в�����ȥҵ��Ĳ����������ҵ��ֻ�����Ͳ�ͬ id��ͬ�����һ�ȡ����ʱ���ʱ��ֻ��Ҫ����ʱ��
public class Event {

    public Boolean flagForSuccess = false;
    public Boolean flagForSuccessTree = true;
//    public Path ownPath;
    public Path ownPath;
    private EventType eventType;
    public int eventId;
    private double arriveTime;//����ʱ��
    private double holdTime;//����ʱ��
    public Tree resultTree = null;

    public double obtainEndTime(){
        return arriveTime+holdTime;
    }

    //ֱ�ӵ�����������Ϳ����ˣ����ڵ���ҵ�񣬾���startTime��������ȥҵ�����endtime
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
