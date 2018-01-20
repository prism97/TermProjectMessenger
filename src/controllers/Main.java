package controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import singleChat.Client;
import util.NetworkUtil;
import java.util.ArrayList;

public class Main extends Application {
    private Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        showLoginPage();
    }

    void showLoginPage() throws Exception {
        // XML Loading using FXMLLoader
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("login.fxml"));
        Parent root = loader.load();

        // Loading the controller
        LoginController controller = loader.getController();
        controller.setMain(this);

        // Set the primary stage
        stage.setTitle("Login");
        stage.setScene(new Scene(root));
        stage.show();
    }

    void showSignupPage() throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("signup1.fxml"));
        Parent root = loader.load();

        // Loading the controller
        SignupController controller = loader.getController();
        controller.setMain(this);

        // Set the primary stage
        stage.setTitle("Signup");
        stage.setScene(new Scene(root));
        stage.show();
    }

    void showProfilePage(String userName) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("profile.fxml"));
        Parent root = loader.load();

        ProfileController controller = loader.getController();
        try {
            controller.init(userName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        controller.setMain(this);

        stage.setTitle("Profile");
        stage.setScene(new Scene(root));
        stage.show();
    }

    void showChatBox(String userName, String partnerName, Client client, NetworkUtil nc) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("chatbox.fxml"));
        Parent root = loader.load();

        ChatController controller = loader.getController();
        controller.init(userName, partnerName, client, nc);

        Stage chatWindow = new Stage();
        chatWindow.setTitle("Chatbox");
        chatWindow.setScene(new Scene(root));
        chatWindow.show();
    }

    void showGroupchatSelection (ArrayList<String> activeFriends, String userName, Client client, NetworkUtil nc) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("groupchat.fxml"));
        Parent root = loader.load();

        GroupController controller = loader.getController();
        controller.init(activeFriends, userName, client, nc);
        controller.setMain(this);

        Stage stage1 = new Stage();
        stage1.setTitle("Groupchat");
        stage1.setScene(new Scene(root));
        stage1.show();
    }

    void showGroupChatBox(String userName, String partnerName, Client client, NetworkUtil nc) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("groupchatbox.fxml"));
        Parent root = loader.load();

        GroupChatController controller = loader.getController();
        controller.init(userName, partnerName, client, nc);

        Stage chatWindow = new Stage();
        chatWindow.setTitle("GroupChat");
        chatWindow.setScene(new Scene(root));
        chatWindow.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
