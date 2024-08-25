package telegram.Services.HttpWorkers.Senders;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import telegram.Services.HttpWorkers.UrlGetter;

public abstract class HttpworkerAbstract {
    private String BOT_METHOD;
    private String jsonContent;

    public HttpworkerAbstract(String BOT_METHOD, String jsonContent) {
        this.jsonContent = jsonContent;
        this.BOT_METHOD = BOT_METHOD;
    }

    

    
    public void sendMessage() {
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
    }
}
