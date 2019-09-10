import data.*;
import net.dongliu.requests.Requests;
import com.google.gson.Gson;

import java.util.LinkedList;
import java.util.Random;


public class VK {
    LinkedList<String> tokens;
    Random random;
    Gson gson;

    public VK(LinkedList<String> tokens) {
        this.tokens = tokens;
        Random random = new Random();
        Gson gson = new Gson();
    }

    public String generateRandom(int b){
        String output = "";
        int randomChar = 0;
        for (int i = b; i > 0; i--){
            randomChar = random.nextInt(9);
            output += randomChar;
        }
        return output;
    }

    public String joinChat(String link, String token){
        String URL = "https://api.vk.com/method/messages.joinChatByInviteLink?v=5.101&link=" + link + "&access_token=" + token;
        return Requests.get(URL).send().readToText();
    }

    public String writeToChat(String message, int chatID, String token){
        String URL = "https://api.vk.com/method/messages.send?v=5.101&&chat_id=" + chatID + "&access_token=" + token + "&message=" + message.replace(' ', '+') + "&peer_id=200000000" + chatID + "&random_id=" + generateRandom(16);
        return Requests.get(URL).send().readToText();
    }

    public String getChats(String token){
        String URL = "https://api.vk.com/method/messages.getConservations?v=5.101&access_token=" + token;
        return Requests.get(URL).send().readToText();
    }

    public User getUser(int userID, String token){
        String URL = "https://api.vk.com/method/users.get?v=5.101&access_token=" + token + "&user_ids=" + userID;
        return gson.fromJson(Requests.get(URL).send().readToText(), User.class);
    }

    public Message[] getChatHistory(int count, int chatID, String token){
        String URL = "https://api.vk.com/method/messages.getHistory?v=5.101&access_token=" + token + "&peer_id=200000000" + chatID + "&count=" + count;
        return gson.fromJson(Requests.get(URL).send().readToText(), Response.class).items;
    }

    public LongpollServer getLongpollServer(String token){
        String URL = "https://api.vk.com/method/messages.getLongPollServer?v=5.101&need_pts=1&lp_version=3&access_token=" + token;
        String response = Requests.get(URL).send().readToText();
        return gson.fromJson(response, LongpollServer.class);
    }

    public String askLongPoll(LongpollServer server, int wait, int mode, String token){
        String URL = "https://" + server.server + "?act=a_check&key=" + server.key + "&ts=" + server.ts + "&wait=" + wait + "&mode=" + mode + "&version=3&access_token=" + token;
        return Requests.get(URL).send().readToText();
    }
}
