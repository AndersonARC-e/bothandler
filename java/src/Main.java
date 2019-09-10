import data.Console;

import java.util.LinkedList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        LinkedList<String> tokens = new LinkedList<>();
        System.out.println("<insert token>:" + "\n/");
        tokens.add("b8e6e7b44844c91c283e4c547ee111474656349d2784ee654963232201d4b5a531c7c296548d7bac17454");
        VK vk = new VK(tokens);
        Console console = new Console(tokens);
        System.out.println("<token inserted>" + "\n<command>" + "\n/");
        String next = " ";
        while (!next.equals("")){
            next = scanner.next();
            if (next.equals("help")){
                System.out.println("<help:>" + "\n/write chat text");
            }
        }
    }
}
