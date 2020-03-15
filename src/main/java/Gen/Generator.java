package Gen;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 16-4-14.
 * 业务发生器的抽象基类
 */
public class Generator {
    public double currentTime=0;//static is better?
    public List<ServiceEvent> eventQueue = new ArrayList<ServiceEvent>();

    /**
     * 生成[a，b)之间的随机整数
     * @param a
     * @param b
     * @return
     */
    protected int genRandomInt(int a, int b){
        int c = b-a;
        double m = Math.random();
        return a+ (int)(c*m);
    }

    /**
     * 生成[a，b)之间的随机浮点数
     * @param a
     * @param b
     * @return
     */
    protected double genRandomDouble(double a, double b){
        double c = b-a;
        double m = Math.random();
        return a+m*c;
    }

    /**
     * 泊松分布的间隔时间服从指数分布（期望为1/λ）
     * 指数分布的概率分布函数：F(x) = 1 - λ·e^(-λx)
     * 通过逆变换，x = - 1 / λ · ln(1-y)
     * 因为1-U（U是服从[0，1)均匀分布的随机变量）也服从均匀分布，所以x = - 1 / λ · ln(u), u != 0 && u != 1.
     * @param beta
     * @return
     */
    protected double genExponentDistributionRandom(double beta){
        double u,x;
        do {
            u = Math.random();
        }while(u==0);

        x = -1/beta* Math.log(u);
        return x;
    }

    protected double genArrivalTime(double time){
        return currentTime+genExponentDistributionRandom(time);
    }

    protected double genHoldTime(double time){
        return genExponentDistributionRandom(time);
    }

    public Generator() {

    }
}
