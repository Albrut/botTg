package telegram.Services.HttpWorkers.Senders;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import telegram.Models.Message;
import telegram.Services.HttpWorkers.UrlGetter;
import telegram.Services.JsonWorker.JsonSer;

public abstract class HttpworkerAbstract {
    private String BOT_METHOD;
    private String messageTextToSend;
    private String chatId;
    private String jsonField;

    public HttpworkerAbstract(String BOT_METHOD, String messageTextToSend, String chatId) {
        this.BOT_METHOD = BOT_METHOD;
        this.messageTextToSend = messageTextToSend;
        this.chatId = chatId;
    }

    
    public String getBOT_METHOD() {
        return BOT_METHOD;
    }

    public void setBOT_METHOD(String BOT_METHOD) {
        this.BOT_METHOD = BOT_METHOD;
    }

    public String getMessageTextToSend() {
        return messageTextToSend;
    }

    public void setMessageTextToSend(String messageTextToSend) {
        this.messageTextToSend = messageTextToSend;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getJsonField() {
        return jsonField;
    }

    public void setJsonField(String jsonField) {
        this.jsonField = jsonField;
    }

    
    public void sendMessage() {
        Message message = new Message(messageTextToSend, chatId);
        jsonField = JsonSer.toJson(message);
        
        
        String url = UrlGetter.getUrl() + BOT_METHOD;    

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonField))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response body: " + response.body());
            System.out.println("Status code: " + response.statusCode());

        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
