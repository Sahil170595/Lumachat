package com.chatapp.ai_chat_app.controller;

import com.chatapp.ai_chat_app.App;
import com.chatapp.ai_chat_app.service.AIService;
import com.chatapp.ai_chat_app.service.ChatDatabaseService;
import com.chatapp.ai_chat_app.service.FriendService;
import com.chatapp.ai_chat_app.service.GeminiService;

import com.chatapp.ai_chat_app.util.GeminiCommandParser;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainController {

    @FXML private ListView<String> friendsList;
    @FXML private VBox chatContainer;
    @FXML private ScrollPane chatScrollPane;
    @FXML private TextField inputField;
    @FXML private Label chatTitle;
    @FXML private TextField friendInputField;

    private final ChatDatabaseService messageService = ChatDatabaseService.getInstance();
    private final AIService aiService = new AIService();
    private final FriendService friendService = FriendService.getInstance();
    private final GeminiService geminiService = new GeminiService();  

    private String currentFriend;
    private String currentUser;

    private final SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");

    @FXML
    public void initialize() {
        friendsList.setOnMouseClicked(this::handleFriendSelect);
        inputField.setOnAction(event -> handleSend());
        geminiService.pingGemini();
    }

    public void setCurrentUser(String username) {
        this.currentUser = username;
        chatTitle.setText("Welcome " + username + "! Select a friend to chat.");
        refreshFriendList();
    }

    private void refreshFriendList() {
        List<String> friends = friendService.getFriends(currentUser);
        if (!friends.contains("AI Assistant")) {
            friends.add("AI Assistant");
        }
        friendsList.setItems(FXCollections.observableArrayList(friends));
    }

    private void handleFriendSelect(MouseEvent event) {
        String selected = friendsList.getSelectionModel().getSelectedItem();
        if (selected != null && !selected.equals(currentFriend)) {
            currentFriend = selected;
            chatTitle.setText("Chatting with " + selected);
            loadChatHistory(selected);
        }
    }

    private void loadChatHistory(String friend) {
        chatContainer.getChildren().clear();
        List<String> messages = messageService.getMessages(currentUser, friend);
        for (String msg : messages) {
            boolean isSentByUser = msg.contains(currentUser + ":");
            addMessageBubble(msg, isSentByUser, new Date());
        }
        scrollToBottom();
    }

    private void addMessageBubble(String text, boolean isSentByCurrentUser, Date timestamp) {
        // Chat bubble
        Label bubble = new Label(text);
        bubble.setWrapText(true);
        bubble.setMaxWidth(360);
        bubble.getStyleClass().add("bubble");
        bubble.getStyleClass().add(isSentByCurrentUser ? "bubble-right" : "bubble-left");

        if (!isSentByCurrentUser && "AI Assistant".equals(currentFriend)) {
            bubble.getStyleClass().add("ai");
        }

        // Timestamp below bubble
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm"); // e.g., "14:27"
        Label timestampLabel = new Label(timeFormat.format(timestamp));
        timestampLabel.getStyleClass().add("timestamp");

        VBox messageBox = new VBox(bubble, timestampLabel);
        messageBox.setAlignment(isSentByCurrentUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

        HBox wrapper = new HBox(messageBox);
        wrapper.setMaxWidth(Double.MAX_VALUE);
        wrapper.setAlignment(isSentByCurrentUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        wrapper.setPadding(new Insets(5));

        chatContainer.getChildren().add(wrapper);
        
        if (!isSentByCurrentUser && text.contains("Gemini Assistant:")) {
            bubble.getStyleClass().add("gemini");
        }

    }


    private void scrollToBottom() {
        Platform.runLater(() -> {
            double scrollPos = chatScrollPane.getVvalue();
            double threshold = 0.95;  // If user is at 95% or more down the scroll

            if (scrollPos >= threshold) {
                chatScrollPane.setVvalue(1.0);  // Scroll to bottom
            }
            // else: do nothing, user is reviewing past messages
        });
    }


    @FXML
    private void handleSend() {
        String message = inputField.getText().trim();
        if (message.isEmpty()) return;

        if (currentFriend == null) {
            chatTitle.setText("⚠️ Select a friend to start chatting.");
            return;
        }

        String timeStr = formatter.format(new Date());
        String fullMessage = String.format(" %s: %s", currentUser, message);
        addMessageBubble(fullMessage, true, new Date());
        scrollToBottom();
        messageService.saveMessage(currentUser, currentFriend, fullMessage);
        inputField.clear();

        if ("AI Assistant".equals(currentFriend)) {
            new Thread(() -> {
                String aiReply = aiService.getAIResponse(message, currentUser);
                String replyTime = formatter.format(new Date());
                String fullReply = String.format("%s", aiReply);
                messageService.saveMessage("AI Assistant", currentUser, aiReply);
                Platform.runLater(() -> {
                    if (!"AI Assistant".equals(currentFriend)) return;
                    addMessageBubble(fullReply, false, new Date());
                    scrollToBottom();
                });
            }).start();
        }
        
        if (message.startsWith("@Gemini") || message.startsWith("#AI") || message.startsWith("#")) {
            new Thread(() -> {
                List<String> history = messageService.getRecentMessages(currentUser, currentFriend, 5); // last 5 messages
                String parsedPrompt = GeminiCommandParser.parse(message, history);

                String reply = geminiService.getAIResponseWithContext(history, parsedPrompt);
                String fullReply = String.format("[%s] Gemini Assistant: %s", formatter.format(new Date()), reply);
                messageService.saveMessage(currentFriend, currentUser, fullReply);

                Platform.runLater(() -> {
                    addMessageBubble(fullReply, false, new Date());
                    scrollToBottom();
                });
            }).start();
            return;
        }

    }

    @FXML
    private void handleAddFriend() {
        String newFriend = friendInputField.getText().trim();
        if (newFriend.isEmpty()) {
            chatTitle.setText("Enter a username to add.");
            return;
        }
        if (newFriend.equals(currentUser)) {
            chatTitle.setText("You cannot add yourself.");
            friendInputField.clear();
            return;
        }
        if (friendService.addFriend(currentUser, newFriend)) {
            chatTitle.setText("✅ Friend added: " + newFriend);
            refreshFriendList();
        } else {
            chatTitle.setText("❌ Failed to add friend: " + newFriend);
        }
        friendInputField.clear();
    }

    @FXML
    private void handleRemoveFriend() {
        String selected = friendsList.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        if (selected.equals("AI Assistant")) {
            chatTitle.setText("You cannot remove AI Assistant.");
            return;
        }

        if (friendService.removeFriend(currentUser, selected)) {
            chatTitle.setText("❌ Friend removed: " + selected);
            refreshFriendList();
            if (selected.equals(currentFriend)) {
                chatContainer.getChildren().clear();
                currentFriend = null;
                chatTitle.setText("Friend removed. Select someone else to chat.");
            }
        } else {
            chatTitle.setText("⚠️ Could not remove friend: " + selected);
        }
    }

    @FXML
    private void handleOpenProfile() {
        try {
            App.loadProfileView(currentUser);
        } catch (Exception e) {
            e.printStackTrace();
            chatTitle.setText("⚠️ Failed to open profile: " + e.getMessage());
        }
    }
}
