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
 * ����ҵ�����󡪡���·���ж�ռ�á����䲨��������ռ�ò�����
 * 1. calculatePath������List<Graph<Node,Node>>�������������·���ζ�·��k�ζ�·
 * 2. domainWaveCalculate����������������жϣ��ٵ���checkUsed����
 *    (1) subsecondThirdWave1/2������int ͳ������
 *          (1.1) subistheSam1/2����
 * 3. checkUsed������boolean�������ж�����������Ƿ�ռ�ã���domainWaveCalculate����
 *    (1) oneEdgeEnough������int������ţ������ж����·�Ƿ��в�����Դ�����з������ڲ�����ţ�û��Ѱ�Ҵζ�·
 *    (2) secondThirdWave������int ͳ�����������жϴζ�·��k�ζ�·�����·���صĲ�������Ƿ��в�����Դ���㣬����int
 *                                             ��checkUsed�������жϣ�ͳ����=k����˵�������߶�����ò������
 *                                             ͳ������=k�����·���++���ٴ��жϴζ�·��ѭ����
 *                                             ѭ����֮�󣬻���û���ҵ�����ģ�����ζ�·
 *        (1.1) istheSam���������ж�secondThirdWave�����в����Ƿ�һ��
 * 4. markOccupiedWavelength�����������Ѿ�ռ�õĲ������б�ǣ���checkUsed������
 *
 */

public class PathCalculate {
    //ȫ�ֱ���
    public List<Node> vertex;          //List��Ϊvertex������������Node��ļ�������,ȫ�ֱ���
    public List<Link> edge;
    public int serviceID;              //������Դ��ҵ��ID

    //���캯��������ʼ��ȫ�ֱ���
    public PathCalculate(List<Node> Nodes, List<Link> Links, int eventID){
        vertex = Nodes;
        edge = Links;
        serviceID = eventID;
    }


