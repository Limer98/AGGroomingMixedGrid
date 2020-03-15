package Topology;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.KShortestPaths;
import org.jgrapht.alg.VertexCovers;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 1 on 2016/4/15.
 * 需求：业务请求——算路、判断占用、分配波长、计算占用波长数
 * 1. calculatePath函数（List<Graph<Node,Node>>）——计算最短路、次短路，k次短路
 * 2. domainWaveCalculate函数——对域进行判断，再调用checkUsed函数
 *    (1) subsecondThirdWave1/2函数（int 统计数）
 *          (1.1) subistheSam1/2函数
 * 3. checkUsed函数（boolean）——判断请求带宽波长是否被占用，在domainWaveCalculate函数
 *    (1) oneEdgeEnough函数（int波长编号）——判断最短路是否有波长资源，如有返回所在波长编号，没有寻找次短路
 *    (2) secondThirdWave函数（int 统计数）——判断次短路、k次短路对最短路返回的波长编号是否都有波长资源满足，返回int
 *                                             在checkUsed函数中判断，统计数=k，则说明三条边都满足该波长编号
 *                                             统计数！=k，最短路编号++，再次判断次短路，循环。
 *                                             循环完之后，还是没有找到满足的，进入次短路
 *        (1.1) istheSam函数——判断secondThirdWave函数中波长是否一致
 * 4. markOccupiedWavelength函数——对已经占用的波长进行标记，在checkUsed函数中
 *
 */

public class PathCalculate {
    //全局变量
    public List<Node> vertex;          //List名为vertex，它的内容是Node类的几个属性,全局变量
    public List<Link> edge;
    public int serviceID;              //申请资源的业务ID

    //构造函数——初始化全局变量
    public PathCalculate(List<Node> Nodes, List<Link> Links, int eventID){
        vertex = Nodes;
        edge = Links;
        serviceID = eventID;
    }


