package telegram.Models;

public class User {
    private String message;
    private String userId;
    private String username;
    private boolean isAuthenticated;
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
    public boolean isAuthenticated() {
        return isAuthenticated;
    }
    public void setAuthenticated(){
        isAuthenticated = true;
    }
    
}
