package telegram.Services.HttpWorkers.Senders;

import telegram.Models.User;
import telegram.Models.UserLogin;
import telegram.Services.RegLogServices.RegService;

import java.util.Arrays;

public class MessageHandler {
    public User user;
    private HttpSender httpSender;

    public MessageHandler(User user) {
        if (user == null) {
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

        String message = user.getMessage();

        if (message.startsWith("/register")) {
            handleRegistration(message);
        } else if (message.startsWith("/login")) {
            handleLogin(message);
        } else {
            switch (message) {
                case "/start":
                    sendWelcomeMessage();
                    break;
                case "/getInfo":
                    sendUserInfo();
                    break;
                case "/help":
                    sendHelpMenu();
                    break;
                case "/about":
                    sendAboutMessage();
                    break;
                case "/products":
                    listProducts();
                    break;
//                case "/register":
//                    handleRegistration();
//                    break;
//                case "/login":
//                    handleLogin();
//                    break;
                case "/createProduct":
                    handleCreateProduct();
                    break;
                default:
                    sendUnknownCommand();
            }
        }
    }

    private void handleRegistration(String message) {
        if (message.length() <= "/register ".length()) {
            httpSender.setMessageTextToSend("🔑 <b>Registration</b>\nPlease provide your details in the format:\n`username password`\n\nExample: `/register user123 password123`");
            httpSender.sendMessage();
            return;
        }
        String[] messageParts = message.substring("/register ".length()).split(" ", 2);
        if (messageParts.length < 2) {
            httpSender.setMessageTextToSend("🔑 <b>Registration</b>\nPlease provide your details in the format:\n`username password`\n\nExample: `/register user123 password123`");
            httpSender.sendMessage();
            return;
        }
        String combinedMessage = String.join(" ", messageParts);
        RegService.register(combinedMessage);
    }

    private void handleLogin(String message) {
        // Убираем "/login " из начала строки
        String[] messageParts = message.substring("/login ".length()).split(" ", 2);
        if (messageParts.length < 2) {
            httpSender.setMessageTextToSend("🔐 <b>Login</b>\nPlease provide your login details in the format:\n`username password`\n\nExample: `user123 password123`");
            httpSender.sendMessage();
            return;
        }
        RegService.login(message);
    }


    private void sendWelcomeMessage() {
        httpSender.setMessageTextToSend("👋 <b>Hello</b>, " + user.getUsername() + "! Welcome to our bot. How can we assist you today?");
        httpSender.sendMessage();
    }

    private void sendUserInfo() {
        httpSender.setMessageTextToSend("ℹ️ <b>Your Info</b>\n- ID: <code>" + user.getUserId() + "</code>\n- Username: <b>" + user.getUsername() + "</b>\nCreator: <tg-spoiler>@Albrut</tg-spoiler>");
        httpSender.sendMessage();
    }

    private void sendHelpMenu() {
        httpSender.setMessageTextToSend("🆘 <b>Help Menu</b>\n- <code>/start</code>: Start the bot\n- <code>/getInfo</code>: Get your user info\n- <code>/about</code>: Learn more about this bot\n- <code>/register</code>: Register as a user\n- <code>/login</code>: Login\n- <code>/products</code>: View available products\n- <code>/createProduct</code>: Create a new product");
        httpSender.sendMessage();
    }

    private void sendAboutMessage() {
        httpSender.setMessageTextToSend("🤖 <b>About This Bot</b>\nCreated by <tg-spoiler>@Albrut</tg-spoiler>. This bot is designed to provide you with useful information and help you navigate through our services.");
        httpSender.sendMessage();
    }

    private void sendUnknownCommand() {
        httpSender.setMessageTextToSend("❓ <b>Unknown Command</b>\nPlease use <code>/help</code> to see the list of available commands.");
        httpSender.sendMessage();
    }

    private void handleRegistration() {
        String[] messageParts = user.getMessage().split("/register ", 2);
        if (messageParts.length < 2) {
            httpSender.setMessageTextToSend("🔑 <b>Registration</b>\nPlease provide your details in the format:\n`username password`\n\nExample: `user123 password123`");
            httpSender.sendMessage();
            return;
        }
        String message = messageParts[1];
        RegService.register(message);
    }

    private void handleLogin() {
        String[] messageParts = user.getMessage().split(" ", 2);
        if (messageParts.length < 2) {
            httpSender.setMessageTextToSend("🔐 <b>Login</b>\nPlease provide your login details in the format:\n`username password`\n\nExample: `user123 password123`");
            httpSender.sendMessage();
            return;
        }
    }

    private void listProducts() {
        String products = "🛒 <b>Our Products</b>\n1. Product A - $10\n2. Product B - $15\n3. Product C - $20\nTo create a new product, use <code>/createProduct</code>.";
        httpSender.setMessageTextToSend(products);
        httpSender.sendMessage();
    }

    private void handleCreateProduct() {
        String[] messageParts = user.getMessage().split(" ", 2);
        if (messageParts.length < 2) {
            httpSender.setMessageTextToSend("🛠️ <b>Create Product</b>\nPlease provide the product name and price in the format:\n`ProductName Price`\n\nExample: `ProductD 25`");
            httpSender.sendMessage();
            return;
        }

        String productDetails = messageParts[1];
        String[] details = productDetails.split("\\s+", 2);

        if (details.length != 2) {
            httpSender.setMessageTextToSend("⚠️ Invalid product format. Example: `ProductD 25`");
            httpSender.sendMessage();
            return;
        }

        String productName = details[0];
        String productPrice = details[1];

        try {
            double price = Double.parseDouble(productPrice);
            // Here you would call the database logic to save the product.
            httpSender.setMessageTextToSend("✅ Product " + productName + " created successfully with price $" + price);
        } catch (NumberFormatException e) {
            httpSender.setMessageTextToSend("⚠️ Invalid price. Please enter a valid number. Example: `ProductD 25`");
        }
        httpSender.sendMessage();
    }
}
