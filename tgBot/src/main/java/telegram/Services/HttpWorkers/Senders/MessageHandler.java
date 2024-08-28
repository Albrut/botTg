package telegram.Services.HttpWorkers.Senders;

import telegram.Models.User;

public class MessageHandler {
    private User user;
    private HttpSender httpSender;

    public MessageHandler(User user) {
        // Handle the case where the user is null
        if (user == null) {
            System.err.println("User is null. Cannot process the message.");
            return;
        }

        this.user = user;

        // Skip processing if the user has no ID
        if (user.getUserId() == null || user.getUserId().isEmpty()) {
            System.err.println("User ID is missing. Cannot process the message.");
            return;
        }

        this.httpSender = new HttpSender("sendMessage", null, user.getUserId());
    }

    public void initialize() {
        // Check if user is null before proceeding
        if (user == null || user.getUserId() == null || user.getUserId().isEmpty()) {
            return; // Skip message handling if user is null or ID is missing
        }

        switch (user.getMessage()) {
            case "/start":
                httpSender.setMessageTextToSend("👋 *Hello*, " + user.getUsername() + "! Welcome to our bot. How can we assist you today?");
                httpSender.sendMessage();
                break;
            case "/getInfo":
                httpSender.setMessageTextToSend("ℹ️ *Your Info*\n- ID: `" + user.getUserId() + "`\n- Username: *" + user.getUsername() + "*\nCreator: [@Albrut](https://t.me/Albrut)");
                httpSender.sendMessage();
                break;
            case "/help":
                httpSender.setMessageTextToSend("🆘 *Help Menu*\n- `/start`: Start the bot\n- `/getInfo`: Get your user info\n- `/about`: Learn more about this bot");
                httpSender.sendMessage();
                break;
            case "/about":
                httpSender.setMessageTextToSend("🤖 *About This Bot*\nCreated by [@Albrut](https://t.me/Albrut). This bot is designed to provide you with useful information and help you navigate through our services.");
                httpSender.sendMessage();
                break;
            default:
                httpSender.setMessageTextToSend("❓ *Unknown Command*\nPlease use `/help` to see the list of available commands.");
                httpSender.sendMessage();
        }
    }
}
