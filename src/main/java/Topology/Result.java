package Topology;

public class Result {
    public double rou;//业务量
    public int count;//建立成功的总数
    public int sum;//业务总数
    public double energyConsumed;//消耗的总能耗
    public double totalVirHops;//总虚拟跳数
    public double totalPhysHops;//总物理跳数

    public Result(double rou, int count, int sum, double energyConsumed, double totalVirHops, double totalPhysHops){
        this.rou = rou;
        this.count = count;
        this.sum = sum;
        this.energyConsumed = energyConsumed;
        this.totalVirHops = totalVirHops;
        this.totalPhysHops = totalPhysHops;
    }
}
