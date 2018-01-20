package singleChat;

import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import util.NetworkUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class ReadThreadClient implements Runnable {
    private Client client;
    private NetworkUtil nc;
    private HashMap<String, ArrayList<String>> map;

    ReadThreadClient(Client client, NetworkUtil nc, HashMap<String, ArrayList<String>> map) {
        this.client = client;
        this.nc = nc;
        this.map = map;
        Thread thr = new Thread(this);
        thr.setDaemon(true);
        thr.start();
    }

    @Override
    public void run() {
        try {
            while (true) {
                String s = (String) nc.read();
                if (s != null) {
                    String[] br = s.split(":", 2);
                    String name = br[0];
                    String message = br[1];
                    ArrayList<String> arrayList;

                    if (name.equals("NewGroup")) {
                        if(!map.containsKey(name)){
                            System.out.println("gc found");
                            String[] x = message.split(":", 2);
                            arrayList = new ArrayList<>();
                            arrayList.add(x[1]);
                            map.put(name, arrayList);
                            client.setGroupChatOn(true);
                            nc.write(s);
                            System.out.println(s);
                        }
                    } else {
                        if (map.containsKey(name)) {
                            map.get(name).add(message);
                            if (!name.equals("Server")) {
                                notification();
                            }
                            System.out.println(s);
                        } else {
                            arrayList = new ArrayList<>();
                            arrayList.add(message);
                            if (!name.equals("Server")) {
                                notification();
                            }
                            map.put(name, arrayList);
                            System.out.println(s);
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            nc.closeConnection();
        }
    }


    private void notification() {
        File file = new File("Messenger Facebook Sound.mp3");
        System.out.println("Notification");
        Media musicFile = new Media(file.toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(musicFile);
        Platform.runLater(() -> mediaPlayer.setAutoPlay(true));
    }
}
