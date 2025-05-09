package com.chatapp.ai_chat_app.service;

import com.mongodb.client.*;
import org.bson.Document;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.Optional;

public class UserService {
    private static final String DB_NAME = "chatapp";
    private static final String COLLECTION_NAME = "users";

    private static UserService instance;
    private final MongoCollection<Document> users;

    private UserService() {
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase(DB_NAME);
        this.users = database.getCollection(COLLECTION_NAME);
        System.out.println("✅ MongoDB connected and 'users' collection ready.");
    }

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    public boolean register(String username, String password) {
        if (userExists(username)) return false;

        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        Document doc = new Document("username", username)
                .append("password", hashed)
                .append("friends", new ArrayList<String>());
        users.insertOne(doc);
        return true;
    }

    public boolean login(String username, String password) {
        Optional<Document> userDoc = Optional.ofNullable(users.find(new Document("username", username)).first());
        return userDoc.map(doc -> {
            String storedHash = doc.getString("password");
            return BCrypt.checkpw(password, storedHash);
        }).orElse(false);
    }

    public boolean userExists(String username) {
        return users.find(new Document("username", username)).first() != null;
    }

    public boolean updatePassword(String username, String oldPassword, String newPassword) {
        Document userDoc = users.find(new Document("username", username)).first();
        if (userDoc == null) return false;

        String storedHash = userDoc.getString("password");
        if (!BCrypt.checkpw(oldPassword, storedHash)) return false;

        String newHashed = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        users.updateOne(
                new Document("username", username),
                new Document("$set", new Document("password", newHashed))
        );
        return true;
    }
}
