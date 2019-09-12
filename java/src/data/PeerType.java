package data;

public enum PeerType {
    user("user"),chat("chat"),group("group"),email("email");

    private String type;

    public String asString(){
        return type;
    }

    PeerType(String type){

    }
}
