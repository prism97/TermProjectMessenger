package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import singleChat.Client;
import singleChat.Server;

import java.io.*;
import java.util.*;


public class ProfileController {
    private Client c;
    private HashMap<String, String> acc = new HashMap<>();
    private Main main;
    private String user;
    private ArrayList<String> activeNotFriend = new ArrayList<>();


    @FXML
    private Label userName;

    @FXML
    private VBox suggestionBox;

    @FXML
    private VBox friendBox;


    void init(String msg) {
        c = new Client("127.0.0.1", 33333, msg);
        user = msg;
        userName.setText(msg + "'s Friendlist");
        showSuggestions(readFriendList());
        showFriends(readFriendList());
        //messageCounter();
        changeFriends();
        seeRequest();
    }

    @FXML
    void logoutAction() {
        try {
            c.getNc().write("Server:" + user + ":logout");
            main.showLoginPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    void createGroupAction() {
        ArrayList<String> activeFriends = new ArrayList<>();
        ArrayList<String> friends = readFriendList();
        for (String s : friends) {
            System.out.println("friend:" + s);
            if (activeNotFriend.contains(s + ":login") && !activeNotFriend.contains(s + ":logout")) {
                activeFriends.add(s);
                System.out.println("activeFriend:" + s);
            }
        }

        if(activeFriends.size() >= 2){
            try {
                main.showGroupchatSelection(activeFriends, user, c, c.getNc());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("You don't have enough active friends to start a group chat!");
            alert.showAndWait();
        }
    }


    @FXML
    void groupAction() {
        if (c.isGroupChatOn()) {
            if (c.messageMap.containsKey("NewGroup")) {
                String groupName = c.messageMap.get("NewGroup").get(0);
                try {
                    main.showGroupChatBox(user, groupName, c, c.getNc());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showFriends(ArrayList<String> friends) {
        friendBox.getChildren().removeAll();
        if (friends.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Sorry " + user + ", you don't have any friends yet. Please send a friend request to any of the suggested users and make new friends.");
            alert.showAndWait();
        } else {
            for (String name : friends) {
                Button fr = new Button(name);
                fr.setPrefWidth(220);
                fr.setPrefHeight(25);
                fr.setStyle("-fx-background-color: #0084ff;");
                fr.setTextFill(Color.WHITE);
                fr.getProperties().put(name, name);
                fr.setTextAlignment(TextAlignment.CENTER);
                fr.setFont(Font.font("Candara", FontWeight.BOLD, 14));
                fr.setVisible(true);
                fr.setOnAction(e -> chatAction(fr));
                friendBox.getChildren().add(fr);
            }
        }
    }


    private void changeFriends() {
        Runnable task = () -> {
            while (true) {
                if (c.messageMap.containsKey("Server") && !c.messageMap.get("Server").isEmpty()) {
                    ArrayList<String> friends = readFriendList();
                    for (String s : friends) {
                        if (c.messageMap.get("Server").contains(s + ":login")) {
                            activeNotFriend.add(s + ":login");
                            for (Node node : friendBox.getChildren()) {
                                if (node.getProperties().containsKey(s)) {
                                    Platform.runLater(() -> node.setStyle("-fx-background-color: #370072;"));
                                }
                            }
                        } else if (c.messageMap.get("Server").contains(s + ":logout")) {
                            activeNotFriend.add(s + ":logout");
                            for (Node node : friendBox.getChildren()) {
                                if (node.getProperties().containsKey(s)) {
                                    Platform.runLater(() -> node.setStyle("-fx-background-color: #0084ff;"));
                                }
                            }
                        } else if (c.messageMap.get("Server").contains(s + ":newFriend")) {
                            Platform.runLater(() -> suggestionBox.getChildren().removeIf(c -> c.getProperties().containsKey(s)));
                            Button fr = new Button(s);
                            fr.setPrefWidth(220);
                            fr.setPrefHeight(25);
                            fr.setStyle("-fx-background-color: #370072;");
                            fr.setTextFill(Color.WHITE);
                            fr.getProperties().put(s, s);
                            fr.setTextAlignment(TextAlignment.CENTER);
                            fr.setFont(Font.font("Candara", FontWeight.BOLD, 14));
                            fr.setVisible(true);
                            fr.setOnAction(e -> chatAction(fr));
                            Platform.runLater(() -> friendBox.getChildren().add(fr));
                        }
                    }

                    c.messageMap.get("Server").clear();
                }
            }
        };
        Thread cf = new Thread(task);
        cf.setDaemon(true);
        cf.start();

    }

    private void showSuggestions(ArrayList<String> friends) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(Server.INFO_FILE));
            String line;
            while ((line = br.readLine()) != null) {
                String[] keyvalue = line.split(":");
                boolean flag = false;
                if (!friends.isEmpty()) {
                    if (friends.contains(keyvalue[0])) {
                        flag = true;
                    }
                }
                if (!flag) {
                    acc.put(keyvalue[0], keyvalue[1]);
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (String key : acc.keySet()) {
            if (!key.equals(user)) {
                Button name = new Button(key);
                name.setPrefWidth(225);
                name.setPrefHeight(25);
                name.setStyle("-fx-background-color: white;");
                name.setTextAlignment(TextAlignment.CENTER);
                name.setTextFill(Color.valueOf("#370072"));
                name.setFont(Font.font("Candara", FontWeight.NORMAL, 14));
                name.setVisible(true);
                name.getProperties().put(key, key);
                name.setOnAction(e -> sendRequest(name));
                suggestionBox.getChildren().add(name);
            }
        }
    }

    private void chatAction(Button bt) {
        try {
            main.showChatBox(user, bt.getText(), c, c.getNc());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendRequest(Button bt) {
        ButtonType yes = new ButtonType("YES", ButtonBar.ButtonData.YES);
        ButtonType no = new ButtonType("NO", ButtonBar.ButtonData.NO);
        String question = "Are you sure you want to send " + bt.getText() + " a friend request?";
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, question, yes, no);
        alert.setHeaderText(null);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == yes) {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(Server.REQUEST_FILE, true));
                bw.append(bt.getText()).append(":").append(user).append("\n");
                bw.flush();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setHeaderText(null);
            ok.setContentText("Your friend request has been sent to " + bt.getText());
            ok.showAndWait();
        }
    }

    private void seeRequest() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(Server.REQUEST_FILE));
            String line;
            while ((line = br.readLine()) != null) {
                String[] x = line.split(":");
                if (x[0].equals(user)) {
                    ButtonType yes = new ButtonType("YES", ButtonBar.ButtonData.YES);
                    ButtonType no = new ButtonType("NO", ButtonBar.ButtonData.NO);
                    String question = "You have received a friend request from " + x[1] + "!\n" + "Do you want to accept it?";
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, question, yes, no);
                    alert.setHeaderText("New Friend Request!!");
                    Optional<ButtonType> result = alert.showAndWait();

                    if (result.isPresent() && result.get() == yes) {
                        System.out.println("yes received");
                        BufferedReader lr = new BufferedReader(new FileReader(Server.FRIEND_FILE));

                        BufferedReader rr = new BufferedReader(new FileReader(Server.REQUEST_FILE));

                        HashMap<String, String> requests = new HashMap<>();

                        String str;

                        while ((str = rr.readLine()) != null) {
                            String[] z = str.split(":");
                            requests.put(z[0], z[1]);
                        }

                        rr.close();

                        HashMap<String, String> friends = new HashMap<>();
                        int i = 0;
                        boolean flag = false;
                        boolean flag2 = false;
                        while ((str = lr.readLine()) != null) {
                            if (!(str.equals(""))) {
                                i++;
                                String[] y = str.split(":");
                                if (y[0].equals(user)) {
                                    flag = true;
                                    friends.put(y[0], (y[1] + x[1] + ","));
                                } else if (y[0].equals(x[1])) {
                                    flag2 = true;
                                    friends.put(y[0], (y[1] + x[0] + ","));
                                } else {
                                    friends.put(y[0], y[1]);
                                }
                            }
                        }
                        if (i == 0 || !flag) {
                            friends.put(user, (x[1] + ","));
                        }
                        if (i == 0 || !flag2) {
                            friends.put(x[1], (user + ","));
                        }

                        lr.close();

                        rewriteRequests(requests, line);
                        rewriteFriends(friends);
                        c.getNc().write("Server:" + x[1] + ":newFriend");
                        Platform.runLater(() -> suggestionBox.getChildren().removeIf(c -> c.getProperties().containsKey(x[1])));
                        Button fr = new Button(x[1]);
                        fr.setPrefWidth(220);
                        fr.setPrefHeight(25);
                        if (activeNotFriend.contains(x[1] + ":login") && !activeNotFriend.contains(x[1] + ":logout")) {  //here
                            fr.setStyle("-fx-background-color: #370072;");
                        } else {
                            fr.setStyle("-fx-background-color: #0084ff;");
                        }
                        fr.setTextFill(Color.WHITE);
                        fr.getProperties().put(x[1], x[1]);
                        fr.setTextAlignment(TextAlignment.CENTER);
                        fr.setFont(Font.font("Candara", FontWeight.BOLD, 14));
                        fr.setVisible(true);
                        fr.setOnAction(e -> chatAction(fr));
                        Platform.runLater(() -> friendBox.getChildren().add(fr));
                    }
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void rewriteRequests(HashMap<String, String> requests, String line) {
        try {
            BufferedWriter rw = new BufferedWriter(new FileWriter(Server.REQUEST_FILE));
            for (String key : requests.keySet()) {
                String str = key + ":" + requests.get(key);
                if (!(str.equals(line))) {
                    rw.write(str + "\n");
                    rw.flush();
                }
            }
            rw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void rewriteFriends(HashMap<String, String> friends) {
        try {
            BufferedWriter lw = new BufferedWriter(new FileWriter(Server.FRIEND_FILE));
            for (String key : friends.keySet()) {
                lw.write(key + ":" + friends.get(key) + "\n");
                lw.flush();
            }
            lw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> readFriendList() {
        ArrayList<String> friends = new ArrayList<>();
        String[] fr;
        try {
            BufferedReader br = new BufferedReader(new FileReader(Server.FRIEND_FILE));
            String line;
            while ((line = br.readLine()) != null) {
                String[] names = line.split(":");
                if (names[0].equals(user)) {
                    fr = names[1].split(",");
                    friends.addAll(Arrays.asList(fr));
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return friends;
    }

    /*public ArrayList<String> readActiveFriendList() {
        ArrayList<String> friends = readFriendList();
        ArrayList<String> activeFriends = new ArrayList<>();
        for (String s : friends) {
            if (s.)
        }
    }*/

    void setMain(Main main) {
        this.main = main;
    }

}
