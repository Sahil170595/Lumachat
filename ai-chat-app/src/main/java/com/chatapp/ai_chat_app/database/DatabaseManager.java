package com.chatapp.ai_chat_app.database;

import com.mongodb.client.*;
import org.bson.Document;

public class DatabaseManager {
    private static final String DB_NAME = "chatapp";
    private static final String USERS_COLLECTION = "users";

    private static DatabaseManager instance;
    private final MongoDatabase database;
    private final MongoCollection<Document> usersCollection;

    private DatabaseManager() {
        MongoClient mongoClient = MongoClients.create();
        this.database = mongoClient.getDatabase(DB_NAME);
        this.usersCollection = database.getCollection(USERS_COLLECTION);
        System.out.println("✅ MongoDB connected and users collection ready.");
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public boolean registerUser(String username, String password) {
        if (usersCollection.find(new Document("username", username)).first() != null) {
            return false; // User already exists
        }
        Document user = new Document("username", username).append("password", password);
        usersCollection.insertOne(user);
        return true;
    }

    public boolean authenticateUser(String username, String password) {
        Document query = new Document("username", username).append("password", password);
        return usersCollection.find(query).first() != null;
    }
} 
