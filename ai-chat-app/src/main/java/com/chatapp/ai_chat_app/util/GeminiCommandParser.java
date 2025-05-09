package com.chatapp.ai_chat_app.util;

import java.util.List;

public class GeminiCommandParser {

    public static String parse(String rawMessage, List<String> history) {
        String cmd = rawMessage.trim().toLowerCase();

        if (cmd.startsWith("#summarize")) {
            return "Summarize this conversation:\n" + String.join("\n", history);
        }

        if (cmd.startsWith("#translate")) {
            String lang = cmd.replaceFirst("#translate", "").trim();
            return "Translate this conversation into " + lang + ":\n" + String.join("\n", history);
        }

        if (cmd.startsWith("#explain")) {
            return "Explain this clearly:\n" + cmd.replaceFirst("#explain", "").trim();
        }

        return rawMessage.replaceFirst("@Gemini|#AI", "").trim();
    }
}
