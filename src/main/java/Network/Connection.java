package Network;

import Gen.ServiceEvent;
import Topology.Path;

public class Connection {

    public Path route;
//    private boolean[] mask;
    private ServiceEvent request;

    public boolean[] spectrumAssignment; //频谱分配；长度为320的数组
    private int start; //频谱隙的开始
    private int end; //频谱隙的结尾

    public Connection(ServiceEvent request, Path route, boolean[] spectrumAssignment){
        this.request = request;
        this.route = route;
        this.spectrumAssignment = spectrumAssignment;

        //确定频谱分配起点start和终点end
        for (int i = 0; i < spectrumAssignment.length; i++){
            if (spectrumAssignment[i] == true){
                start = i;
                break;
            }
        }

        for (int i = start; i < spectrumAssignment.length; i++){
            if (spectrumAssignment[i] == false){
                end = i-1;
                return;
            }
        }
        end = spectrumAssignment.length-1;
    }

    //复制一个连接
    public Connection(Connection another){
        this.route = another.route;
        this.request = another.request;
        this.spectrumAssignment = another.copySpectrumAssignment();
        this.start = another.getStart();
        this.end = another.getEnd();
    }

    public ServiceEvent getRequest() { return request; }

    public boolean[] copySpectrumAssignment(){
        boolean[] copy = new boolean[spectrumAssignment.length];
        System.arraycopy(spectrumAssignment, 0, copy, 0, spectrumAssignment.length);
        return copy;
    }

    public void setStart(int start){ this.start = start; }
    public int getStart(){ return start; }
    public void setEnd(int end){ this.end = end; }
    public int getEnd(){ return end; }
}
