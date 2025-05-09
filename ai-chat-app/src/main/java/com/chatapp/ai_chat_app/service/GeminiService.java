package com.chatapp.ai_chat_app.service;

import com.google.cloud.aiplatform.v1beta1.EndpointName;
import com.google.cloud.aiplatform.v1beta1.GenerateContentRequest;
import com.google.cloud.aiplatform.v1beta1.GenerateContentResponse;
import com.google.cloud.aiplatform.v1beta1.GenerationConfig;
import com.google.cloud.aiplatform.v1beta1.ModelName;
import com.google.cloud.aiplatform.v1beta1.Part;
import com.google.cloud.aiplatform.v1beta1.PublisherModelName;
import com.google.cloud.aiplatform.v1beta1.SafetySetting;
//import com.google.cloud.aiplatform.v1beta1.TextGenerationServiceClient;
//import com.google.cloud.aiplatform.v1beta1.TextGenerationSettings;
import com.google.protobuf.InvalidProtocolBufferException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GeminiService {

    private static final String API_KEY = System.getenv("GEMINI_API_KEY");
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;

    public GeminiService() {
        // ✅ Add this for debug:
        System.out.println("🔑 Gemini API Key Loaded: " + (API_KEY != null ? "[OK]" : "[NULL]"));
    }
    /**
     * Simple one-shot prompt
     */
    public String getAIResponse(String prompt, String username) {
        return getAIResponseWithContext(List.of(), prompt);
    }
    public void pingGemini() {
        try {
            System.out.println("⏳ Pinging Gemini...");

            // Basic message (valid structure)
            JSONObject part = new JSONObject().put("text", "Hello!");
            JSONArray parts = new JSONArray().put(part);

            JSONObject content = new JSONObject()
                .put("role", "user")
                .put("parts", parts);

            JSONArray contents = new JSONArray().put(content);
            JSONObject payload = new JSONObject().put("contents", contents);

            HttpURLConnection conn = (HttpURLConnection) new URL(GEMINI_URL).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.toString().getBytes(StandardCharsets.UTF_8));
            }

            int code = conn.getResponseCode();
            System.out.println("✅ Gemini responded with HTTP code: " + code);

            if (code == 200) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("📨 Gemini: " + line);
                    }
                }
            } else {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder error = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        error.append(line);
                    }
                    System.err.println("❌ Error: " + error);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Main Gemini handler: prompt + optional context
     */
    public String getAIResponseWithContext(List<String> history, String prompt) {
        try {
            // 1. Build conversation history
        	JSONArray contents = new JSONArray();

        	// Add history as user messages
        	for (String line : history) {
        	    contents.put(new JSONObject()
        	        .put("role", "user")
        	        .put("parts", new JSONArray().put(new JSONObject().put("text", line))));
        	}

        	// Add actual prompt
        	contents.put(new JSONObject()
        	    .put("role", "user")
        	    .put("parts", new JSONArray().put(new JSONObject().put("text", prompt))));

            JSONObject payload = new JSONObject().put("contents", contents);

            // 2. Send HTTP POST
            HttpURLConnection conn = (HttpURLConnection) new URL(GEMINI_URL).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.toString().getBytes(StandardCharsets.UTF_8));
            }

            // 3. Read response
            StringBuilder responseBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
            }

            // 4. Parse response
            JSONObject json = new JSONObject(responseBuilder.toString());
            JSONArray candidates = json.getJSONArray("candidates");
            if (!candidates.isEmpty()) {
                JSONObject content = candidates.getJSONObject(0).getJSONObject("content");
                JSONArray parts = content.getJSONArray("parts");
                return parts.getJSONObject(0).getString("text").trim();
            }

            return "⚠️ Gemini did not return a valid response.";

        } catch (Exception e) {
            e.printStackTrace();
            return "⚠️ Gemini API error: " + e.getMessage();
        }
    }
}
