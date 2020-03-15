package Utils;
import Common.CommonResource;
import Topology.Link;
import Topology.Node;
import org.omg.CORBA.CODESET_INCOMPATIBLE;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @auther：sherrystar
 * @package_name: Utils
 * @create_time: 2019/1/17
 * @describe:
 */
public class ReadTopo {

    private String fileName;
    public List<Node> nodeList =  new ArrayList<Node>();
    public List<Link> linkList =  new ArrayList<Link>();
    public int[][] topo = new int[CommonResource.NODE_NUMBER][CommonResource.NODE_NUMBER];//means connected or not

    public ReadTopo(String fileName) {
        this.fileName = fileName;

        BufferedReader inputStream = null;
        try {
            inputStream = new BufferedReader(new FileReader(fileName));
            String line;
            int i = 0;
            while((line = inputStream.readLine()) != null){
                String[] nodeRow = line.split("\\s+");
                for (int j = 0; j < nodeRow.length; j++) {
                    topo[i][j] = Integer.parseInt(nodeRow[j]);
                }
                i++;
            }
        } catch(Exception e){
            System.out.println(e);
        }

        //generate nodelist and linklist
        genNodeList(); //只能generate一次
        genLinkList();

        //给到CommonResouce
        CommonResource.topo = topo;
    }

    //node number from 1 to ...    l
    public void genNodeList(){
        for (int i = 1; i <= topo.length; i++) {
            nodeList.add(new Node(i));
        }
    }

    //for Link   link(1,2)!=link(2,1)
    public void genLinkList(){

        for (int i = 1; i <= topo.length; i++) {
            for (int j = 1; j <= topo[0].length; j++) {
                if(topo[i-1][j-1] == 1)
                    linkList.add(new Link(i,j));
            }
        }
    }

    public List<Node> getNodeList() {
        return nodeList;
    }

    public List<Link> getLinkList() {
        return linkList;
    }

    //返回拓扑矩阵
    public int[][] getTopo() {
        return topo;
    }



    public void printOut(){
        for (int i = 0; i < CommonResource.NODE_NUMBER; i++) {
            for (int j = 0; j < CommonResource.NODE_NUMBER; j++) {
                System.out.print(topo[i][j]+" ");
            }
            System.out.println();
        }
    }


    public static void main(String[] args) {
        new ReadTopo(CommonResource.FILE_NAME).printOut();
    }

}
