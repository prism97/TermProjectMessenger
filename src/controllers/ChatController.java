package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import singleChat.Client;
import util.NetworkUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class ChatController {
    private String userName;
    private String partnerName;
    private NetworkUtil userNc;
    private Client client;


    @FXML
    private VBox vBox;

    @FXML
    private Text chatPartner;

    @FXML
    private Text me;

    @FXML
    private TextField message;


    private void startTask() {
        Runnable task = () -> {
            System.out.println("reading");
            HashMap<String, ArrayList<String>> message;
            try {
                while (true) {
                    message = client.messageMap;
                    if (message.containsKey(partnerName)) {
                        for (Iterator<String> iterator = client.messageMap.get(partnerName).iterator(); iterator.hasNext(); ) {
                            String m = iterator.next();
                            iterator.remove();
                            Label label = new Label(m);
                            label.setPadding(new Insets(5));
                            label.setFont(Font.font("Candara", FontWeight.BOLD, 14));
                            label.setTextFill(Color.WHITE);
                            label.setBackground(new Background(new BackgroundFill(Color.valueOf("#ffa0a0"), new CornerRadii(5), Insets.EMPTY)));
                            HBox hBox = new HBox();
                            hBox.setAlignment(Pos.BASELINE_LEFT);
                            hBox.getChildren().add(label);
                            Platform.runLater(() -> vBox.getChildren().add(hBox));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(partnerName + " readNc over");
            }
        };
        Thread readNc = new Thread(task);
        readNc.setDaemon(true);
        readNc.start();
    }


    void init(String un, String pn, Client client, NetworkUtil nc) {
        this.userName = un;
        this.partnerName = pn;
        this.userNc = nc;
        this.client = client;
        chatPartner.setText(partnerName);
        me.setText(userName);
        startTask();
    }

    public void sendAction() {
        try {
            if (client.isGroupChatOn()) {
                userNc.write("Group" + ":" + userName + ":" + message.getText());
            } else {
                userNc.write(userName + ":" + partnerName + ":" + message.getText());
            }

            Label label = new Label(message.getText());
            label.setPadding(new Insets(5));
            label.setFont(Font.font("Candara", FontWeight.BOLD, 14));
            label.setTextFill(Color.WHITE);
            label.setBackground(new Background(new BackgroundFill(Color.valueOf("#d696bb"), new CornerRadii(5), Insets.EMPTY)));
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.BASELINE_RIGHT);
            hBox.getChildren().add(label);
            vBox.getChildren().add(hBox);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        message.setText("");
        message.requestFocus();
    }

}
