package telegram.Services.JsonWorker;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import telegram.Models.User;

public class JsonSer {

    private static final Gson gson = new Gson();

    // Преобразование объекта в JSON
    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    // Метод для обработки сообщения и возвращения объекта User
    public static User messageToString(String response) {
        JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
        JsonArray resultArray = jsonObject.getAsJsonArray("result");
        
        if (resultArray != null) {
            for (JsonElement element : resultArray) {
                JsonObject messageObject = element.getAsJsonObject().getAsJsonObject("message");

                // Проверка на наличие только текстового контента без медиа файлов
                if (messageObject.has("text") &&
                    !messageObject.has("audio") &&
                    !messageObject.has("document") &&
                    !messageObject.has("photo") &&
                    !messageObject.has("video") &&
                    !messageObject.has("voice") &&
                    !messageObject.has("sticker") &&
                    !messageObject.has("animation")) {

                    String messageId = messageObject.get("message_id").getAsString();

                    
                    if (JsonList.getJsonList.contains(messageId)) {
                        System.out.println("Message with ID " + messageId + " already received.");
                    } else {
                        String messageText = messageObject.get("text").getAsString();
                        JsonObject fromObject = messageObject.getAsJsonObject("from");
                        String username = fromObject.get("username").getAsString();
                        String chatId = fromObject.get("id").getAsString();

                        
                        User user = new User(messageText, chatId, username);
                        System.out.println("Message: " + messageText + " | Username: " + username + " | Chat ID: " + chatId);
                        JsonList.getJsonList.add(messageId); 
                        return user;
                    }
                } else {
                    System.out.println("Message contains non-text content. Skipping...");
                }
            }
        } else {
            System.out.println("No result array found in the JSON response.");
        }
        
        return null;
    }
}
