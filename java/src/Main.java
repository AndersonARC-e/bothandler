import java.io.BufferedReader;
import java.io.FileReader;

public class Main {
    public static void main(String[] args){
        String[] tokens = null;
        String managerToken = null;
        String spyToken = null;
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader("tokens.vk"));
            tokens = bufferedReader.readLine().split(";");
            bufferedReader = new BufferedReader(new FileReader("manager.vk"));
            managerToken = bufferedReader.readLine();
            bufferedReader = new BufferedReader(new FileReader("spy.vk"));
            spyToken = bufferedReader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        VK vk = new VK(managerToken);
        VK spy = new VK(spyToken);
        assert tokens != null;
        Handler handler = new Handler(tokens, managerToken);
        Console console = new Console(vk, spy, handler, true, 322715959, 560153110);
        console.print("<token inserted>" + "\n<command>");
    }
}