    //方法1：D算路,Path类型（后有class Path）,入参为图g/g1、源节点、目的节点；返回k条最短路径，形式List<GraphPath<Node, Node>>
    public Path calculatePath(DefaultDirectedWeightedGraph ggg, int Source, int Dest) {
        Path pa = new Path();
        DijkstraShortestPath<Node, AccessEdge> dsp = new DijkstraShortestPath<Node, AccessEdge>(ggg,vertex.get(Source-1),vertex.get(Dest-1)); //vertex数组是从0开始
        GraphPath<Node, AccessEdge> p = dsp.getPath();
//        pa.nodes.add(p); //计算出来的路径
        pa.gPath = p;
        return pa;
    }

/*    public Path calculatePath(DefaultDirectedWeightedGraph ggg, int Source, int Dest) {

        DijkstraShortestPath<Node, AccessEdge> dsp = new DijkstraShortestPath<Node, AccessEdge>(ggg,vertex.get(Source-1),vertex.get(Dest-1)); //vertex数组是从0开始
        GraphPath<Node, AccessEdge> p = dsp.getPath();
//        pa.nodes.add(p); //计算出来的路径
        Path pa = new Path(p);
//        pa.gPath = p;
        return pa;
    }*/



/*    public Path calculatePath(DefaultDirectedWeightedGraph ggg, int Source, List<Integer> Dest) {
        Path pa = new Path();

        for (int i = 0; i < Dest.size(); i++) {
            DijkstraShortestPath<Node, AccessEdge> dsp = new DijkstraShortestPath<Node, AccessEdge>(ggg,vertex.get(Source-1),vertex.get(Dest.get(i)-1));

            GraphPath<Node, AccessEdge> p = dsp.getPath();
//            pa.nodes.add(p);
            pa.gPath = p;
        }


        return pa;
    }*/


//    //方法3：根据请求的带宽判断是否被占用,满足一致性,入参是K最短路径（pa）、请求带宽和第几条路径；
//    public boolean checkUsed(List<AccessEdge>  edgeList, int band) {
//        int bandwave = band;
//        int searchwaveNum;     //函数oneEdgeEnough返回值
//        int sameNum=0;        //记录每条边是否都满足波长区间
//        int unedegNum=0;       //记录波长遍历完后仍然没找到的不可用的数
//        boolean result = false; // 返回值
//        //先获取第x条路径的第一条边
//        int a = edgeList.get(0).getSource().identifier;
//        int b = edgeList.get(0).getDest().identifier;
//        //取符合第一条边带宽请求的波长编号
//        for (int i=1;i<=80-bandwave+1;i++){
//            searchwaveNum = oneEdgeEnough(a, b, band,i); //返回的波长编号
//            if (searchwaveNum == -1) {
//                return false;//第一条路径不满足带宽请求，寻找下一条路径
//                } else {
//                    //这个地方取出来的是属于2行的一整条路径（最短路、次短路。。。）
//                    //判断第二条、第三条边是否满足，满足sanmeNum+1
//                    sameNum=sameNum+1;
//                    sameNum=sameNum+secondThirdWave(searchwaveNum,bandwave,edgeList);
//                    if (sameNum==edgeList.size()){
//                        markOccupiedWavelength(searchwaveNum, bandwave,subSplited);
//                        return true;
//                    }else
//                        unedegNum=unedegNum+1;
//                }
//            }
//            if (unedegNum==80-bandwave+1){  //如果80-bandware+1次都没有找到的话，就换次短路
//               result=false;
//            }
//        return result;
//    }
//
//
//    //方法4：checkUsed函数调用，判断第一条（最短路径）边上的波长资源是否存在,返回波长编号
//    public int oneEdgeEnough(int identifiera,int identifierb,int band,int searchNumStart){
//        int bandwave = band;
//        int v = 0; //记录波长满足后波长所属编号
//        //遍历所有Link
//        int z=0;
//        //遍历edge，找到a，b对应的边，从头判断波长是否占用
//        do {
//            int f =0;   //记录被占用的连续波长数
//            if (edge.get(z).srcSeq == identifiera && edge.get(z).dstSeq == identifierb) {
//                //判断一致性，是否被占用
//                for (int n=searchNumStart; n <=80-bandwave+1; n++) {   //n一直加，但要保持bandware的距离，不然总带宽会超过80
//                    if (edge.get(z).wavelengths.get(n).isUsed) {
//                        v=0;
//                    }else{
//                        f=f+1;
//                        if (f==bandwave){
//                            v=n;   //所在波长编号
//                            return v-bandwave+1;
//                        }
//                    }
//                }
//                break;
//            }
//            z++;
//        }while (z < edge.size());
//        return -1;
//    }
//
//    //方法5：checkUsed函数调用，对第一条路径的第二、三条边进行判断，是否和第一条边波长一致
//    public int secondThirdWave(int searchNum ,int bandwave,List<AccessEdge> edgeList){
//        int sameNum=0;
//        boolean result;        //函数istheSame返回值（判断后面的边和第一条是否波长一致）
//        for (int y = 1; y < edgeList.size(); y++) {
//            int c = edgeList.get(y).getSource().identifier;
//            int d = edgeList.get(y).getDest().identifier;
//            result=istheSame(searchNum, bandwave, c, d);
//            if (result==true){
//                sameNum=sameNum+1;
//            }
//        }
//        return sameNum;
//    }
//
//
//
//    //方法7：判断波长能用之后，对波长isUsed进行占用
//    public void markOccupiedWavelength(int searchNum,int banware,List<Integer> subSplited){
//        int searchNum1=searchNum+banware;//TODO 这里好像有点问题！！
//        //k条路径进行遍历标记
//        for (int y = 0; y < subSplited.size()-1; y++) {
//            int c = subSplited.get(y);
//            int d = subSplited.get(y+1);
//            //找到对应的边,并进行标记
//            for (int z=0;z<edge.size();z++) {
//                if (edge.get(z).srcSeq == c && edge.get(z).dstSeq == d) {
//                    for (int n = searchNum; n < searchNum1; n++) {
//                        edge.get(z).wavelengths.get(n).isUsed = true;  //标记已被占用
//                        edge.get(z).wavelengths.get(n).waveserviceID=serviceID;     //标记占用该波长的业务
//                    }
//                }
//                /*else if(edge.get(z).srcSeq == d && edge.get(z).dstSeq == c){
//                    for (int n = searchNum; n < searchNum1; n++) {
//                        edge.get(z).wavelengths.get(n).isUsed = true;  //标记已被占用
//                        edge.get(z).wavelengths.get(n).waveserviceID=serviceID;     //标记占用该波长的业务
//                    }
//                }*/
//            }
//        }
//    }


}
