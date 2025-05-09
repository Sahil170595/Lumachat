package com.chatapp.ai_chat_app.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.chatapp.ai_chat_app.App;
import com.chatapp.ai_chat_app.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class RegisterController implements Initializable{

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;
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
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("All fields are required.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return;
        }

        if (UserService.getInstance().userExists(username)) {
            showError("Username already taken.");
            return;
        }

        boolean success = UserService.getInstance().register(username, password);
        if (success) {
            try {
                App.loadLoginView(); // ✅ central app method with theme and layout
            } catch (Exception e) {
                showError("Navigation error. Try again.");
                e.printStackTrace();
            }
        } else {
            showError("Registration failed. Try again.");
        }
    }

    @FXML
    private void handleBackToLogin() {
        try {
            App.loadLoginView(); // ✅ central app method
        } catch (Exception e) {
            showError("Navigation error. Try again.");
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
    }
}
