package data;

import com.google.gson.Gson;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.LinkedList;

public class Console {
    public LinkedList<String[]> aic;
    LinkedList<String> tokens;
    JTextField console;
    JTextArea field;
    int chat;

    public Console(LinkedList<String> tokens) {
        aic = new LinkedList<String[]>();
        this.tokens = tokens;
        JFrame frame = new JFrame("BotConsole");
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        init(frame);
        frame.add(panel);
        frame.setSize(600,300);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    void init(JFrame frame){
        field = new JTextArea();
        field.setBounds(0,0,600,280);
        frame.add(field);
        console = new JTextField();
        console.setBounds(0,280,600,20);
        frame.add(console);
    }
}
