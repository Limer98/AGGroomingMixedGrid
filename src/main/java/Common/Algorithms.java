package Common;

public class Algorithms {

    private static int M = 4; //4*12.5=100Gbps
    //满足频谱连续性
    public static int firstFit_AllFlexNodes(boolean[] commonSlots, int bandwidth){
        int numSlots = commonSlots.length;
        int freeSlots = 0;
        for (int i = 0; i < numSlots; i++){
            if (commonSlots[i] == true){
                if(freeSlots == 0){
                    continue;
                }
                freeSlots = 0;
                continue;
            }
            freeSlots++;
            if (freeSlots >= bandwidth){
                return i-bandwidth+1;
            }//找到连续可用的空闲频谱块
        }
        if (freeSlots >= bandwidth){
            return numSlots-freeSlots;
        }//？？？？
        return numSlots;//未找到连续可用的空闲频谱块
    }

    public static int firstFit_FromFlexGoThroughFixed(boolean[] commonSlots, int bandwidth){
        int numSlots = commonSlots.length;
        int freeSlots = 0;
        for (int i = 0; i < numSlots; i++){
            if (commonSlots[i] == true){
                if (freeSlots == 0){
                    continue;
                }
                freeSlots = 0;
                continue;
            }
            freeSlots ++;
            if (i%M  == 0){
                freeSlots = 1; //????
            }
            if (freeSlots >= bandwidth){
                return i-bandwidth+1;
            }
        }
        if(freeSlots >= bandwidth){
            return numSlots-freeSlots;
        }
        return numSlots;
    }

    public static int firstFit_FromFixed(boolean[] commonSlots, int bandwidth){
        int numSlots = commonSlots.length;
        int freeSlots = 0;
        for(int i = 0; i < numSlots; i++){
            if(commonSlots[i] == true){
                if(freeSlots == 0){
                    continue;
                }
                freeSlots = 0;
                continue;
            }
            if(i%M == 0 || freeSlots > 0){
                if(freeSlots >= bandwidth){
                    freeSlots++;
                    return i-bandwidth+1;
                }
            }
            else continue;
        }
        if(freeSlots >= bandwidth){
            return numSlots-freeSlots;
        }
        return numSlots;
    }
}
