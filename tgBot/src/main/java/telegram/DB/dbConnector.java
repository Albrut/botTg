package telegram.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class dbConnector {
    private static final String URL = "jdbc:postgresql://localhost:5433/telegram_bot";
    private static final String username = "postgres";
    private static final String password = "123";

    public static Connection gConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, username, password);
            if (connection != null) {
                System.out.println("Connection to the database is successful!");
            }
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database: " + e.getMessage());
        }
        return connection;
    }
}
