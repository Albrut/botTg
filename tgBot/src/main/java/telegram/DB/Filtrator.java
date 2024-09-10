package telegram.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Filtrator {
    private Connection connection;

    public Filtrator() {
        try {
            connection = dbConnector.gConnection();
        } catch (Exception e) {
            System.out.println("Error while connecting to the database: " + e.getMessage());
        }
    }

    public boolean checkerMessageInDB(String messageID, String message, String username) {
        String sqlSelect = "SELECT * FROM messages WHERE id = ?";
        String sqlInsert = "INSERT INTO messages (id, username, message) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatementSelect = connection.prepareStatement(sqlSelect);
             PreparedStatement preparedStatementInsert = connection.prepareStatement(sqlInsert)) {

            int messageIdInt = Integer.parseInt(messageID);
            preparedStatementSelect.setInt(1, messageIdInt);
            ResultSet resultSet = preparedStatementSelect.executeQuery();

            if (resultSet.next()) {
                // System.out.println("Message with id " + messageID + " already exists in the database.");
                return true;
            } else {
                preparedStatementInsert.setInt(1, messageIdInt);
                preparedStatementInsert.setString(2, username);
                preparedStatementInsert.setString(3, message);
                preparedStatementInsert.executeUpdate();
                System.out.println("Message with id " + messageID + " inserted into the database.");
                return false;
            }

        } catch (SQLException e) {
            // System.out.println("Error while accessing the database: " + e.getMessage());
            return true;
        }
    }
}
