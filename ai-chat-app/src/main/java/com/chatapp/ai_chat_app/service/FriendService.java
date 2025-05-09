package com.chatapp.ai_chat_app.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FriendService {
    private final MongoCollection<Document> usersCollection;
    private static FriendService instance;

    private FriendService() {
        this.usersCollection = ChatDatabaseService.getInstance().getUsersCollection();
    }

    public static FriendService getInstance() {
        if (instance == null) {
            instance = new FriendService();
        }
        return instance;
    }

    public boolean addFriend(String currentUser, String friendUsername) {
        if (!userExists(friendUsername) || currentUser.equals(friendUsername)) return false;

        usersCollection.updateOne(
            Filters.eq("username", currentUser),
            Updates.addToSet("friends", friendUsername)
        );
        return true;
    }

    public boolean removeFriend(String currentUser, String friendUsername) {
        usersCollection.updateOne(
            Filters.eq("username", currentUser),
            Updates.pull("friends", friendUsername)
        );
        return true;
    }

    public List<String> getFriends(String username) {
        Optional<Document> userDoc = Optional.ofNullable(usersCollection.find(Filters.eq("username", username)).first());
        return userDoc.map(doc -> doc.getList("friends", String.class)).orElseGet(ArrayList::new);
    }

    private boolean userExists(String username) {
        return usersCollection.find(Filters.eq("username", username)).first() != null;
    }
}