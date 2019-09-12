import data.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;

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
                Update[] updates = vk.askLongPoll(25,2);
                if (updates != null) {
                    String upd = vk.updateToString(updates);
                    if (!upd.equals("")) {
                        console.print(upd);
                    }
                    console.check(updates);
                    for (Update update: updates) {
                        for (int j = 0; j < console.spyConversations.size(); j++){
                            if (update.peer_id == console.spyConversations.get(j).toSpy){
                                vk.forward(update.message_id, console.spyConversations.get(j).toForward);
                            }
                        }
                    }
                }
            }
            catch (Exception e){
                System.out.println("CATCHER");
                e.printStackTrace();
            }
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
                LongpollServer[] server = vk.getLongpollServer();
                for (int i = 0; i < vk.tokens.length; i++) {
                    Server current = server[i].response;
                    console.print("<longpoll server>: [server=" + current.server  + "] [key=" + current.key + "] [ts=" + current.ts + "] [pts=" + current.pts + "]");
                }
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
    public Chat[][] conversations;
    public LinkedList<String[]> aic;
    public LPListener listener;
    LinkedList<SpyPair> spyConversations;
    String[] tokens;
    JTextArea field;
    JTextArea console;
    VK vk;
    int chat;
    String domainChat;

    public Console(String[] tokens, VK vk) {
        this.tokens = tokens;
        this.vk = vk;
        aic = new LinkedList<>();
        listener = new LPListener(this, vk);
        conversations = vk.getConversations();
        spyConversations = new LinkedList<>();
        JFrame frame = new JFrame("BotConsole");
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        init(frame);
        frame.add(panel);
        frame.setSize(900,446);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
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
        Update[] check1 = vk.checkForString(toCheck, "Клеф че скажешь");
        Update[] check2 = vk.checkForString(toCheck, "Клеф помощь");
        Update[] check3 = vk.checkForString(toCheck, "Клеф чат");
        Update[] check4 = vk.checkForString(toCheck, "Клеф напиши");
        Update[] check5 = vk.checkForString(toCheck, "Клеф зайди");
        Update[] check6 = vk.checkForString(toCheck, "Клеф борщ");
        Update[] check7 = vk.checkForString(toCheck, "Клеф шпион");
        if (check1.length > 0) {
            for (Update update : check1) {
                vk.writeToChat("[id" + update.from + "|Пидарас], хули тебе надо?", update.peer_id);
            }
        }
        else if (check2.length > 0) {
            for (Update update : check2) {
                vk.writeToChat("Тебе уже ничего не поможет...<br><br>" +
                        "Команды:<br>" +
                        " - Клеф че скажешь - скажу что нибудь<br>" +
                        " - Клеф чат [айди чата/человека] - переключиться на чат<br>" +
                        " - Клеф зайди [ссылка на чат] - зайду в беседу<br>" +
                        " - Клеф напиши [сообщение] - напишу сообщение в выбранный чат<br>" +
                        " - Клеф шпион [айди чата/человека] - буду пересылать в беседу сообщения из указанного чата<br>" +
                        " - Клеф борщ - недоработанная функция, для исполнения нужны деньги (киви 79214162287)", update.peer_id);
            }
        }
        else if (check3.length > 0) {
            for (Update update : check3) {
                try {
                    if (update.text.contains("Клеф чат id")) {
                        int parsed = Integer.parseInt(update.text.replace("Клеф чат id", ""));
                        String parse = parsed + "";
                        boolean trustee = false;
                        User[][] users = vk.getUser(parse, "ins");
                        if (users[0][0] != null) {
                            for (Chat[] conversation : conversations) {
                                for (Chat value : conversation) {
                                    Conversation current = value.conversation;
                                    if (current.peer.id == parsed) {
                                        if (current.can_write.allowed) {
                                            vk.writeToChat("Чат изменен на чат с " + users[0][0].first_name + " " + users[0][0].last_name, update.peer_id);
                                            chat = parsed;
                                            domainChat = "";
                                            trustee = true;
                                        } else {
                                            vk.writeToChat("Меня забанили в чате с " + users[0][0].first_name + " " + users[0][0].last_name, update.peer_id);
                                            trustee = true;
                                            domainChat = "";
                                            break;
                                        }
                                    } else {
                                        if (!trustee) {
                                            vk.writeToChat("Меня нет в чате c id" + parsed + ", но вы можете добавить меня туда с помощью команды Клеф зайти", update.peer_id);
                                            break;
                                        }
                                    }
                                }
                            }
                        } else {
                            vk.writeToChat("Пользователя c id" + parsed + " не существует", update.peer_id);
                            break;
                        }
                    } else {
                        int parsed = Integer.parseInt(update.text.replace("Клеф чат ", ""));
                        boolean trustee = false;
                        for (int i = 0; i < conversations.length; i++) {
                            for (int j = 0; j < conversations[i].length; j++) {
                                Conversation current = conversations[i][j].conversation;
                                if (current.peer.local_id == parsed) {
                                    if (current.can_write.allowed) {
                                        vk.writeToChat("Чат изменен на чат " + current.chat_settings.title, update.peer_id);
                                        trustee = true;
                                        chat = parsed + 2000000000;
                                        break;
                                    } else {
                                        vk.writeToChat("Меня забанили в чате " + current.chat_settings.title, update.peer_id);
                                        trustee = true;
                                        break;
                                    }
                                }
                            }
                            if (!trustee) {
                                vk.writeToChat("Меня нет в чате " + parsed + ", но вы можете добавить меня туда с помощью команды Клеф зайти", update.peer_id);
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    String parsed = update.text.replace("Клеф чат ", "");
                    User[][] users = vk.getUser(parsed, "ins");
                    if (users[0][0] != null) {
                        vk.writeToChat("Чат изменен на чат с " + users[0][0].first_name + " " + users[0][0].last_name, update.peer_id);
                        domainChat = parsed;
                        chat = 0;
                    } else {
                        vk.writeToChat("Ошибка", update.peer_id);
                        e.printStackTrace();
                    }
                }
            }
        }
        else if (check4.length > 0) {
            for (Update update : check4) {
                if (chat > 0) {
                    String response = vk.writeToChat(update.text.replace("Клеф напиши ", ""), chat);
                    if (response.contains("error")) {
                        vk.writeToChat("Ошибка", update.peer_id);
                    } else {
                        vk.writeToChat("Написал", update.peer_id);
                    }
                } else if (!domainChat.equals("")) {
                    String response = vk.writeToChat(update.text.replace("Клеф напиши ", ""), domainChat);
                    if (response.contains("error")) {
                        vk.writeToChat("Ошибка", update.peer_id);
                    } else {
                        vk.writeToChat("Написал", update.peer_id);
                    }
                } else {
                    vk.writeToChat("Ошибка", update.peer_id);
                }
            }
        }
        else if (check5.length > 0) {
            for (Update update : check5) {
                String response = vk.joinChat(update.text.replace("Клеф зайди ", ""));
                if (response.contains("error")) {
                    vk.writeToChat("Ошибка", update.peer_id);
                } else {
                    vk.writeToChat("Зашел", update.peer_id);
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
                        String parse = parsed + "";
                        boolean trustee = false;
                        User[][] users = vk.getUser(parse, "ins");
                        if (users[0][0] != null) {
                            for (Chat[] conversation : conversations) {
                                for (Chat value : conversation) {
                                    Conversation current = value.conversation;
                                    if (current.peer.id == parsed) {
                                        if (current.can_write.allowed) {
                                            vk.writeToChat("Шпионаж за чатом с " + users[0][0].first_name + " " + users[0][0].last_name + " запущен", update.peer_id);
                                            spyConversations.add(new SpyPair(users[0][0].id, update.peer_id));
                                            trustee = true;
                                        } else {
                                            vk.writeToChat("Меня забанили в чате с " + users[0][0].first_name + " " + users[0][0].last_name, update.peer_id);
                                            trustee = true;
                                            break;
                                        }
                                    } else {
                                        if (!trustee) {
                                            vk.writeToChat("Меня нет в чате c id" + parsed + ", но вы можете добавить меня туда с помощью команды Клеф зайти", update.peer_id);
                                            break;
                                        }
                                    }
                                }
                            }
                        } else {
                            vk.writeToChat("Пользователя c id" + parsed + " не существует", update.peer_id);
                            break;
                        }
                    } else {
                        int parsed = Integer.parseInt(update.text.replace("Клеф шпион ", ""));
                        boolean trustee = false;
                        for (int i = 0; i < conversations.length; i++) {
                            for (int j = 0; j < conversations[i].length; j++) {
                                Conversation current = conversations[i][j].conversation;
                                if (current.peer.local_id == parsed) {
                                    if (current.can_write.allowed) {
                                        vk.writeToChat("Шпионаж за чатом " + current.chat_settings.title + " запущен", update.peer_id);
                                        trustee = true;
                                        spyConversations.add(new SpyPair(current.peer.id, update.peer_id));
                                        break;
                                    } else {
                                        vk.writeToChat("Меня забанили в чате " + current.chat_settings.title, update.peer_id);
                                        trustee = true;
                                        break;
                                    }
                                }
                            }
                            if (!trustee) {
                                vk.writeToChat("Меня нет в чате " + parsed + ", но вы можете добавить меня туда с помощью команды Клеф зайти", update.peer_id);
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    String parsed = update.text.replace("Клеф шпион ", "");
                    User[][] users = vk.getUser(parsed, "ins");
                    if (users[0][0] != null) {
                        vk.writeToChat("Шпионаж за чатом с " + users[0][0].first_name + " " + users[0][0].last_name + " запущен", update.peer_id);
                        spyConversations.add(new SpyPair(users[0][0].id, update.peer_id));
                        chat = 0;
                    } else {
                        vk.writeToChat("Ошибка", update.peer_id);
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void print(String text){
        field.setText(field.getText() + "\n" + text);
    }

    public void out(String text){
        console.setText("/" + text);
    }

    public void clr() {
        console.setText("/");
    }

    public String get() {
        return new StringBuilder(console.getText()).deleteCharAt(0).toString().replace("\n","");
    }
}
