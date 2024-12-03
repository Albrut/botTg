package telegram.Services.JsonWorker;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import telegram.DB.Filtrator;
import telegram.Models.User;

public class JsonSer {

    private static final Gson gson = new Gson();
    private static Filtrator filterTg = new Filtrator();

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

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

                    String messageId = messageObject.has("message_id") ? messageObject.get("message_id").getAsString() : null;
                    String messageText = messageObject.has("text") ? messageObject.get("text").getAsString() : null;
                    String username = messageObject.has("from") && messageObject.getAsJsonObject("from").has("username") 
                                        ? messageObject.getAsJsonObject("from").get("username").getAsString() : null;
                    String chatId = messageObject.has("from") && messageObject.getAsJsonObject("from").has("id")
                                        ? messageObject.getAsJsonObject("from").get("id").getAsString() : null;




                    if (messageId != null && !filterTg.checkerMessageInDB(messageId, messageText, username)) {
                        return new User(messageText, chatId, username);
                    } else {
//                         System.out.println("Message with ID " + messageId + " already received or no ID found.");
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
