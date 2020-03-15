package Gen;

public class CoSerEG { //record service for electrical grooming
    public int beginNode;
    public int endNode;
    public int coSerID;
    public int transmission;
    public double departTime;
    public CoSerEG(int beginNod,int endNode,int coSerID,int transmission){
        this.beginNode = beginNod;//nodeID
        this.endNode = endNode;//nodeID
        this.coSerID = coSerID;
        this.transmission = transmission;
    }

    public CoSerEG(int beginNod,int endNode,int coSerID,int transmission,double departTime){
        this.beginNode = beginNod;//nodeID
        this.endNode = endNode;//nodeID
        this.coSerID = coSerID;
        this.transmission = transmission;
        this.departTime = departTime;
    }

}
