package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import singleChat.Client;
import util.NetworkUtil;

import javafx.event.ActionEvent;

import java.util.ArrayList;


public class GroupController {
    private String userName;
    private Client client;
    private NetworkUtil nc;
    private Main main;
    private ArrayList<CheckBox> checkBoxes = new ArrayList<>();

    @FXML
    private TextField groupName;

    @FXML
    private VBox activeFriendBox;

    @FXML
    private Button startChat;


    void init(ArrayList<String> activeFriends, String userName, Client client, NetworkUtil nc) {
        this.userName = userName;
        this.client = client;
        this.nc = nc;
        showActiveFriends(activeFriends);
    }


    private void showActiveFriends(ArrayList<String> friends) {
        activeFriendBox.getChildren().removeAll();

        if (friends.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Sorry! You don't have any friend in your friendlist.");
            alert.showAndWait();
        } else {
            for (String friend : friends) {
                CheckBox cb = new CheckBox();
                cb.setText(friend);
                cb.setStyle("-fx-background-color: #370072;");
                cb.setTextFill(Color.WHITE);
                cb.setPrefSize(180, 20);
                activeFriendBox.getChildren().add(cb);
                checkBoxes.add(cb);
            }
            startChat.setOnAction(e -> handleOptions(checkBoxes));
        }
    }

    private void handleOptions(ArrayList<CheckBox> checkBoxes) {
        try {
            StringBuilder members = new StringBuilder(userName + ",");
            int i = 0;
            for (CheckBox cb : checkBoxes) {
                if (cb.isSelected()) {
                    i++;
                    members.append(cb.getText()).append(",");
                }
            }
            members.deleteCharAt(members.length() - 1);
            if((i >= 2) && !(groupName.getText().trim().equals(""))){
                nc.write("NewGroup:" + members + ":" + groupName.getText());
                client.setGroupChatOn(true);
                main.showGroupChatBox(userName, groupName.getText(), client, client.getNc());
            } else {
                if(i < 2){
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setHeaderText(null);
                    alert.setContentText("Choose at least two friends to create a group chat!");
                    alert.showAndWait();
                }
                if(groupName.getText().trim().equals("")){
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setHeaderText(null);
                    alert.setContentText("Please give a name to your group chat!");
                    alert.showAndWait();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goBack(ActionEvent event) {
        groupName.setText(null);
        for (CheckBox cb : checkBoxes) {
            if (cb.isSelected()) {
                cb.setSelected(false);
            }
        }
        ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
    }

    void setMain(Main main) {
        this.main = main;
    }
}
