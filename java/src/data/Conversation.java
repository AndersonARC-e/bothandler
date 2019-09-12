package data;

public class Conversation {
    public int last_message_id;
    public int unread_count;
    public int in_read;
    public CanWrite can_write;
    public ChatSettings chat_settings;
    public Peer peer;
    public int out_read;
    public boolean important;
    public boolean unanswered;
}
