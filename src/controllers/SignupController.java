package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import singleChat.Server;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class SignupController {
    private Main main;

    @FXML
    private TextField userText2;

    @FXML
    private TextField userEmail;

    @FXML
    private DatePicker userBD;

    @FXML
    private PasswordField passwordText2;

    @FXML
    private PasswordField confirmPassword;


    @FXML
    void signupAction() throws Exception {
        String userName = userText2.getText();
        String password = passwordText2.getText();
        String confirmPass = confirmPassword.getText();

        // Checking if password matches with confirm password
        if (password.equals(confirmPass)){
            BufferedWriter bw = new BufferedWriter(new FileWriter(Server.INFO_FILE, true));
            try{
                bw.append(userName).append(":").append(password).append("\n");
            }catch (IOException e){
                e.printStackTrace();
            }
            bw.close();
            main.showProfilePage(userName);
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Incorrect Credentials");
            alert.setHeaderText("Incorrect Credentials");
            alert.setContentText("Passwords must be identical.");
            alert.showAndWait();
        }
    }

    @FXML
    void backAction() {
        try {
            main.showLoginPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void resetAction() {
        userText2.setText(null);
        passwordText2.setText(null);
        userEmail.setText(null);
        confirmPassword.setText(null);
        userBD.setValue(null);
    }

    void setMain(Main main) {
        this.main = main;
    }
}
