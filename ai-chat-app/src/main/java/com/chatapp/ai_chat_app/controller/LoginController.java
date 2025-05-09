package com.chatapp.ai_chat_app.controller;

import com.chatapp.ai_chat_app.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;
    @FXML private ImageView logoView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Image logo = new Image(getClass().getResourceAsStream("/images/lumachat_logo_only.png"));
            logoView.setImage(logo);
        } catch (Exception e) {
            System.err.println("Failed to load logo image: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (UserService.getInstance().login(username, password)) {
            loadMainView(username);
        } else {
            messageLabel.setText("Invalid username or password.");
        }
    }

    @FXML
    private void handleRegisterRedirect() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RegisterView.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/dark-theme.css").toExternalForm());

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Register");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadMainView(String username) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
            Parent root = loader.load();

            MainController controller = loader.getController();
            controller.setCurrentUser(username);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/dark-theme.css").toExternalForm());

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Chat - " + username);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
