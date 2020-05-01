package Common;

import AuxGraph.AuNode;
import Gen.ServiceEvent;

import static Common.CommonResource.ALGORITHM_TYPE;

public class Weights {
    //existing lightpath weight
    public double weight_ELP(AuNode srcNode, AuNode dstNode, ServiceEvent currentSer, int physHops){
        double weight = 0;
        switch (ALGORITHM_TYPE){
            case 0: {//EMG algorithm
                if (srcNode.isFixed & dstNode.isFixed){//both fixed node
                    weight = 0.001;
                }else if (!srcNode.isFixed & !dstNode.isFixed){//both flexible node
                    weight = 1.683*currentSer.transmissionRate;
                }else {//a fixed and a flexible node
                    weight = 0.8415*currentSer.transmissionRate;
                }
                break;
            }
            case 1:{//MOG
                weight = 0.09*physHops+0.0001*(physHops-1);break;
//                weight = 0.09;break;
            }
            case 2:{//MEG
                weight = 0.01*physHops+0.0001*(physHops-1);break;
//                weight = 0.01;break;
            }
            case 3:{//MVG algorithm: 100+0.01H+0.0001(H-1)
                weight = 100+0.01*physHops+0.0001*(physHops-1);break;
            }
            default:break;
        }
        return weight;
    }
    public double weight_PLP(){//auLinkT1toR2,potential lightpath edge
        double weight = 0;
        switch (ALGORITHM_TYPE){
            case 0: {//EMG algorithm
                weight = 0.001;break;
            }
            case 1:{//MOG
                weight = 0.01;break;
            }
            case 2:{//MEG
                weight = 0.09;break;
            }
            case 3:{//MVG
                weight = 0.01;break;
            }
            default:break;
        }
        return weight;
    }
    public double weight_TxRx(AuNode auNode,int transmissionRate){
        double weight = 0;
        switch (ALGORITHM_TYPE){
            case 0: {//EMG algorithm
                weight = setWeightofOTRI(auNode,transmissionRate);break;
            }
            case 1:{//MOG
                weight = setWeighofOTRI_MOGorMEG(auNode,transmissionRate);break;
            }
            case 2:{//MEG
                weight = setWeighofOTRI_MOGorMEG(auNode,transmissionRate);break;
            }
            case 3:{//MVG
                weight = 50;break;
            }
            default:break;
        }
        return weight;
    }
    private double setWeightofOTRI(AuNode auNode,int transmissionRate){
        double weightO1toT1; //the same as weightR1toI1
        if (auNode.isFixed){
            weightO1toT1 = 188;//185.5+102.5
        }else {
            if (auNode.isSub){
                weightO1toT1 = 0.8415*transmissionRate;
            }else {
                weightO1toT1 = 0.8415*transmissionRate+325.6665;//0.8415*transmissionRate+45.6665+280
            }
        }
        return weightO1toT1;
    }
    private double setWeighofOTRI_MOGorMEG(AuNode auNode,int transmissionRate){
        double weightO1toT1; //the same as weightR1toI1
        if (auNode.isFixed){
            weightO1toT1 = 100;
        }else {
            if (auNode.isSub){
                weightO1toT1 = 0.001;
            }else {
                weightO1toT1 = 100;
            }
        }
        return weightO1toT1;
    }
    private double setWeighofOTRI_MVH(AuNode auNode,int transmissionRate){
        double weightO1toT1; //the same as weightR1toI1
        if (auNode.isFixed){
            weightO1toT1 = 50;
        }else {
            if (auNode.isSub){
                weightO1toT1 = 50;
            }else {
                weightO1toT1 = 50;
            }
        }
        return weightO1toT1;
    }
}
