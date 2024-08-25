package telegram.Services.HttpWorkers.Updaters;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import telegram.Services.HttpWorkers.UrlGetter;
import telegram.Services.JsonWorker.JsonSer;

public class GetUpdates {

    public static String getUpdates() {
        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            String url = UrlGetter.getUrl() + "getUpdates";
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonSer.messageToString(response.body());

            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
