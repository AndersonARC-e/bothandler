import data.*;
import org.json.JSONArray;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

class LPListener extends Thread {
    Console console;
    private boolean run = true;
    VK vk;

    public LPListener(Console console, VK vk){
        this.console = console;
        this.vk = vk;
        start();
    }

    public void run() {
        while (run){
            try {
                Update[] rawUpdates = vk.askLongpoll(25,2);
                LinkedList<Update> newUpdates = new LinkedList<>();
                for (Update update: rawUpdates) {
                    if (update.from != console.botID) {
                        newUpdates.add(update);
                    }
                }
                Update[] updates = newUpdates.toArray(Update[]::new);
                if (updates != null) {
                    String upd = vk.updateToString(updates);
                    if (!upd.equals("")) {
                        console.print(upd);
                    }
                    console.check(updates);
                    for (Update update : updates) {
                        for (int j = 0; j < console.spyConversations.size(); j++) {
                            if (update.peer_id == console.spyConversations.get(j).toSpy) {
                                vk.forward(update.message_id, console.spyConversations.get(j).toForward);
                            }
                        }
                    }
                    for (Update update: updates) {
                        for (int j = 0; j < console.learnConversations.size(); j++){
                            if (update.peer_id == console.learnConversations.get(j)){
                                if (!update.text.contains("Клеф")) {
                                    console.aic.add(update.text.split(" "));
                                    console.aic(update.peer_id);
                                }
                            }
                        }
                        for (int j = 0; j < console.globalLearnConversations.size(); j++){
                            if (update.peer_id == console.globalLearnConversations.get(j)){
                                if (!update.text.contains("Клеф")) {
                                    console.aic.add(update.text.split(" "));
                                    console.globalAic();
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception e){
                System.out.println("CATCHER");
                console.vk.writeToChat(Arrays.toString(e.getStackTrace()), console.adminID);
                e.printStackTrace();
            }
            //try {
                //Update[] updates = console.spy.askLongpoll(25, 2);
                //if (updates != null) {
                   //String upd = console.spy.updateToString(updates);
                    //if (!upd.equals("")) {
                        //console.print(upd);
                    //}
                    //console.check(updates);
                //}
            //} catch (Exception e){
                    //System.out.println("CATCHER");
                    //e.printStackTrace();
            //}
        }
    }

    public void continueThread() {
        run = true;
        start();
    }

    public void stopThread() {
        run = false;
    }
}

class KListener implements KeyListener {
    LinkedList<String> prev;
    Console console;
    int strindex;
    VK vk;

    public KListener(Console console, VK vk) {
        this.prev = new LinkedList<>();
        this.console = console;
        this.vk = vk;
    }

    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e) {
        String input = console.get();
        if (e.getKeyCode() == 10){
            console.print("<warn:> \"Интерфейс устарел. Ошибок не избежать. Используйте не графический интерфейс.\"");
            if (input.contains("join ")) {
                console.print("<joined chat>:" + vk.joinChat(input.split(" ")[1]));
            }
            else if (input.contains("chat ")){
                console.chat = Integer.parseInt(input.split(" ")[1]);
                console.print("<switched chat>:" + input.split(" ")[1]);
            }
            else if (input.contains("write ")){
                console.print("<wrote to chat>:" + vk.writeToChat(input.split(" ",2)[1], console.chat + 2000000000));
            }
            else if (input.contains("longpoll server")){
                Server current = vk.getLongpollServer().response;
                console.print("<longpoll server>: [server=" + current.server  + "] [key=" + current.key + "] [ts=" + current.ts + "] [pts=" + current.pts + "]");
            }
            else if (input.contains("longpoll start")){
                console.listener.continueThread();
            }
            else if (input.contains("longpoll stop")){
                console.listener.stopThread();
            }
            else {
                console.print("<wrong command>:" + "\"" + input + "\"");
            }
            prev.addLast(input);
            strindex = 0;
            console.clr();
        }
        else if (e.getKeyCode() == 38 && prev.size() > 0 && strindex < prev.size() - 1){
            strindex += 1;
            console.out(prev.get(strindex));
        }
        else if (e.getKeyCode() == 40 && prev.size() > 0 && strindex > 0){
            strindex -= 1;
            console.out(prev.get(strindex));
        }
    }

    public void keyReleased(KeyEvent e) {

    }
}

public class Console {
    JScrollPane pane;
    boolean noGUI;
    public Chat[] conversations;
    public LinkedList<String[]> aic;
    public LPListener listener;
    Handler handler;
    String managerToken;
    VK spy;
    LinkedList<SpyPair> spyConversations;
    LinkedList<Integer> learnConversations;
    LinkedList<Integer> globalLearnConversations;
    JTextArea field;
    JTextArea console;
    VK vk;
    int chat;
    int adminID;
    int sudoChat;
    int botID;
    String domainChat;

    public Console(VK manager, VK spy, Handler handler, boolean noGUI, int adminID, int botID) {
        this.handler = handler;
        this.noGUI = noGUI;
        this.spy = spy;
        this.botID = botID;
        this.adminID = adminID;
        vk = manager;
        aic = new LinkedList<>();
        conversations = vk.getConversations();
        spyConversations = new LinkedList<>();
        learnConversations = new LinkedList<>();
        globalLearnConversations = new LinkedList<>();
        domainChat = "";
        chat = 0;
        sudoChat = 0;
        listener = new LPListener(this, vk);
        if (!noGUI) {
            JFrame frame = new JFrame("BotConsole");
            JPanel panel = new JPanel();
            panel.setLayout(new FlowLayout());
            init(frame);
            frame.add(panel);
            frame.setSize(900, 446);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        }
    }

    void init(JFrame frame){
        field = new JTextArea();
        field.setBounds(0,0,900,400);
        field.setBackground(Color.BLACK);
        field.setCaretColor(Color.DARK_GRAY);
        field.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        field.setText("BotCONSOLE v 1.0");
        field.setForeground(Color.GRAY);
        field.setEditable(false);
        console = new JTextArea();
        console.setBounds(0,388,900,20);
        console.setBackground(Color.DARK_GRAY);
        console.setCaretColor(Color.BLACK);
        console.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        console.setText("/");
        console.addKeyListener(new KListener(this, vk));
        console.setForeground(Color.GRAY);
        console.setEditable(true);
        pane = new JScrollPane(field);
        pane.createVerticalScrollBar();
        pane.setSize(900,390);
        pane.setBackground(Color.BLACK);
        pane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        pane.setForeground(Color.GRAY);
        frame.add(console);
        frame.setBackground(Color.BLACK);
        frame.add(pane);
    }

    public void check(Update[] toCheck) {
        Update[] check1 = vk.checkForString(toCheck, "Клеф ген");
        Update[] check2 = vk.checkForString(toCheck, "Клеф помощь");
        Update[] check3 = vk.checkForString(toCheck, "Клеф чат");
        Update[] check4 = vk.checkForString(toCheck, "Клеф напиши");
        Update[] check5 = vk.checkForString(toCheck, "Клеф зайди");
        Update[] check6 = vk.checkForString(toCheck, "Клеф борщ");
        Update[] check7 = vk.checkForString(toCheck, "Клеф шпион");
        Update[] check8 = vk.checkForString(toCheck, "Клеф учись");
        if (check1.length > 0) {
            for (Update update : check1) {
                String input = update.text;
                int peer = update.peer_id;
                if (input.contains("глобал")) {
                    if (input.contains("Клеф ген rand")) {
                        vk.writeToChat(globalGenerateAic(AicType.RANDOM, peer), peer);
                    } else if (input.contains("Клеф ген msg")) {
                        vk.writeToChat(globalGenerateAic(AicType.MESSAGE_RANDOM, peer), peer);
                    } else if (input.contains("Клеф ген strp")) {
                        vk.writeToChat(globalGenerateAic(AicType.STRUCTURIZED_PART, peer), peer);
                    } else if (input.contains("Клеф ген strf")) {
                        vk.writeToChat(globalGenerateAic(AicType.STRUCTURIZED_FULL, peer), peer);
                    } else {
                        vk.writeToChat("Вы не передали параметры для генерации: rand/msg/strp/strf", peer);
                    }
                }
                else {
                    if (input.contains("Клеф ген rand")) {
                        vk.writeToChat(generateAic(AicType.RANDOM, peer), peer);
                    } else if (input.contains("Клеф ген msg")) {
                        vk.writeToChat(generateAic(AicType.MESSAGE_RANDOM, peer), peer);
                    } else if (input.contains("Клеф ген strp")) {
                        vk.writeToChat(generateAic(AicType.STRUCTURIZED_PART, peer), peer);
                    } else if (input.contains("Клеф ген strf")) {
                        vk.writeToChat(generateAic(AicType.STRUCTURIZED_FULL, peer), peer);
                    } else {
                        vk.writeToChat("Вы не передали параметры для генерации: rand/msg/strp/strf", peer);
                    }
                }
            }
        }
        else if (check2.length > 0) {
            for (Update update : check2) {
                vk.writeToChat("Тебе уже ничего не поможет...<br><br>" +
                        "Команды:<br>" +
                        " - Клеф ген [rand/msg/strp/strf] [local/global] - скажу что нибудь<br>" +
                        " - Клеф чат [айди чата/человека] - переключиться на чат<br>" +
                        " - Клеф зайди [ссылка на чат] - зайду в беседу<br>" +
                        " - Клеф напиши [сообщение] - напишу сообщение в выбранный чат<br>" +
                        " - Клеф учись - начну обучаться из этой беседы<br>" +
                        " - Клеф шпион [айди чата/человека] - буду пересылать в беседу сообщения из указанного чата<br>" +
                        " - Клеф борщ - недоработанная функция, для исполнения нужны деньги (киви 79214162287)", update.peer_id);
            }
        }
        else if (check3.length > 0) {
            for (Update update : check3) {
                try {
                    if (!update.text.contains("sudo Клеф чат ")) {
                        if (update.text.contains("Клеф чат id")) {
                            int parsed = Integer.parseInt(update.text.replace("Клеф чат id", ""));
                            User us = vk.getUser(parsed, NameCase.INS);
                            if (us != null) {
                                Chat ch = vk.conservationById(us.id);
                                if (ch != null) {
                                    Conversation current = ch.conversation;
                                    if (current.can_write.allowed) {
                                        chat = us.id;
                                        domainChat = "";
                                        vk.writeToChat("Чат изменен на чат с " + us.first_name + " " + us.last_name, update.peer_id);
                                    } else {
                                        vk.writeToChat("Я в черном списке у " + us.first_name + " " + us.last_name, update.peer_id);
                                    }
                                } else {
                                    if (us.can_access_closed) {
                                        vk.writeToChat("У меня нет диалога с " + us.first_name + " " + us.last_name + ", но я все равно переключил чат на него", update.peer_id);
                                        chat = us.id;
                                        domainChat = "";
                                    } else {
                                        vk.writeToChat("У меня нет диалога с " + us.first_name + " " + us.last_name + " и я не могу ему написать, чат не изменен", update.peer_id);
                                    }
                                }
                            } else {
                                vk.writeToChat("Пользователя c ID " + parsed + " не существует", update.peer_id);
                            }
                        } else {
                            int parsed = Integer.parseInt(update.text.replace("Клеф чат ", ""));
                            Chat ch = vk.conservationById(parsed + 2000000000);
                            if (ch != null) {
                                Conversation current = ch.conversation;
                                if (current.can_write.allowed) {
                                    chat = parsed + 2000000000;
                                    domainChat = "";
                                    vk.writeToChat("Чат изменен на чат " + current.chat_settings.title, update.peer_id);
                                }
                                else {
                                    vk.writeToChat("Меня забанили в чате " + current.chat_settings.title, update.peer_id);
                                }
                            }
                            else  {
                                vk.writeToChat("Меня нет в чате " + parsed + ", но вы можете добавить меня туда с помощью команды Клеф зайти", update.peer_id);
                            }
                        }
                    } else if (update.from == adminID) {
                        int parsed = Integer.parseInt(update.text.replace("sudo Клеф чат ", ""));
                        sudoChat = parsed + 2000000000;
                        vk.writeToChat("Чат изменен на чат " + sudoChat, update.peer_id);
                    }
                } catch(NumberFormatException e) {
                    String parsed = update.text.replace("Клеф чат ", "");
                    User us = vk.getUser(parsed, NameCase.INS);
                    if (us != null) {
                        Chat ch = vk.conservationById(us.id);
                        if (ch != null) {
                            Conversation current = ch.conversation;
                            if (current.can_write.allowed) {
                                chat = us.id;
                                domainChat = "";
                                vk.writeToChat("Чат изменен на чат с " + us.first_name + " " + us.last_name, update.peer_id);
                            } else {
                                vk.writeToChat("Я в черном списке у " + us.first_name + " " + us.last_name, update.peer_id);
                            }
                        } else {
                            if (us.can_access_closed) {
                                vk.writeToChat("У меня нет диалога с " + us.first_name + " " + us.last_name + ", но я все равно переключил чат на него", update.peer_id);
                                chat = us.id;
                                domainChat = "";
                            } else {
                                vk.writeToChat("У меня нет диалога с " + us.first_name + " " + us.last_name + " и я не могу ему написать, чат не изменен", update.peer_id);
                            }
                        }
                    } else {
                        vk.writeToChat("Пользователя c ID " + parsed + " не существует", update.peer_id);
                    }
                }
            }
        }
        else if (check4.length > 0) {
            for (Update update : check4) {
                if (!update.text.contains("sudo Клеф напиши ")) {
                    if (chat > 0) {
                        String response = vk.writeToChat(update.text.replace("Клеф напиши ", ""), chat);
                        if (response.contains("error")) {
                            vk.writeToChat("Ошибка, проверьте лог", update.peer_id);
                            vk.writeToChat("check4:no_sudo:" + response, adminID);
                        } else {
                            vk.writeToChat("Написал", update.peer_id);
                        }
                    } else if (!domainChat.equals("")) {
                        String response = vk.writeToChat(update.text.replace("Клеф напиши ", ""), domainChat);
                        if (response.contains("error")) {
                            vk.writeToChat("Ошибка, проверьте лог", update.peer_id);
                            vk.writeToChat("check4:no_sudo:" + response, adminID);
                        } else {
                            vk.writeToChat("Написал", update.peer_id);
                        }
                    } else {
                        vk.writeToChat("Чат не задан", update.peer_id);
                    }
                } else if (update.from == adminID) {
                    if (sudoChat > 0) {
                        String response = handler.writeToChat(update.text.replace("sudo Клеф напиши ", ""), sudoChat);
                        if (response.contains("error")) {
                            vk.writeToChat("Ошибка, проверьте лог", update.peer_id);
                            vk.writeToChat("check4:sudo:" + response, adminID);
                        } else {
                            vk.writeToChat("Написал", update.peer_id);
                        }
                    } else {
                        vk.writeToChat("Чат не задан", update.peer_id);
                    }
                }
                else {
                    vk.writeToChat("Неправильно заданы параметры для команды Клеф напиши", update.peer_id);
                }
            }
        }
        else if (check5.length > 0) {
            for (Update update : check5) {
                if (!update.text.contains("sudo Клеф зайди ")) {
                    String response = vk.joinChat(update.text.replace("Клеф зайди ", ""));
                    if (response.contains("error")) {
                        vk.writeToChat("Ошибка, проверьте лог", update.peer_id);
                        vk.writeToChat("check5:no_sudo:" + response, adminID);
                    } else {
                        vk.writeToChat("Зашел", update.peer_id);
                    }
                } else {
                    String response = handler.joinChat(update.text.replace("sudo Клеф зайди ", ""));
                    if (response.contains("error")) {
                        vk.writeToChat("Ошибка, проверьте лог", update.peer_id);
                        vk.writeToChat("check5:sudo:" + response, adminID);
                    } else {
                        vk.writeToChat("Зашел", update.peer_id);
                    }
                }
            }
        }
        else if (check6.length > 0) {
            for (Update update : check6) {
                vk.writeToChat("Дай денег на киви 79214162287 и будет тебе борщ", update.peer_id);
            }
        }
        else if (check7.length > 0) {
            for (Update update : check7) {
                try {
                    if (update.text.contains("Клеф шпион id")) {
                        int parsed = Integer.parseInt(update.text.replace("Клеф шпион id", ""));
                        User us = vk.getUser(parsed, NameCase.INS);
                        if (us != null) {
                            Chat ch = vk.conservationById(parsed + 2000000000);
                            if (ch != null) {
                                if (ch.conversation.can_write.allowed) {
                                    int id = spyContains(spyConversations, us.id);
                                    if (id < 0) {
                                        spyConversations.add(new SpyPair(us.id, update.peer_id));
                                        vk.writeToChat("Шпионаж за чатом с " + us.first_name + " " + us.last_name + " запущен", update.peer_id);
                                    } else {
                                        spyConversations.remove(id);
                                        vk.writeToChat("Я уже шпионю за чатом с " + us.first_name + " " + us.last_name + ", шпионаж остановлен", update.peer_id);
                                    }
                                } else {
                                    vk.writeToChat("Я в черном списке у " + us.first_name + " " + us.last_name, update.peer_id);
                                }
                            } else {
                                vk.writeToChat("У меня нет диалога с " + us.first_name + " " + us.last_name + ", но я все равно начну за ним шпионить", update.peer_id);
                                spyConversations.add(new SpyPair(us.id, update.peer_id));
                            }
                        } else {
                            vk.writeToChat("Пользователя c id" + parsed + " не существует", update.peer_id);
                        }
                    }
                    else {
                        int parsed = Integer.parseInt(update.text.replace("Клеф шпион ", ""));
                        Chat ch = vk.conservationById(parsed + 2000000000);
                        if (ch != null) {
                            Conversation co = ch.conversation;
                            if (ch.conversation.can_write.allowed) {
                                int id = spyContains(spyConversations, co.peer.id);
                                if (id < 0) {
                                    spyConversations.add(new SpyPair(co.peer.id, update.peer_id));
                                    vk.writeToChat("Шпионаж за чатом " + co.chat_settings.title + " запущен", update.peer_id);
                                } else {
                                    spyConversations.remove(id);
                                    vk.writeToChat("Я уже шпионю за чатом " + co.chat_settings.title + ", шпионаж остановлен", update.peer_id);
                                }
                            } else {
                                vk.writeToChat("Меня забанили в чате " + co.chat_settings.title, update.peer_id);
                            }
                        } else {
                            vk.writeToChat("Меня нет в чате " + parsed, update.peer_id);
                        }
                    }
                } catch (NumberFormatException e) {
                    String parsed = update.text.replace("Клеф шпион ", "");
                    User us = vk.getUser(parsed, NameCase.INS);
                    if (us != null) {
                        int id = spyContains(spyConversations, us.id);
                        if (id < 0) {
                            spyConversations.add(new SpyPair(us.id, update.peer_id));
                            vk.writeToChat("Шпионаж за чатом с " + us.first_name + " " + us.last_name + " запущен", update.peer_id);
                        } else {
                            spyConversations.remove(id);
                            vk.writeToChat("Я уже шпионю за чатом с " + us.first_name + " " + us.last_name + ", шпионаж остановлен", update.peer_id);
                        }
                    } else {
                        vk.writeToChat("Неправильно заданы параметры для команды Клеф шпион", update.peer_id);
                        e.printStackTrace();
                        break;
                    }
                }
            }
        }
        else if (check8.length > 0) {
            for (Update update : check8) {
                int id = contains(learnConversations, update.peer_id);
                int global = contains(globalLearnConversations, update.peer_id);
                if (update.text.contains("Клеф учись глобал") && update.from == adminID)
                    if (global < 0) {
                        globalLearnConversations.add(update.peer_id);
                        vk.writeToChat("Теперь я буду глобально обучаться из вашей беседы, будьте осторожны", update.peer_id);
                    } else {
                        globalLearnConversations.remove(update.peer_id);
                        vk.writeToChat("Я уже обучаюсь из вашей беседы, глобальное обучение приостановлено", update.peer_id);
                    }
                else {
                    if (id < 0) {
                        learnConversations.add(update.peer_id);
                        vk.writeToChat("Теперь я буду обучаться из вашей беседы", update.peer_id);
                    } else {
                        learnConversations.remove(update.peer_id);
                        vk.writeToChat("Я уже обучаюсь из вашей беседы, обучение приостановлено", update.peer_id);
                    }
                }
            }
        }
    }

    public void print(String text){
        if (!noGUI) {
            field.setText(field.getText() + "\n" + text);
        }
    }

    public void aic(int peerID) {
        BufferedReader reader = null;
        BufferedWriter writer = null;
        String read = "";
        try {
            try {
                reader = new BufferedReader(new FileReader(peerID + ".aic"));
                writer = new BufferedWriter(new FileWriter(peerID + ".aic"));
            } catch (FileNotFoundException e) {
                File file = new File(peerID + ".aic");
                file.createNewFile();
                reader = new BufferedReader(new FileReader(peerID + ".aic"));
                writer = new BufferedWriter(new FileWriter(peerID + ".aic"));
            }
            read = reader.readLine();
            if (read != null) {
                JSONArray first = new JSONArray(read);
                JSONArray[] oldAic = new JSONArray[first.length()];
                for (int i = 0; i < first.length(); i++) {
                    oldAic[i] = first.getJSONArray(i);
                }
                JSONArray next = new JSONArray(aic);
                JSONArray[] newAic = new JSONArray[next.length()];
                for (int i = 0; i < next.length(); i++) {
                    newAic[i] = next.getJSONArray(i);
                }
                JSONArray[] fin = new JSONArray[first.length() + next.length()];
                for (int i = 0; i < fin.length; i++) {
                    if (i < oldAic.length) {
                        fin[i] = oldAic[i];
                    } else {
                        fin[i] = newAic[i];
                    }
                }
                writer.write(new JSONArray(fin).toString());
                writer.close();
                reader.close();
            }
            else {
                JSONArray fin = new JSONArray(aic);
                writer.write(fin.toString());
                writer.close();
                reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void globalAic() {
        BufferedReader reader = null;
        BufferedWriter writer = null;
        String read = "";
        try {
            try {
                reader = new BufferedReader(new FileReader("clef.aic"));
                writer = new BufferedWriter(new FileWriter("clef.aic"));
            } catch (FileNotFoundException e) {
                File file = new File("clef.aic");
                file.createNewFile();
                reader = new BufferedReader(new FileReader("clef.aic"));
                writer = new BufferedWriter(new FileWriter("clef.aic"));
            }
            read = reader.readLine();
            if (read != null) {
                JSONArray first = new JSONArray(read);
                JSONArray[] oldAic = new JSONArray[first.length()];
                for (int i = 0; i < first.length(); i++) {
                    oldAic[i] = first.getJSONArray(i);
                }
                JSONArray next = new JSONArray(aic);
                JSONArray[] newAic = new JSONArray[next.length()];
                for (int i = 0; i < next.length(); i++) {
                    newAic[i] = next.getJSONArray(i);
                }
                JSONArray[] fin = new JSONArray[first.length() + next.length()];
                for (int i = 0; i < fin.length; i++) {
                    if (i < oldAic.length) {
                        fin[i] = oldAic[i];
                    } else {
                        fin[i] = newAic[i];
                    }
                }
                writer.write(new JSONArray(fin).toString());
                writer.close();
                reader.close();
            }
            else {
                JSONArray fin = new JSONArray(aic);
                writer.write(fin.toString());
                writer.close();
                reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String generateAic(AicType type, int peerID){
        LinkedList<String[]> readAic = new LinkedList<>();
        Random random = new Random();
        BufferedReader bufferedReader = null;
        String read = "";
        try {
            bufferedReader = new BufferedReader(new FileReader(peerID + ".aic"));
            read = bufferedReader.readLine();
            JSONArray readArray = new JSONArray(read);
            for (int i = 0; i < readArray.length(); i++) {
                String[] toAdd = new String[readArray.getJSONArray(i).length()];
                for (int j = 0; j < toAdd.length; j++){
                    toAdd[j] = (String) readArray.getJSONArray(i).get(j);
                }
                readAic.add(toAdd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String toReturn = "";
        if (read != null) {
            if (type == AicType.STRUCTURIZED_PART) {
                String[] message = null;
                message = readAic.get(random.nextInt(readAic.size() - 1) + 1);
                for (int i = 0; i < random.nextInt(16) + 1; i++) {
                    if (random.nextBoolean()) {
                        message = readAic.get(random.nextInt(readAic.size() - 1) + 1);
                        for (String s : message) {
                            if (random.nextBoolean()) {
                                toReturn += s + " ";
                            }
                        }
                    } else {
                        for (String s : message) {
                            if (random.nextBoolean()) {
                                toReturn += s + " ";
                            }
                        }
                    }
                }
            }
            else if (type == AicType.STRUCTURIZED_FULL) {
                String[] message = null;
                message = readAic.get(random.nextInt(readAic.size() - 1) + 1);
                for (int j = 0; j < message.length; j++) {
                    String prepared = message[j];
                    toReturn += prepared + " ";
                }
            }
            else if (type == AicType.RANDOM) {
                String[] message = null;
                message = readAic.get(random.nextInt(readAic.size() - 1) + 1);
                for (int i = 0; i < random.nextInt(16) + 1; i++) {
                    if (random.nextBoolean()) {
                        message = readAic.get(random.nextInt(readAic.size() - 1) + 1);
                        for (int j = 0; j < message.length; j++) {
                            if (random.nextBoolean()) {
                                String prepared = message[random.nextInt(message.length)];
                                toReturn += prepared + " ";
                            }
                        }
                    } else {
                        for (int j = 0; j < message.length; j++) {
                            if (random.nextBoolean()) {
                                String prepared = message[random.nextInt(message.length)];
                                toReturn += prepared + " ";
                            }
                        }
                    }
                }
            }
            else if (type == AicType.MESSAGE_RANDOM) {
                String[] message = null;
                message = readAic.get(random.nextInt(readAic.size() - 1) + 1);
                for (int i = 0; i < random.nextInt(16) + 1; i++) {
                    message = readAic.get(random.nextInt(readAic.size() - 1) + 1);
                    for (String s : message) {
                        if (random.nextBoolean()) {
                            toReturn += s + " ";
                        }
                    }
                }
            }
        }
        else {
            vk.writeToChat("Я ещё не обучен в вашей беседе. Напишите Клеф учись чтобы начать обучение", peerID);
        }
        return toReturn;
    }

    public String globalGenerateAic(AicType type, int peerID){
        LinkedList<String[]> readAic = new LinkedList<>();
        Random random = new Random();
        BufferedReader bufferedReader = null;
        String read = "";
        try {
            bufferedReader = new BufferedReader(new FileReader("clef.aic"));
            read = bufferedReader.readLine();
            JSONArray readArray = new JSONArray(read);
            for (int i = 0; i < readArray.length(); i++) {
                String[] toAdd = new String[readArray.getJSONArray(i).length()];
                for (int j = 0; j < toAdd.length; j++){
                    toAdd[j] = (String) readArray.getJSONArray(i).get(j);
                }
                readAic.add(toAdd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String toReturn = "";
        if (read != null) {
            if (type == AicType.STRUCTURIZED_PART) {
                String[] message = null;
                message = readAic.get(random.nextInt(readAic.size() - 1) + 1);
                for (int i = 0; i < random.nextInt(16) + 1; i++) {
                    if (random.nextBoolean()) {
                        message = readAic.get(random.nextInt(readAic.size() - 1) + 1);
                        for (String s : message) {
                            if (random.nextBoolean()) {
                                toReturn += s + " ";
                            }
                        }
                    } else {
                        for (String s : message) {
                            if (random.nextBoolean()) {
                                toReturn += s + " ";
                            }
                        }
                    }
                }
            }
            else if (type == AicType.STRUCTURIZED_FULL) {
                String[] message = null;
                message = readAic.get(random.nextInt(readAic.size() - 1) + 1);
                for (int j = 0; j < message.length; j++) {
                    String prepared = message[j];
                    toReturn += prepared + " ";
                }
            }
            else if (type == AicType.RANDOM) {
                String[] message = null;
                message = readAic.get(random.nextInt(readAic.size() - 1) + 1);
                for (int i = 0; i < random.nextInt(16) + 1; i++) {
                    if (random.nextBoolean()) {
                        message = readAic.get(random.nextInt(readAic.size() - 1) + 1);
                        for (int j = 0; j < message.length; j++) {
                            if (random.nextBoolean()) {
                                String prepared = message[random.nextInt(message.length)];
                                toReturn += prepared + " ";
                            }
                        }
                    } else {
                        for (int j = 0; j < message.length; j++) {
                            if (random.nextBoolean()) {
                                String prepared = message[random.nextInt(message.length)];
                                toReturn += prepared + " ";
                            }
                        }
                    }
                }
            }
            else if (type == AicType.MESSAGE_RANDOM) {
                String[] message = null;
                message = readAic.get(random.nextInt(readAic.size() - 1) + 1);
                for (int i = 0; i < random.nextInt(16) + 1; i++) {
                    message = readAic.get(random.nextInt(readAic.size() - 1) + 1);
                    for (String s : message) {
                        if (random.nextBoolean()) {
                            toReturn += s + " ";
                        }
                    }
                }
            }
        }
        else {
            vk.writeToChat("Я ещё не обучен в вашей беседе. Напишите Клеф учись чтобы начать обучение", peerID);
        }
        return toReturn;
    }

    public void out(String text){
        if (!noGUI) {
            console.setText("/" + text);
        }
    }

    public void clr() {
        if (!noGUI) {
            console.setText("/");
        }
    }

    public String get() {
        return new StringBuilder(console.getText()).deleteCharAt(0).toString().replace("\n","");
    }

    public int contains(LinkedList<Integer> input, int id){
        for (int i = 0; i < input.size(); i++){
            if (input.get(i) == id){
                return i;
            }
        }
        return -1;
    }

    public int spyContains(LinkedList<SpyPair> input, int id){
        for (int i = 0; i < input.size(); i++){
            if (input.get(i).toSpy == id){
                return i;
            }
        }
        return -1;
    }
}
