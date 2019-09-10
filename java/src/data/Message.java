package data;

public class Message {
    public int id;
    public int date;
    public int peer_id;
    public int from_id;
    public String text;
    public int random_id;
    public String ref;
    public String ref_source;
    public boolean important;
    public String payload;
    public Message[] fwd_messages;
    public Message reply_message;
}
