package com.chatapp.ai_chat_app.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

public class MessageBubble extends HBox {

    public MessageBubble(String text, boolean isSentByUser) {
        Label message = new Label(text);
        message.getStyleClass().addAll("bubble", isSentByUser ? "bubble-right" : "bubble-left");
        message.setWrapText(true);
        message.setMaxWidth(400);
        message.setMinHeight(Region.USE_PREF_SIZE);

        this.getChildren().add(message);
        this.setAlignment(isSentByUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        this.setMaxWidth(Double.MAX_VALUE);
        this.getStyleClass().add("chat-bubble-wrapper"); // optional for further tuning
    }
}
