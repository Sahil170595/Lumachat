package com.chatapp.ai_chat_app;

import com.chatapp.ai_chat_app.controller.MainController;
import com.chatapp.ai_chat_app.controller.ProfileController;
import com.chatapp.ai_chat_app.database.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        try {
            primaryStage = stage;

            // Initialize database
            DatabaseManager.getInstance();

            // Load initial view
            loadLoginView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadLoginView() throws Exception {
        loadScene("/fxml/Login.fxml", "LumaChat - Login", 600, 800);
    }

    public static void loadRegisterView() throws Exception {
        loadScene("/fxml/RegisterView.fxml", "LumaChat - Register", 600, 800);
    }

    public static void loadMainView(String username) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/MainView.fxml"));
        Parent root = loader.load();

        MainController controller = loader.getController();
        controller.setCurrentUser(username);

        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(App.class.getResource("/css/dark-theme.css").toExternalForm());

        primaryStage.setTitle("LumaChat - Chat");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void loadProfileView(String username) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/ProfileView.fxml"));
        Parent root = loader.load();

        ProfileController controller = loader.getController();
        controller.setCurrentUser(username);

        Scene scene = new Scene(root, 600, 500);
        scene.getStylesheets().add(App.class.getResource("/css/dark-theme.css").toExternalForm());

        primaryStage.setTitle("Profile - " + username);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void loadScene(String fxmlPath, String title, int width, int height) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxmlPath));
        Parent root = loader.load();

        Scene scene = new Scene(root, width, height);
        scene.getStylesheets().add(App.class.getResource("/css/dark-theme.css").toExternalForm());

        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
