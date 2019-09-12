import data.*;
import net.dongliu.requests.Requests;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Random;

public class VK {
    LongpollServer[] server;
    String[] tokens;
    Random random;
    Gson gson;

    public VK(String[] tokens) {
        this.tokens = tokens;
        random = new Random();
        gson = new Gson();
        newLongpollServer();
    }

    public String generateRandom(int b){
        String output = "";
        for (int i = b; i > 0; i--){
            output += random.nextInt(9);
        }
        return output;
    }

    public String joinChat(String link){
        String response = null;
        for (String token : tokens) {
            String URL = "https://api.vk.com/method/messages.joinChatByInviteLink?v=5.101&link=" + link + "&access_token=" + token;
            response += Requests.get(URL).send().readToText() + "\n";
            try {
                Thread.sleep(350);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    public String forward(int fwd_id, int peerID){
        String response = null;
        if (peerID > 2000000000) {
            for (String token : tokens) {
                String URL = "https://api.vk.com/method/messages.send?v=5.101&chat_id=" + (peerID - 2000000000) + "&access_token=" + token + "&forward_messages=" + fwd_id + "&peer_id=" + peerID + "&random_id=" + generateRandom(16);
                response += Requests.get(URL).send().readToText() + "\n";
                try {
                    Thread.sleep(350);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            for (String token : tokens) {
                String URL = "https://api.vk.com/method/messages.send?v=5.101&user_id=" + peerID + "&access_token=" + token + "&forward_messages=" + fwd_id + "&peer_id=" + peerID + "&random_id=" + generateRandom(16);
                response += Requests.get(URL).send().readToText() + "\n";
                try {
                    Thread.sleep(350);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return response;
    }

    public String writeToChat(String message, String domain){
        int peerID = getUser(domain)[0][0].id;
        String response = null;
        for (String token : tokens) {
            String URL = "https://api.vk.com/method/messages.send?v=5.101&user_id=" + peerID + "&access_token=" + token + "&message=" + message.replace(' ', '+') + "&peer_id=" + peerID + "&random_id=" + generateRandom(16);
            response += Requests.get(URL).send().readToText() + "\n";
            try {
                Thread.sleep(350);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    public String writeToChat(String message, int peerID){
        String response = null;
        if (peerID > 2000000000) {
            for (String token : tokens) {
                String URL = "https://api.vk.com/method/messages.send?v=5.101&chat_id=" + (peerID - 2000000000) + "&access_token=" + token + "&message=" + message.replace(' ', '+') + "&peer_id=" + peerID + "&random_id=" + generateRandom(16);
                response += Requests.get(URL).send().readToText() + "\n";
                try {
                    Thread.sleep(350);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            for (String token : tokens) {
                String URL = "https://api.vk.com/method/messages.send?v=5.101&user_id=" + peerID + "&access_token=" + token + "&message=" + message.replace(' ', '+') + "&peer_id=" + peerID + "&random_id=" + generateRandom(16);
                response += Requests.get(URL).send().readToText() + "\n";
                try {
                    Thread.sleep(350);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return response;
    }

    public Chat[][] getConversations(){
        LinkedList<Chat[]> result = new LinkedList<>();
        for (String token: tokens) {
            String URL = "https://api.vk.com/method/messages.getConversations?v=5.101&access_token=" + token;
            String conversations = "";
            try {
                JSONObject response = new JSONObject(Requests.get(URL).send().readToText());
                conversations = response.getJSONObject("response").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            result.add(gson.fromJson(conversations, Conversations.class).items);
        }
        return result.toArray(Chat[][]::new);
    }

    public User[][] getUser(String user_ids){
        LinkedList<User[]> response = new LinkedList<>();
        for (String token : tokens) {
            String URL = "https://api.vk.com/method/users.get?v=5.101&access_token=" + token + "&user_ids=" + user_ids;
            try {
                JSONObject json = new JSONObject(Requests.get(URL).send().readToText());
                response.add(gson.fromJson(json.get("response").toString(), User[].class));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(350);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return response.toArray(User[][]::new);
    }

    public User[][] getUser(String user_ids, String name_case){
        LinkedList<User[]> response = new LinkedList<>();
        for (int i = 0; i < tokens.length; i++) {
            String URL = "https://api.vk.com/method/users.get?v=5.101&access_token=" + tokens[i] + "&user_ids=" + user_ids + "&name_case=" + name_case;
            try {
                JSONObject json = new JSONObject(Requests.get(URL).send().readToText());
                response.add(gson.fromJson(json.get("response").toString(), User[].class));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(350);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return response.toArray(User[][]::new);
    }

    public Message[] getChatHistory(int count, int chatID, String token){
        String URL = "https://api.vk.com/method/messages.getHistory?v=5.101&access_token=" + token + "&peer_id=200000000" + chatID + "&count=" + count;
        return gson.fromJson(Requests.get(URL).send().readToText(), MessageHistory.class).items;
    }

    public void newLongpollServer(){
        LinkedList<LongpollServer> response = new LinkedList<>();
        for (String token : tokens) {
            String URL = "https://api.vk.com/method/messages.getLongPollServer?v=5.101&need_pts=1&lp_version=3&access_token=" + token;
            response.add(gson.fromJson(Requests.get(URL).send().readToText(), LongpollServer.class));
            try {
                Thread.sleep(350);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        server = response.toArray(LongpollServer[]::new);
    }

    public Update[] askLongPoll(int wait, int mode) {
        LinkedList<Update> result = new LinkedList<>();
        for (int i = 0; i < tokens.length; i++) {
            //getting new update
            Update update = new Update();
            Server poll = server[i].response;
            String URL = "https://" + poll.server + "?act=a_check&key=" + poll.key + "&ts=" + poll.ts + "&wait=" + wait + "&mode=" + mode + "&version=3&access_token=" + tokens[i];
            try {
                JSONObject response = new JSONObject(Requests.get(URL).timeout(wait*1000, wait*1000).send().readToText());
                //indexing ts
                poll.ts = response.getInt("ts");
                //indexing updates
                JSONArray updates = response.getJSONArray("updates");
                //initialising updates
                for (int j = 0; j < updates.length(); j++){
                    //get json array
                    JSONArray current = updates.getJSONArray(i);
                    update.code = current.getInt(0);
                    if (update.code == 4) {
                        update.message_id = current.getInt(1);
                        update.flag = current.getInt(2);
                        update.peer_id = current.getInt(3);
                        update.timestamp = current.getInt(4);
                        update.text = current.getString(5);
                        update.from = Integer.parseInt(current.getJSONObject(6).getString("from"));
                        //update.attachments = new Attachment[10];
                        //update.attachments[0] = (Attachment) current.getJSONObject(7).get("attach1");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            result.add(update);
            try {
                Thread.sleep(350);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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

    public String updateToString(Update[] updates) {
        StringBuilder result = new StringBuilder();
        for (Update update: updates){
            if (update.text != null){
                result.append("<").append(update.peer_id).append("> [").append(update.from).append("]: ").append(update.text).append("\n");
            }
        }
        return result.toString();
    }

    public LongpollServer[] getLongpollServer(){
        return server;
    }
}
