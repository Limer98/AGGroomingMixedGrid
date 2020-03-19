package Topology;

import Gen.CoSerEG;
import Utils.Pair;

import java.util.HashMap;
import java.util.List;

public class ResourceStatus { //record capacity consumption

    /******* new *******/
    public int transID;
    public int TxResource; //unit is the resource slot;
    public int RxResource;
//    HashMap<Integer,ConsumCapType> ResLogger = new HashMap<Integer,ConsumCapType>();//eventId,..
    HashMap<Integer,List<ConsumCapType>> ResLogger = new HashMap<Integer,List<ConsumCapType>>();//eventId,..
    /******* --- *******/

    public ResourceStatus(int transID,int TxResource,int RxResource){//new ResourceStatus
        this.transID = transID;
        this.TxResource = TxResource;
        this.RxResource = RxResource;
    }


    public ResourceStatus(ResourceStatus resStatus){//new ResourceStatus
        this.transID = resStatus.transID;
        this.TxResource = resStatus.TxResource;
        this.RxResource = resStatus.RxResource;
        this.ResLogger = resStatus.ResLogger;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResourceStatus resourceStatus = (ResourceStatus) o;

        return transID == resourceStatus.transID;
    }

    @Override
    public int hashCode() {
        return transID;
    }

}

class ConsumCapType{
    public int consumNums;//量化后的占用的个数，如果正电疏导，则为0
    public int consumCap;//实际的容量Gbps
    public boolean isTx;
    public HashMap<Integer, CoSerEG> coServiceEG;//合作的业务请求electrical grooming,id and transmission rate
    public int consumNumsEG;//电疏导结束后，量化后的消耗个数
    public int clubEG = -1;
    public int splitID = -1;

    public ConsumCapType(int consumNums,int consumCap,boolean isTx){
        this.consumNums = consumNums;
        this.consumCap = consumCap;
        this.isTx = isTx;
    }
    public ConsumCapType(int consumNums,int consumCap,boolean isTx,int consumNumsEG){
        this.consumNums = consumNums;
        this.consumCap = consumCap;
        this.isTx = isTx;
        this.consumNumsEG = consumNumsEG;
    }
    public ConsumCapType(int consumNums,int consumCap,boolean isTx,int consumNumsEG,int splitID){
        this.consumNums = consumNums;
        this.consumCap = consumCap;
        this.isTx = isTx;
        this.consumNumsEG = consumNumsEG;
        this.splitID = splitID;
    }

    public void setConsumNumsEG(int consumNumsEG) {
        this.consumNumsEG = consumNumsEG;
    }
    public int getConsumNumsEG(){
        return this.consumNumsEG;
    }
}
