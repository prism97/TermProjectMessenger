package singleChat;

import util.NetworkUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class ReadThreadServer implements Runnable {
    private NetworkUtil nc;
    private HashMap<String, NetworkUtil> map;
    private String clientName;
    private HashMap<String, ArrayList<String>> groupMap = new HashMap<>();

    ReadThreadServer(HashMap<String, NetworkUtil> map, NetworkUtil nc, String clientName) {
        this.nc = nc;
        this.map = map;
        this.clientName = clientName;
        Thread thr = new Thread(this);
        thr.start();
    }

    public void run() {
        try {
            while (true) {
                String s = (String) nc.read();
                if (s != null) {
                    String[] br = s.split(":", 3);
                    if (br[0].equals("Server")) {
                        if (br[2].equals("logout")) {
                            nc.closeConnection();
                            for (String m : map.keySet()) {
                                if (!m.equals(clientName)) {
                                    map.get(m).write("Server:" + clientName + ":logout");
                                }
                            }
                            map.remove(clientName);
                        } else if (br[2].equals("newFriend")) {
                            map.get(br[1]).write("Server:" + clientName + ":newFriend");
                        }
                    } else if (br[0].equals("NewGroup")) {
                        String[] mem = br[1].split(",");
                        if (!groupMap.containsKey(br[2])) {
                            System.out.println("gc created");
                            ArrayList<String> members = new ArrayList<>();
                            for (String x : mem) {
                                if(!x.equals(clientName)){
                                    map.get(x).write(s);
                                    members.add(x);
                                    System.out.println(x);
                                }
                            }
                            groupMap.put(br[2], members);
                        }
                    } else if (br[0].equals(clientName) && map.containsKey(br[1])) {
                        NetworkUtil partnerNc = map.get(br[1]);
                        partnerNc.write(br[0] + ":" + br[2]);
                        System.out.println("to " + br[1] + " " + "from " + br[0] + ":" + br[2]);
                    } else {
                        if (groupMap.containsKey(br[0])) {
                            for (String gm : groupMap.get(br[0])) {
                                map.get(gm).write(s);
                            }
                            System.out.println("Group Message:" + br[1] + ":" + br[2]);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("connection closed");
            nc.closeConnection();
        }
    }
}




