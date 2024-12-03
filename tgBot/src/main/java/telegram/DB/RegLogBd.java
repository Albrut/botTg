package telegram.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegLogBd {
    static Connection connection;

    static {
        connection = dbConnector.gConnection();
    }

    // Регистрация пользователя
    public static void registration(String username, String password, String role) {
        String checkUserSql = "SELECT * FROM Users WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(checkUserSql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("User " + username + " is already registered.");
                return;
            } else {
                String insertSql = "INSERT INTO Users (username, password, role) VALUES (?, ?, ?)";
                try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                    insertStmt.setString(1, username);
                    insertStmt.setString(2, password);  // Сохраняем пароль в открытом виде
                    insertStmt.setString(3, role);
                    insertStmt.executeUpdate();
                    System.out.println("User " + username + " registered successfully.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Логин пользователя
    public static boolean login(String username, String password) {
        String checkUserSql = "SELECT * FROM Users WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(checkUserSql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password"); // Получаем пароль из базы данных


                if (storedPassword.equals(password)) {
                    System.out.println("Login successful!");
                    return true;
                } else {
                    System.out.println("Invalid password.");
                    return false;
                }
            } else {
                System.out.println("User " + username + " not found.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