    //����1��D��·,Path���ͣ�����class Path��,���Ϊͼg/g1��Դ�ڵ㡢Ŀ�Ľڵ㣻����k�����·������ʽList<GraphPath<Node, Node>>
    public Path calculatePath(DefaultDirectedWeightedGraph ggg, int Source, int Dest) {
        Path pa = new Path();
        DijkstraShortestPath<Node, AccessEdge> dsp = new DijkstraShortestPath<Node, AccessEdge>(ggg,vertex.get(Source-1),vertex.get(Dest-1)); //vertex�����Ǵ�0��ʼ
        GraphPath<Node, AccessEdge> p = dsp.getPath();
//        pa.nodes.add(p); //���������·��
        pa.gPath = p;
        return pa;
    }

/*    public Path calculatePath(DefaultDirectedWeightedGraph ggg, int Source, int Dest) {

        DijkstraShortestPath<Node, AccessEdge> dsp = new DijkstraShortestPath<Node, AccessEdge>(ggg,vertex.get(Source-1),vertex.get(Dest-1)); //vertex�����Ǵ�0��ʼ
        GraphPath<Node, AccessEdge> p = dsp.getPath();
//        pa.nodes.add(p); //���������·��
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


//    //����3����������Ĵ����ж��Ƿ�ռ��,����һ����,�����K���·����pa�����������͵ڼ���·����
//    public boolean checkUsed(List<AccessEdge>  edgeList, int band) {
//        int bandwave = band;
//        int searchwaveNum;     //����oneEdgeEnough����ֵ
//        int sameNum=0;        //��¼ÿ�����Ƿ����㲨������
//        int unedegNum=0;       //��¼�������������Ȼû�ҵ��Ĳ����õ���
//        boolean result = false; // ����ֵ
//        //�Ȼ�ȡ��x��·���ĵ�һ����
//        int a = edgeList.get(0).getSource().identifier;
//        int b = edgeList.get(0).getDest().identifier;
//        //ȡ���ϵ�һ���ߴ�������Ĳ������
//        for (int i=1;i<=80-bandwave+1;i++){
//            searchwaveNum = oneEdgeEnough(a, b, band,i); //���صĲ������
//            if (searchwaveNum == -1) {
//                return false;//��һ��·���������������Ѱ����һ��·��
//                } else {
//                    //����ط�ȡ������������2�е�һ����·�������·���ζ�·��������
//                    //�жϵڶ��������������Ƿ����㣬����sanmeNum+1
//                    sameNum=sameNum+1;
//                    sameNum=sameNum+secondThirdWave(searchwaveNum,bandwave,edgeList);
//                    if (sameNum==edgeList.size()){
//                        markOccupiedWavelength(searchwaveNum, bandwave,subSplited);
//                        return true;
//                    }else
//                        unedegNum=unedegNum+1;
//                }
//            }
//            if (unedegNum==80-bandwave+1){  //���80-bandware+1�ζ�û���ҵ��Ļ����ͻ��ζ�·
//               result=false;
//            }
//        return result;
//    }
//
//
//    //����4��checkUsed�������ã��жϵ�һ�������·�������ϵĲ�����Դ�Ƿ����,���ز������
//    public int oneEdgeEnough(int identifiera,int identifierb,int band,int searchNumStart){
//        int bandwave = band;
//        int v = 0; //��¼��������󲨳��������
//        //��������Link
//        int z=0;
//        //����edge���ҵ�a��b��Ӧ�ıߣ���ͷ�жϲ����Ƿ�ռ��
//        do {
//            int f =0;   //��¼��ռ�õ�����������
//            if (edge.get(z).srcSeq == identifiera && edge.get(z).dstSeq == identifierb) {
//                //�ж�һ���ԣ��Ƿ�ռ��
//                for (int n=searchNumStart; n <=80-bandwave+1; n++) {   //nһֱ�ӣ���Ҫ����bandware�ľ��룬��Ȼ�ܴ���ᳬ��80
//                    if (edge.get(z).wavelengths.get(n).isUsed) {
//                        v=0;
//                    }else{
//                        f=f+1;
//                        if (f==bandwave){
//                            v=n;   //���ڲ������
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
//    //����5��checkUsed�������ã��Ե�һ��·���ĵڶ��������߽����жϣ��Ƿ�͵�һ���߲���һ��
//    public int secondThirdWave(int searchNum ,int bandwave,List<AccessEdge> edgeList){
//        int sameNum=0;
//        boolean result;        //����istheSame����ֵ���жϺ���ıߺ͵�һ���Ƿ񲨳�һ�£�
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
//    //����7���жϲ�������֮�󣬶Բ���isUsed����ռ��
//    public void markOccupiedWavelength(int searchNum,int banware,List<Integer> subSplited){
//        int searchNum1=searchNum+banware;//TODO ��������е����⣡��
//        //k��·�����б������
//        for (int y = 0; y < subSplited.size()-1; y++) {
//            int c = subSplited.get(y);
//            int d = subSplited.get(y+1);
//            //�ҵ���Ӧ�ı�,�����б��
//            for (int z=0;z<edge.size();z++) {
//                if (edge.get(z).srcSeq == c && edge.get(z).dstSeq == d) {
//                    for (int n = searchNum; n < searchNum1; n++) {
//                        edge.get(z).wavelengths.get(n).isUsed = true;  //����ѱ�ռ��
//                        edge.get(z).wavelengths.get(n).waveserviceID=serviceID;     //���ռ�øò�����ҵ��
//                    }
//                }
//                /*else if(edge.get(z).srcSeq == d && edge.get(z).dstSeq == c){
//                    for (int n = searchNum; n < searchNum1; n++) {
//                        edge.get(z).wavelengths.get(n).isUsed = true;  //����ѱ�ռ��
//                        edge.get(z).wavelengths.get(n).waveserviceID=serviceID;     //���ռ�øò�����ҵ��
//                    }
//                }*/
//            }
//        }
//    }


}
