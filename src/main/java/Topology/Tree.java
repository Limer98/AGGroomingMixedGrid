package Topology;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther：sherrystar
 * @package_name: Topology
 * @create_time: 2019/1/18
 * @describe:
 */
public class Tree {

    TreeNode resultTree = null;
}

class TreeNode{
    Node value = null;
    TreeNode parent = null;
    List<TreeNode> nlist = new ArrayList<TreeNode>();

    public TreeNode(){

    }
    public TreeNode(Node n){
        this.value = n;
    }
}
