package telegram.Services.HttpWorkers.Senders;

import telegram.Models.User;

public class MessageHandler {
    private User user;
    private HttpSender httpSender;

    public MessageHandler(User user) {
        if (user == null) {
            // System.err.println("User is null. Cannot process the message.");
            return;
        }

        this.user = user;

        if (user.getUserId() == null || user.getUserId().isEmpty()) {
            System.err.println("User ID is missing. Cannot process the message.");
            return;
        }

        this.httpSender = new HttpSender("sendMessage", null, user.getUserId());
    }

    public void initialize() {
        
        if (user == null) {
            return; 
        }

        switch (user.getMessage()) {
            case "/start":
                httpSender.setMessageTextToSend("👋 <b>Hello</b>, " + user.getUsername() + "! Welcome to our bot. How can we assist you today?");
                httpSender.sendMessage();
                break;
            case "/getInfo":
                httpSender.setMessageTextToSend("ℹ️ <b>Your Info</b>\n- ID: <code>" + user.getUserId() + "</code>\n- Username: <b>" + user.getUsername() + "</b>\nCreator: <tg-spoiler>@Albrut</tg-spoiler>");
                httpSender.sendMessage();
                break;
            case "/help":
                httpSender.setMessageTextToSend("🆘 <b>Help Menu</b>\n- <code>/start</code>: Start the bot\n- <code>/getInfo</code>: Get your user info\n- <code>/about</code>: Learn more about this bot");
                httpSender.sendMessage();
                break;
            case "/about":
                httpSender.setMessageTextToSend("🤖 <b>About This Bot</b>\nCreated by <tg-spoiler>@Albrut</tg-spoiler>. This bot is designed to provide you with useful information and help you navigate through our services.");
                httpSender.sendMessage();
                break;
            default:
                httpSender.setMessageTextToSend("❓ <b>Unknown Command</b>\nPlease use <code>/help</code> to see the list of available commands.");
                httpSender.sendMessage();
        }
    }
}
