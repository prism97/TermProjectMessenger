package singleChat;

import util.NetworkUtil;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {
    private HashMap<String, NetworkUtil> clientMap;
    public static final String INFO_FILE = "Account.txt";
    public static final String REQUEST_FILE = "FriendRequest.txt";
    public static final String FRIEND_FILE = "FriendList.txt";

    private Server() {
        clientMap = new HashMap<>();

        try {
            ServerSocket serverSocket = new ServerSocket(33333);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                serve(clientSocket);
            }
        } catch (Exception e) {
            System.out.println("Server starts:" + e);
        }
    }

    private void serve(Socket clientSocket) {
        NetworkUtil nc = new NetworkUtil(clientSocket);
        String clientName = (String) nc.read();

        for(String s : clientMap.keySet()){
            nc.write("Server:" + s + ":login");
        }

        clientMap.put(clientName, nc);
        addActive(clientName);
        new ReadThreadServer(clientMap, nc, clientName);
    }

    private void addActive(String name){
        for(String s : clientMap.keySet()) {
            if(!s.equals(name)) {
                clientMap.get(s).write("Server:" + name + ":login");
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
    }
}
