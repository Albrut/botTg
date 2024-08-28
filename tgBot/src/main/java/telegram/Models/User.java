package telegram.Models;

public class User {
    private String message;
    private String userId;
    private String username;
    public User(String message, String userId, String username) {
        this.message = message;
        this.userId = userId;
        this.username = username;
    }
    public String getMessage() {
        return message;
    }
    public String getUserId() {
        return userId;
    }
    public String getUsername() {
        return username;
    }
    
}
