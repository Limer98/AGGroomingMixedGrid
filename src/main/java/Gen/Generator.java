package Gen;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 16-4-14.
 * ҵ�������ĳ������
 */
public class Generator {
    public double currentTime=0;//static is better?
    public List<ServiceEvent> eventQueue = new ArrayList<ServiceEvent>();

    /**
     * ����[a��b)֮����������
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
     * ����[a��b)֮������������
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
     * ���ɷֲ��ļ��ʱ�����ָ���ֲ�������Ϊ1/�ˣ�
     * ָ���ֲ��ĸ��ʷֲ�������F(x) = 1 - �ˡ�e^(-��x)
     * ͨ����任��x = - 1 / �� �� ln(1-y)
     * ��Ϊ1-U��U�Ƿ���[0��1)���ȷֲ������������Ҳ���Ӿ��ȷֲ�������x = - 1 / �� �� ln(u), u != 0 && u != 1.
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
