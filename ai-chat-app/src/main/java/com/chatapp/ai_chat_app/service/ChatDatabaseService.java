package com.chatapp.ai_chat_app.service;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;

import java.util.Collections;

import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatDatabaseService {

    private static final String DB_NAME = "chatapp";
    private static final String COLLECTION_NAME = "messages";

    private static ChatDatabaseService instance;
    private final MongoCollection<Document> messages;
    private final MongoDatabase database;

    public static ChatDatabaseService getInstance() {
        if (instance == null) {
            instance = new ChatDatabaseService();
        }
        return instance;
    }

    private ChatDatabaseService() {
        MongoClient mongoClient = MongoClients.create();
        database = mongoClient.getDatabase(DB_NAME);
        messages = database.getCollection(COLLECTION_NAME);
        System.out.println("✅ MongoDB connected and 'messages' collection ready.");
    }

    public void saveMessage(String sender, String recipient, String content) {
        System.out.println("→ Saving message from '" + sender + "' to '" + recipient + "' with content: " + content);
        Document doc = new Document("sender", sender)
                .append("recipient", recipient)
                .append("timestamp", new Date())
                .append("content", content);
        messages.insertOne(doc);
    }

    public List<String> getMessages(String user1, String user2) {
        List<String> result = new ArrayList<>();

        messages.find(new Document("$or", List.of(
                new Document("sender", user1).append("recipient", user2),
                new Document("sender", user2).append("recipient", user1)
        ))).sort(new Document("timestamp", 1))
        .forEach(doc -> {
            Object rawTimestamp = doc.get("timestamp");
            String time = rawTimestamp instanceof Date ? rawTimestamp.toString() : "unknown time";
            String line = String.format("[%s] %s: %s",
                    time,
                    doc.getString("sender"),
                    doc.getString("content"));
            result.add(line);
        });

        return result;
    }
    
    public List<String> getRecentMessages(String user1, String user2, int limit) {
        List<String> messagesList = new ArrayList<>();
        FindIterable<Document> docs = messages.find(Filters.or(
                Filters.and(Filters.eq("sender", user1), Filters.eq("recipient", user2)),
                Filters.and(Filters.eq("sender", user2), Filters.eq("recipient", user1))
            ))
            .sort(Sorts.descending("timestamp"))
            .limit(limit);

        for (Document doc : docs) {
            String sender = doc.getString("sender");
            String content = doc.getString("content");
            messagesList.add(sender + ": " + content);
        }

        Collections.reverse(messagesList); // Oldest first
        return messagesList;
    }


    public MongoCollection<Document> getUsersCollection() {
        return database.getCollection("users");
    }
} 