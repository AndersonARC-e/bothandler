package data;

public class Peer {
    public int local_id;
    public int id;
    public String type;
    private PeerType typeEnum;

    public String getType() {
        return type;
    }
}
