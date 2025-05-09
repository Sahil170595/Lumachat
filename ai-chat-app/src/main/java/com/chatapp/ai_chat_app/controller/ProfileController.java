package com.chatapp.ai_chat_app.controller;

import com.chatapp.ai_chat_app.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class ProfileController {

    @FXML private PasswordField oldPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label statusLabel;
    @FXML private Label usernameLabel;

    private String currentUser;

    public void setCurrentUser(String username) {
        this.currentUser = username;
        if (usernameLabel != null) {
            usernameLabel.setText("@" + username);
        }
    }

    @FXML
    private void handleUpdatePassword() {
        if (oldPasswordField == null || newPasswordField == null || confirmPasswordField == null || statusLabel == null) {
            return;
        }

        String oldPass = oldPasswordField.getText();
        String newPass = newPasswordField.getText();
        String confirmPass = confirmPasswordField.getText();

        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            statusLabel.setText("All fields are required.");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            statusLabel.setText("New passwords do not match.");
            return;
        }

        if (UserService.getInstance().updatePassword(currentUser, oldPass, newPass)) {
            statusLabel.setText("✅ Password updated successfully.");
        } else {
            statusLabel.setText("❌ Incorrect current password.");
        }
    }

    @FXML
    private void handleBackToChat() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
            Parent root = loader.load();

            MainController controller = loader.getController();
            controller.setCurrentUser(currentUser);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/dark-theme.css").toExternalForm());

            if (oldPasswordField != null && oldPasswordField.getScene() != null) {
                Stage stage = (Stage) oldPasswordField.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("LumaChat - Chat");
                stage.show();
            }

        } catch (IOException | NullPointerException e) {
            if (statusLabel != null) {
                statusLabel.setText("⚠️ Navigation error. Try again.");
            }
            e.printStackTrace();
        }
    }
}
