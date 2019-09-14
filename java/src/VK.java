import data.*;
import net.dongliu.requests.Requests;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class VK {
    LongpollServer server;
    String token;
    Random random;
    Gson gson;

    public VK(String token) {
        this.token = token;
        random = new Random();
        gson = new Gson();
        newLongpollServer();
    }

    public String generateRandom(int b) {
        StringBuilder output = new StringBuilder();
        for (int i = b; i > 0; i--){
            output.append(random.nextInt(9));
        }
        return output.toString();
    }

    public String joinChat(String link) {
        String URL = "https://api.vk.com/method/messages.joinChatByInviteLink?v=5.101&link=" + link + "&access_token=" + token;
        return Requests.get(URL).send().readToText() + "\n";
    }

    public String forward(int fwd_id, int peerID) {
        if (peerID > 2000000000) {
            String URL = "https://api.vk.com/method/messages.send?v=5.101&chat_id=" + (peerID - 2000000000) + "&access_token=" + token + "&forward_messages=" + fwd_id + "&peer_id=" + peerID + "&random_id=" + generateRandom(16);
            return Requests.get(URL).send().readToText();
        }
        else {
            String URL = "https://api.vk.com/method/messages.send?v=5.101&user_id=" + peerID + "&access_token=" + token + "&forward_messages=" + fwd_id + "&peer_id=" + peerID + "&random_id=" + generateRandom(16);
            return Requests.get(URL).send().readToText();
        }
    }

    public String writeToChat(String message, String domain) {
        int peerID = getUser(domain).id;
        String URL = "https://api.vk.com/method/messages.send?v=5.101&user_id=" + peerID + "&access_token=" + token + "&message=" + message.replace(' ', '+') + "&peer_id=" + peerID + "&random_id=" + generateRandom(16);
        return Requests.get(URL).send().readToText() + "\n";
    }

    public String writeToChat(String message, int peerID) {
        if (peerID > 2000000000) {
            String URL = "https://api.vk.com/method/messages.send?v=5.101&chat_id=" + (peerID - 2000000000) + "&access_token=" + token + "&message=" + message.replace(' ', '+') + "&peer_id=" + peerID + "&random_id=" + generateRandom(16);
            return Requests.get(URL).send().readToText() + "\n";
        }
        else {
            String URL = "https://api.vk.com/method/messages.send?v=5.101&user_id=" + peerID + "&access_token=" + token + "&message=" + message.replace(' ', '+') + "&peer_id=" + peerID + "&random_id=" + generateRandom(16);
            return Requests.get(URL).send().readToText() + "\n";
        }
    }

    public Chat[] getConversations() {
        String URL = "https://api.vk.com/method/messages.getConversations?v=5.101&access_token=" + token;
        String conversations = "";
        try {
            JSONObject response = new JSONObject(Requests.get(URL).send().readToText());
            conversations = response.getJSONObject("response").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LinkedList<Chat> response = new LinkedList<>(Arrays.asList(gson.fromJson(conversations, Conversations.class).items));
        return response.toArray(Chat[]::new);
    }

    public Chat[] getAllConversations(){
        String URL = "https://api.vk.com/method/messages.getConversations?v=5.101&count=200&access_token=" + token;
        String conversations = "";
        try {
            JSONObject response = new JSONObject(Requests.get(URL).send().readToText());
            conversations = response.getJSONObject("response").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LinkedList<Chat> response = new LinkedList<>(Arrays.asList(gson.fromJson(conversations, Conversations.class).items));
        return response.toArray(Chat[]::new);
    }

    public User getUser(String domain) {
        String URL = "https://api.vk.com/method/users.get?v=5.101&access_token=" + token + "&user_ids=" + domain;
        try {
            JSONObject json = new JSONObject(Requests.get(URL).send().readToText());
            return gson.fromJson(json.get("response").toString(), User[].class)[0];
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUser(int user_id) {
        String URL = "https://api.vk.com/method/users.get?v=5.101&access_token=" + token + "&user_ids=" + user_id;
        try {
            JSONObject json = new JSONObject(Requests.get(URL).send().readToText());
            return gson.fromJson(json.get("response").toString(), User[].class)[0];
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUser(String domain, NameCase name_case) {
        String URL = "https://api.vk.com/method/users.get?v=5.101&access_token=" + token + "&user_ids=" + domain + "&name_case=" + name_case.getType();
        try {
            JSONObject json = new JSONObject(Requests.get(URL).send().readToText());
            return gson.fromJson(json.get("response").toString(), User[].class)[0];
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUser(int user_id, NameCase name_case) {
        String URL = "https://api.vk.com/method/users.get?v=5.101&access_token=" + token + "&user_ids=" + user_id + "&name_case=" + name_case.getType();
        try {
            JSONObject json = new JSONObject(Requests.get(URL).send().readToText());
            return gson.fromJson(json.get("response").toString(), User[].class)[0];
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Message[] getChatHistory(int count, int chatID, String token) {
        String URL = "https://api.vk.com/method/messages.getHistory?v=5.101&access_token=" + token + "&peer_id=200000000" + chatID + "&count=" + count;
        return gson.fromJson(Requests.get(URL).send().readToText(), MessageHistory.class).items;
    }

    public void newLongpollServer(){
        String URL = "https://api.vk.com/method/messages.getLongPollServer?v=5.101&need_pts=1&lp_version=3&access_token=" + token;
        server = gson.fromJson(Requests.get(URL).send().readToText(), LongpollServer.class);
    }

    public Update[] askLongpoll(int wait, int mode) {
        LinkedList<Update> result = new LinkedList<>();
        JSONObject response = null;
        //getting new update
        Update update = new Update();
        Server poll = server.response;
        String URL = "https://" + poll.server + "?act=a_check&key=" + poll.key + "&ts=" + poll.ts + "&wait=" + wait + "&mode=" + mode + "&version=3&access_token=" + token;
        try {
            response = new JSONObject(Requests.get(URL).timeout(0, 0).send().readToText());
            //indexing ts
            poll.ts = response.getInt("ts");
            //indexing updates
            JSONArray updates = response.getJSONArray("updates");
            //initialising updates
            for (int j = 0; j < updates.length(); j++){
                //get json array
                JSONArray current = updates.getJSONArray(j);
                update.code = current.getInt(0);
                if (update.code == 4) {
                    update.message_id = current.getInt(1);
                    update.flag = current.getInt(2);
                    update.peer_id = current.getInt(3);
                    update.timestamp = current.getInt(4);
                    update.text = current.getString(5);
                    if (current.toString().contains("from")) {
                        update.from = Integer.parseInt(current.getJSONObject(6).getString("from"));
                    } else {
                        update.from = update.peer_id;
                    }
                    //update.attachments = new Attachment[10];
                    //update.attachments[0] = (Attachment) current.getJSONObject(7).get("attach1");
                }
            }
            } catch (JSONException e) {
                System.out.println(response);
                e.printStackTrace();
            }
            result.add(update);
        return result.toArray(Update[]::new);
    }

    public Update[] checkForString(Update[] updates, String text) {
        LinkedList<Update> result = new LinkedList<>();
        for (Update update : updates) {
            if (update.text != null) {
                if (update.text.contains(text)) {
                    result.add(update);
                }
            }
        }
        return result.toArray(Update[]::new);
    }

    public Update[] checkForID(Update[] updates, int from_id) {
        LinkedList<Update> result = new LinkedList<>();
        for (Update update : updates) {
            if (update.from == from_id) {
                result.add(update);
            }
        }
        return result.toArray(Update[]::new);
    }

    public Chat conservationById(int peerID) {
        Chat[] chats = getAllConversations();
        for (Chat chat: chats){
            if (chat.conversation.peer.id == peerID){
                return chat;
            }
        }
        return null;
    }

    public Chat conservationByName(String title) {
        Chat[] chats = getAllConversations();
        for (Chat chat: chats){
            if (chat.conversation.chat_settings.title.contains(title)){
                return chat;
            }
        }
        return null;
    }

    public String updateToString(Update[] updates) {
        StringBuilder result = new StringBuilder();
        for (Update update: updates){
            if (update.text != null){
                result.append("<").append(update.peer_id).append("> [").append(update.from).append("]: ").append(update.text).append("\n");
            }
        }
        return result.toString();
    }

    public LongpollServer getLongpollServer(){
        return server;
    }
}
