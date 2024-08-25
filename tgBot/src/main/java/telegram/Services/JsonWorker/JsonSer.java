package telegram.Services.JsonWorker;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonSer {

    private static final Gson gson = new Gson(); 

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static void messageToString(String response) {
        JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
        JsonArray resultArray = jsonObject.getAsJsonArray("result");
        if (resultArray != null) {
            for (JsonElement element : resultArray) {

                JsonObject messageObject = element.getAsJsonObject().getAsJsonObject("message");
                System.out.println(messageObject);
                if (messageObject.has("sticker")){
                    System.out.println("da");
                    return;
                }
                
                String messageId = messageObject.get("message_id").getAsString();
                // System.out.println(messageId);
                if (JsonList.getJsonList.contains(messageId)) {
                    System.out.println("Message with ID " + messageId + " already received.");
                } else {
                    String messageText = messageObject.get("text").getAsString();
                    JsonObject fromObject = messageObject.getAsJsonObject("from");
                    String username = fromObject.get("username").getAsString();
                    System.out.println("Message: " + messageText + " | Username: " + username);
                    JsonList.getJsonList.add(messageId);
                    System.out.println("Message: " + messageText);
                }
            }
        } else {
            System.out.println("No result array found in the JSON response.");
        }
    }
}
