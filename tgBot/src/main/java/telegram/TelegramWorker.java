package telegram;

import telegram.Services.HttpWorkers.Updaters.UpdateFetcher;

/**
 * TelegramWorker
 */
public class TelegramWorker {
    private static final UpdateFetcher updateFetcher = new UpdateFetcher();
    public static void startWork(){
//        String CHAT_ID = "509869919";
//        String CHAt_ID_BAYTIC_CHEL = "905723842";
//        String BOT_METHOD = "sendMessage";


        // Message message = new Message("<b>Hello</b>", CHAT_ID);
        // String jsonContent =JsonSer.toJson(message);
        // HttpSender sender = new HttpSender(BOT_METHOD,jsonContent);
        // sender.sendMessage();
        UpdateFetcher.startFetchingUpdates();

        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            updateFetcher.stopFetchingUpdates();
        }));
    }
}