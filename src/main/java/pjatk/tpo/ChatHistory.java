package pjatk.tpo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatHistory {
    private static final Map<String, List<String>> chatHistories = new HashMap<>();

    public static void addMessage(String userId, String message) {
        List<String> userHistory = chatHistories.get(userId);
        if (userHistory == null) {
            userHistory = new ArrayList<>();
            chatHistories.put(userId, userHistory);
        }
        userHistory.add(message);
    }

    public static List<String> getHistory(String userId) {
        return chatHistories.getOrDefault(userId, new ArrayList<>());
    }
}
