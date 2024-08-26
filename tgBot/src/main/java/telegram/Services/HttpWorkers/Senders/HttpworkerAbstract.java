package telegram.Services.HttpWorkers.Senders;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import telegram.Models.User;
import telegram.Services.HttpWorkers.UrlGetter;

public abstract class HttpworkerAbstract {
    private String BOT_METHOD;
    private String jsonContent;
    private User user;
    

    public HttpworkerAbstract(String BOT_METHOD, String jsonContent) {
        this.jsonContent = jsonContent;
        this.BOT_METHOD = BOT_METHOD;
        this.user = null;
    }
    public HttpworkerAbstract(String BOT_METHOD, String jsonContent, User user) {
        this.jsonContent = jsonContent;
        this.BOT_METHOD = BOT_METHOD;
        this.user = user;
    }

    

    
    public void sendMessage() {
        if(user == null){
        try {
            
            String url = UrlGetter.getUrl() + "" + BOT_METHOD;
            System.out.println(url);
    
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonContent))
                    .build();
    
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Status code: " + response.body());
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    }else{
        String url = UrlGetter.getUrl() + "" + "sendMessage";
        System.out.println(url);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(jsonContent))
        .build();
    }
}
}
