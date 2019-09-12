import java.io.BufferedReader;
import java.io.FileReader;

public class Main {
    public static void main(String[] args){
        String[] tokens = null;
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader("tokens.vk"));
            tokens = bufferedReader.readLine().split(";");
        } catch (Exception e) {
            e.printStackTrace();
        }
        VK vk = new VK(tokens);
        Console console = new Console(tokens, vk);
        console.print("<token inserted>" + "\n<command>");
    }
}
