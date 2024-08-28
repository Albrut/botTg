package telegram.Services.HttpWorkers.Senders;

/**
 * HttpSender
 */
public class HttpSender extends HttpworkerAbstract{

    public HttpSender(String BOT_METHOD, String messageTextToSend, String chatId) {
        super( BOT_METHOD,messageTextToSend,chatId);
    }
    
    
}