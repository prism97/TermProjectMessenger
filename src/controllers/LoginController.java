package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import singleChat.Server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class LoginController {
    private Main main;

    @FXML
    private TextField userText;

    @FXML
    private PasswordField passwordText;

    @FXML
    void loginAction() throws Exception{
        HashMap<String, String> acc = new HashMap<>();

        try{
            BufferedReader br = new BufferedReader(new FileReader(Server.INFO_FILE));
            String line;
            while((line = br.readLine()) != null){
                String[] keyvalue = line.split(":");
                acc.put(keyvalue[0], keyvalue[1]);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        String userName = userText.getText();
        String password = passwordText.getText();

        boolean flag = false;
        if(acc.containsKey(userName)){
            flag = acc.get(userName).equals(password);
        }

        if(flag){
            main.showProfilePage(userName);
        }
        else {
            // failed login
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Incorrect Credentials");
            alert.setHeaderText("Incorrect Credentials");
            alert.setContentText("The username and password you provided is not correct.");
            alert.showAndWait();
        }
    }

    @FXML
    void signupAction() throws Exception {
        main.showSignupPage();
    }

    void setMain(Main main) {
        this.main = main;
    }

}
