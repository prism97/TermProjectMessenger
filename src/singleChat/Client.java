package singleChat;

import util.NetworkUtil;
import java.util.ArrayList;
import java.util.HashMap;

public class Client {
    private NetworkUtil nc;
    public HashMap<String, ArrayList<String>> messageMap;
    private boolean groupChatOn;

    public Client(String serverAddress, int serverPort, String clientName) {
        messageMap = new HashMap<>();
        try {
            System.out.println(serverAddress);
            nc = new NetworkUtil(serverAddress, serverPort);
            nc.write(clientName);
            new ReadThreadClient(this, nc, messageMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setGroupChatOn(boolean groupChatOn) {
        this.groupChatOn = groupChatOn;
    }

    public boolean isGroupChatOn() {
        return groupChatOn;
    }

    public NetworkUtil getNc() {
        return nc;
    }
}

