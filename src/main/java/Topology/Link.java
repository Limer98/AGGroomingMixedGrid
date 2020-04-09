package Topology;

import Gen.ServiceEvent;
import Utils.Pair;

import java.util.*;

/**
 * @auther：sherrystar
 * @package_name: Topology
 * @create_time: 2019/1/17
 * @describe: Link只包括源节点，宿节点和weight
 */
public class Link {
    public int srcSeq; //源点
    public int dstSeq;
    public double weight;
//    public List<ServiceEvent> serviceOnLink = new ArrayList<ServiceEvent>();
    public HashMap<Integer,ServiceEvent> serviceOnLink = new HashMap<Integer,ServiceEvent>();//key is physHops on the Link
    private boolean[] slots; //slot的占用情况
//    public boolean containFixed = false;

    public Link(int srcSeq,int dstSeq,int numSlots,double weight) {
        this.srcSeq = srcSeq;
        this.dstSeq = dstSeq;
        this.weight = weight;

        //未被占用的slots，默认false;
        slots = new boolean[numSlots];
        for (int i = 0; i<numSlots; i++){
            slots[i] = false;
        }
    }

    public Link(int srcSeq, int dstSeq, double weight) {
        this.srcSeq = srcSeq;
        this.dstSeq = dstSeq;
        this.weight = weight;
    }

    public Link(int srcSeq, int dstSeq) {
        this.srcSeq = srcSeq;
        this.dstSeq = dstSeq;
        this.weight = 1; 
    }

    public int getSrcSeq() {
        return srcSeq;
    }

    public int getDstSeq() {
        return dstSeq;
    }

    public double getWeight() {
        return weight;
    }

    public void setSrcSeq(int srcSeq) {
        this.srcSeq = srcSeq;
    }

    public void setDstSeq(int dstSeq) {
        this.dstSeq = dstSeq;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Link link = (Link) o;

        if (srcSeq != link.srcSeq) return false;
        if (dstSeq != link.dstSeq) return false;
        return Double.compare(link.weight, weight) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = srcSeq;
        result = 31 * result + dstSeq;
        temp = Double.doubleToLongBits(weight);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    /*** include slots ***/
    //复制一条链路
    public Link (Link another){
        this.srcSeq = another.getSrcSeq();
        this.dstSeq = another.getDstSeq();
        this.weight = another.getWeight();
        this.slots = another.getSlots();
    }

    public boolean[] getSlots(){
        boolean[] slotsCopy = new boolean[slots.length];
        System.arraycopy(slots, 0, slotsCopy, 0, slots.length);
        return slotsCopy;
    }

    //将slots的状态变为 空闲（false）
    public void reset(){
        for (int i=0; i<slots.length; i++){
            slots[i] = false;
        }
    }

    //复制当前slots的状态
    public void setSlots(boolean[] slots){
        System.arraycopy(slots, 0, this.slots, 0, slots.length);
    }

    //按位或，并赋值给mask，两条链路，只要其中一个被占用，则两个链路都标为被占用。
    public void mask(boolean[] slots){
        for (int i=0; i<slots.length; i++){
            this.slots[i] |= slots[i];
        }
    }

    //两条链路，其中一个被占用，则释放对应的频谱隙资源（false）
    public void unmask(boolean[] slots){
        for (int i=0; i<slots.length; i++){
            this.slots[i] &= (!slots[i]);//先取非(true变false)后按位与并赋值给unmask
        }
    }

    public static void main(String[] args){
        Link a = new Link(1,14,10,1);
        Link b = new Link(a);
        List<Link> linkList = new ArrayList<Link>();
        linkList.add(a);
        linkList.add(b);
    }
}
