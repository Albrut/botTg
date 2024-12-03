package telegram.Services.HttpWorkers.Senders;

import telegram.DB.CategoryProductLoader;
import telegram.DB.RegLogBd;
import telegram.Models.User;
import telegram.Services.RegLogServices.RegService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageHandler {
    public User user;
    private HttpSender httpSender;
    private final List<CategoryProductLoader.Category> categories;
    private final CategoryProductLoader categoryProductLoader;

    public MessageHandler(User user) {
        categoryProductLoader = new CategoryProductLoader();
        categories = CategoryProductLoader.getCategories();
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
        } else if (message.startsWith("/categories")) {
            showCategories();
        } else if (message.startsWith("/products")) {
            showProductsInCategory(message);
        }
        else if(message.startsWith("/createProduct")){
            handleCreateProduct();
        }
     else if (message.startsWith("/createOrder")) {
        handleOrderCreation();}
    else {
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
        String[] messageParts = message.substring("/register ".length()).split(" ", 1);
        if (messageParts.length < 1) {
            httpSender.setMessageTextToSend("🔑 <b>Registration</b>\nPlease provide your details in the format:\n`password`\n\nExample: `/register password123`");
            httpSender.sendMessage();
            return;
        }
        String combinedMessage = String.join(" ", messageParts);
        if (RegService.register(user.getUsername() + " " + combinedMessage)) {
            httpSender.setMessageTextToSend("🎉 <b>Registration Successful!</b>\nWelcome, <b>" + user.getUsername() + "</b>! 🎈\nYou have successfully registered. Now you can <code>/login</code> to access your account. 🚀");
            httpSender.sendMessage();
        } else {
            httpSender.setMessageTextToSend("❌ <b>Registration Failed</b>\nOops, something went wrong during the registration process... 😔\nPlease try again or contact support if the issue persists. 📞");
            httpSender.sendMessage();
        }
    }

    private void handleLogin(String message) {
        String[] messageParts = message.substring("/login ".length()).split(" ", 1);
        if (messageParts.length < 1) {
            httpSender.setMessageTextToSend("🔐 <b>Login</b>\nPlease provide your login details in the format:\n`password`\n\nExample: password123`");
            httpSender.sendMessage();
            return;
        }
        String combinedMessage = String.join(" ", messageParts);
        if (RegService.login(user.getUsername() + " " + combinedMessage)) {
            httpSender.setMessageTextToSend("✅ <b>Login Successful</b>\nWelcome back, <b>" + user.getUsername() + "</b>! 🎉\nYou're now logged in and ready to explore all the features. 😊");
            httpSender.sendMessage();
        } else {
            httpSender.setMessageTextToSend("❌ <b>Login Failed</b>\nOops, something went wrong... 😔\nPlease check your username and password, and try again. 🔄");
            httpSender.sendMessage();
        }
    }

    private void showCategories() {
        if (!RegLogBd.isAuthenticated(user.getUsername())) {
            httpSender.setMessageTextToSend("❌ You need to log in to view categories. Please use the <code>/login</code> command.");
            httpSender.sendMessage();
            return;
        }
        StringBuilder message = new StringBuilder("📂 <b>Categories</b>\n");
        for (CategoryProductLoader.Category category : categories) {
            message.append("- <code>").append(category.getName()).append("</code>\n");
        }
        message.append("\nTo view products in a category, use <code>/products categoryName</code>");
        httpSender.setMessageTextToSend(message.toString());
        httpSender.sendMessage();
    }

    private void showProductsInCategory(String message) {
        if (!RegLogBd.isAuthenticated(user.getUsername())) {
            httpSender.setMessageTextToSend("❌ You need to log in to view products. Please use the <code>/login</code> command.");
            httpSender.sendMessage();
            return;
        }

        String[] messageParts = message.split(" ", 2);
        if (messageParts.length < 2) {
            httpSender.setMessageTextToSend("⚠️ Please specify a category. Example: `/products Chairs`");
            httpSender.sendMessage();
            return;
        }

        String categoryName = messageParts[1];
        CategoryProductLoader.Category selectedCategory = null;
        for (CategoryProductLoader.Category category : categories) {
            if (category.getName().equalsIgnoreCase(categoryName)) {
                selectedCategory = category;
                break;
            }
        }

        if (selectedCategory == null) {
            httpSender.setMessageTextToSend("❌ Category not found. Please use a valid category name.");
            httpSender.sendMessage();
            return;
        }

        List<CategoryProductLoader.Product> products = CategoryProductLoader.getProductsByCategory(selectedCategory.getId());
        if (products.isEmpty()) {
            httpSender.setMessageTextToSend("⚠️ No products found in the category " + categoryName + ".");
            httpSender.sendMessage();
            return;
        }

        StringBuilder productMessage = new StringBuilder("🛍️ <b>Products in " + categoryName + "</b>\n");
        for (CategoryProductLoader.Product product : products) {
            productMessage.append("- <b>").append(product.getName()).append("</b> - $").append(product.getPrice()).append("\n");
            productMessage.append("  <i>").append(product.getDescription()).append("</i>\n\n");
        }
        httpSender.setMessageTextToSend(productMessage.toString());
        httpSender.sendMessage();
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
        String helpMessage = """
                🆘 <b>Help Menu</b>
                - <code>/start</code>: Start the bot
                - <code>/getInfo</code>: Get your user info
                - <code>/about</code>: Learn more about this bot
                - <code>/register username password</code>: Register as a user
                - <code>/login password</code>: Login
                - <code>/categories</code>: View available categories
                - <code>/products categoryName</code>: View products in a specific category (requires login)
                - <code>/createOrder productId1,quantity1,productId2,quantity2</code>: Create an order (requires login)
                - <code>/createProduct categoryId,productName,productDescription,productPrice</code>: Create a new product (Admin only)
                """;

        httpSender.setMessageTextToSend(helpMessage);
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

    private void handleCreateProduct() {
        // Check if the user is authenticated
        if (!RegLogBd.isAuthenticated(user.getUsername())) {
            httpSender.setMessageTextToSend("❌ You need to log in to create products. Please use the <code>/login</code> command.");
            httpSender.sendMessage();
            return;
        }

        // Check if the user is an admin
        if (!CategoryProductLoader.isUserAdmin(user.getUsername())) {
            httpSender.setMessageTextToSend("❌ You are not authorized to create products. Only admins can do this.");
            httpSender.sendMessage();
            return;
        }

        // Get the user's message
        String message = user.getMessage();
        String messageContent = message.replace("/createProduct ", "").trim(); // Remove the command prefix
        String[] messageParts = messageContent.split(",", 4); // Split the remaining message into 4 parts

        if (messageParts.length < 4) {
            // Show categories if not enough parts are provided
            StringBuilder categoryMessage = new StringBuilder("🛠️ <b>Create Product</b>\nPlease provide product details in the format:\n\n<code>categoryId, productName, productDescription, productPrice</code>\n\n");
            List<CategoryProductLoader.Category> categories = CategoryProductLoader.getCategories();
            for (CategoryProductLoader.Category category : categories) {
                categoryMessage.append("- <code>").append(category.getId()).append("</code> - ").append(category.getName()).append("\n");
            }
            httpSender.setMessageTextToSend(categoryMessage.toString());
            httpSender.sendMessage();
            return;
        }

        // Parse the input
        String categoryInput = messageParts[0].trim();
        String productName = messageParts[1].trim();
        String productDescription = messageParts[2].trim();
        String productPriceString = messageParts[3].trim();

        // Find the selected category
        CategoryProductLoader.Category selectedCategory = null;
        try {
            int categoryId = Integer.parseInt(categoryInput); // Attempt to parse as ID
            for (CategoryProductLoader.Category category : CategoryProductLoader.getCategories()) {
                if (category.getId() == categoryId) {
                    selectedCategory = category;
                    break;
                }
            }
        } catch (NumberFormatException e) {
            // If parsing failed, match by name
            for (CategoryProductLoader.Category category : CategoryProductLoader.getCategories()) {
                if (category.getName().equalsIgnoreCase(categoryInput)) {
                    selectedCategory = category;
                    break;
                }
            }
        }

        if (selectedCategory == null) {
            httpSender.setMessageTextToSend("❌ Invalid category. Please select an existing category.");
            httpSender.sendMessage();
            return;
        }

        // Convert price to a valid number
        double productPrice;
        try {
            productPrice = Double.parseDouble(productPriceString);
        } catch (NumberFormatException e) {
            httpSender.setMessageTextToSend("⚠️ Invalid price format. Please enter a valid price.");
            httpSender.sendMessage();
            return;
        }

        // Create the product
        boolean isCreated = CategoryProductLoader.createProduct(user.getUsername(), productName, productDescription, productPrice, selectedCategory.getId());

        if (isCreated) {
            httpSender.setMessageTextToSend("✅ <b>Product Created!</b>\nYour product <b>" + productName + "</b> has been successfully added to the <b>" + selectedCategory.getName() + "</b> category.");
            httpSender.sendMessage();
        } else {
            httpSender.setMessageTextToSend("❌ Product creation failed. Please try again.");
            httpSender.sendMessage();
        }
    }

    private void handleOrderCreation() {

    }



}




