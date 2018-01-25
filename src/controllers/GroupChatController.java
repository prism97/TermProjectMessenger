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

public class GroupChatController {
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
            ArrayList<String> message = new ArrayList<>();
            try {
                while (true) {
                    if (client.messageMap.containsKey(partnerName) && !client.messageMap.get(partnerName).isEmpty()) {
                        message.addAll(client.messageMap.get(partnerName));
                        client.messageMap.get(partnerName).clear();
                        for (String m : message) {
                            System.out.println(m);
                            String[] br = m.split(":", 2);
                            Font titleFont = Font.loadFont(getClass().getResource("OpenSansEmoji.ttf").toExternalForm(), 15);
                            Label nameLabel = new Label(br[0]);
                            nameLabel.setPadding(new Insets(5));
                            nameLabel.setFont(Font.font("Candara", FontWeight.BOLD, 12));
                            nameLabel.setTextFill(Color.WHITE);
                            nameLabel.setBackground(new Background(new BackgroundFill(Color.valueOf("#ff0f4f"), new CornerRadii(5), Insets.EMPTY)));
                            Label msgLabel = new Label(emojiConversion(br[1]));
                            msgLabel.setPadding(new Insets(5));
                            msgLabel.setFont(titleFont);
                            msgLabel.setTextFill(Color.WHITE);
                            msgLabel.setBackground(new Background(new BackgroundFill(Color.valueOf("#ffa0a0"), new CornerRadii(5), Insets.EMPTY)));
                            VBox gcMsg = new VBox();
                            gcMsg.setAlignment(Pos.BASELINE_LEFT);
                            gcMsg.getChildren().add(nameLabel);
                            gcMsg.getChildren().add(msgLabel);
                            Platform.runLater(() -> vBox.getChildren().add(gcMsg));
                        }
                        message.clear();
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
            userNc.write(partnerName + ":" + userName + ":" + message.getText());
            Font titleFont = Font.loadFont(getClass().getResource("OpenSansEmoji.ttf").toExternalForm(), 15);
            Label label = new Label(emojiConversion(message.getText()));
            label.setPadding(new Insets(5));
            label.setFont(titleFont);
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

    private String emojiConversion(String s) {
        String x = s;
        int i = 0;
        String[] emo = {"TT", "-_-", ":d", ":p", ":o", ":/"};
        String[] jr = {"\uD83D\uDE2D", "\uD83D\uDE11", "\uD83D\uDE03", "\uD83D\uDE1B", "\uD83D\uDE2E", "\uD83D\uDE15"};
        for (String e : emo) {
            if (x.contains(e)) {
                x = x.replaceAll(e, jr[i]);
            }
            i++;
        }

        for (int j = 1; j < s.length(); j++) {
            if (s.charAt(j) == ')') {
                switch (s.charAt(j - 1)) {
                    case ':':
                        x = x.replaceAll(":\\)", "\u263A");
                        break;
                    case ';':
                        x = x.replaceAll(";\\)", "\uD83D\uDE09");
                        break;
                }
            } else if (s.charAt(j) == '(' && s.charAt(j - 1) == ':') {
                x = x.replaceAll(":\\(", "\u2639");
            }
        }
        return x;
    }

}
