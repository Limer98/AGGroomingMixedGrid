package Utils;
//定义双键值的HashMap

import Topology.Link;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class DoubleHash<K1, K2, V> extends HashMap<K1, HashMap<K2, V>> {

    private static final long serialVersionUID = 1L;

    //两个key都包含return true，两个key都不包含return false
    public boolean containsKey(K1 key1, K2 key2){
        if(this.containsKey(key1)){
            if (this.get(key1).containsKey(key2)){
                return true;
            }
        }
        return false;
    }

    public V get(K1 key1, K2 key2){
        if(containsKey(key1, key2)){
            return super.get(key1).get(key2); //super调用父类HashMap中的方法
        }
        return null;
    }

    //若都不存在，则给K1, K2, V赋值； 若存在K1，则赋值K2, V。
    public V put(K1 key1, K2 key2, V value){
        if (this.containsKey(key1)){
            return this.get(key1).put(key2, value);
        }
        this.put(key1,new HashMap<K2,V>());
        this.get(key1).put(key2, value);
        return null;
    }

    //采集所有value值并形成List
    public Collection<V> getValues(){
        Collection<V> allValues = new ArrayList<V>();
        for (K1 key1 : this.keySet()){
            ((ArrayList<V>) allValues).addAll(super.get(key1).values());
        }
        return allValues;
    }

    //获取所有value的数目
    public int size(){
        int size = 0;
        for (K1 key1 : this.keySet()){
            size += this.get(key1).size();
        }
        return size;
    }

    //打印出key1和key2对应的value值
    public void print(){
        for (K1 key1 : this.keySet()){
            for (K2 key2 : this.get(key1).keySet()){
                System.out.println(this.get(key1, key2));
            }
        }
    }

    public static void main(String[] args){
        DoubleHash<Integer, Integer, String> index = new DoubleHash<Integer, Integer, String>();
        index.put(1,14,"a");
        index.put(1,7,"b");
        index.put(2,12,"c");
        index.put(2,6,"d");
        index.print();
        Collection<String> collection = new ArrayList<String>();
        collection = index.getValues();
        System.out.println(collection);

        DoubleHash<Integer, Integer, Link> x = new DoubleHash<Integer, Integer, Link>();
        Link link = new Link(1,14,10,1);
        x.put(1,14,link);
    }
}
