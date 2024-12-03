package telegram.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class dbConnector {
    private static final String URL = "jdbc:mysql://localhost:3306/furniture_store ";
    private static final String username = "root";
    private static final String password = "1234";

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
