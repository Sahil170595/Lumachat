package com.chatapp.ai_chat_app.service;

import com.mongodb.client.*;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

public class AIService {

    private static final String OLLAMA_API_URL = "http://localhost:11434/api/chat";
    private static final String MODEL_NAME = "gemma3";

    private final MongoCollection<Document> messageLog;

    public AIService() {
        // Connect to separate MongoDB database for AI history
        MongoClient client = MongoClients.create();
        MongoDatabase aiDB = client.getDatabase("ai_chat_history");
        this.messageLog = aiDB.getCollection("ai_messages");

        System.out.println("✅ Connected to AI chat history database.");
    }

    public String getAIResponse(String userMessage, String username) {
        try {
            // 1. Save user message
            logMessage(username, "user", userMessage);

            // 2. Prepare request
            JSONArray messagesArray = new JSONArray();
            messagesArray.put(new JSONObject()
                    .put("role", "user")
                    .put("content", userMessage));

            JSONObject payload = new JSONObject();
            payload.put("model", MODEL_NAME);
            payload.put("messages", messagesArray);

            // 3. Send request
            HttpURLConnection conn = (HttpURLConnection) new URL(OLLAMA_API_URL).openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.toString().getBytes(StandardCharsets.UTF_8));
            }

            // 4. Read response stream
            StringBuilder fullReply = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    JSONObject json = new JSONObject(line.trim());
                    if (json.has("message")) {
                        fullReply.append(json.getJSONObject("message").getString("content"));
                    }
                }
            }

            String aiReply = fullReply.toString().trim();

            // 5. Save AI reply
            logMessage(username, "assistant", aiReply);

            return aiReply;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Unable to contact AI.";
        }
    }

    private void logMessage(String user, String role, String content) {
        Document doc = new Document("user", user)
                .append("role", role)
                .append("content", content)
                .append("timestamp", Date.from(Instant.now()));
        messageLog.insertOne(doc);
    }
}
