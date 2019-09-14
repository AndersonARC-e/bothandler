public class Handler {
    VK manager;
    String[] tokens;
    VK[] vk;

    public Handler(String[] tokens, String managerToken){
        this.tokens = tokens;
        manager = new VK(managerToken);
        vk = new VK[tokens.length];
        for (int i = 0; i < tokens.length; i++){
            vk[i] = new VK(tokens[i]);
        }
    }

    public String writeToChat(String message, int peerID) {
        StringBuilder response = new StringBuilder();
        for (VK value : vk) {
            response.append(value.writeToChat(message, peerID));
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return response.toString();
    }

    public String writeToChat(String message, String domain) {
        int peerID = manager.getUser(domain).id;
        StringBuilder response = new StringBuilder();
        for (VK value : vk) {
            response.append(value.writeToChat(message, peerID));
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return response.toString();
    }

    public String joinChat(String link) {
        StringBuilder response = new StringBuilder();
        for (VK value : vk) {
            response.append(value.joinChat(link));
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return response.toString();
    }
}
