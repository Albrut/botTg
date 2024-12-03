package telegram.DB;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.Map;

public class RegLogBd {
    static Connection connection;

    static {
        connection = dbConnector.gConnection();
    }

    // Регистрация пользователя
    public static boolean registration(String username, String password, String role) {
        String checkUserSql = "SELECT * FROM Users WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(checkUserSql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("User " + username + " is already registered.");
                return false;
            } else {
                String insertSql = "INSERT INTO Users (username, password, role) VALUES (?, ?, ?)";
                try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                    insertStmt.setString(1, username);
                    insertStmt.setString(2, password);  // Сохраняем пароль в открытом виде
                    insertStmt.setString(3, role);
                    insertStmt.executeUpdate();
                    System.out.println("User " + username + " registered successfully.");
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    // Логин пользователя
    public static boolean login(String username, String password) {
        String checkUserSql = "SELECT * FROM users WHERE username = ?";
        String updateAuthStatusSql = "UPDATE users SET is_auth = TRUE WHERE username = ?"; // Запрос для обновления поля is_auth

        try (PreparedStatement ps = connection.prepareStatement(checkUserSql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password"); // Получаем пароль из базы данных

                if (storedPassword.equals(password)) {
                    // При успешном входе обновляем поле is_auth на TRUE
                    try (PreparedStatement updatePs = connection.prepareStatement(updateAuthStatusSql)) {
                        updatePs.setString(1, username);
                        int rowsUpdated = updatePs.executeUpdate();
                        if (rowsUpdated > 0) {
                            System.out.println("Login successful! User is now authorized.");
                            return true;
                        } else {
                            System.out.println("Failed to update authorization status.");
                        }
                    }
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

    // Логаут пользователя
    public static boolean logout(String username) {
        String checkUserSql = "SELECT * FROM users WHERE username = ?";
        String updateAuthStatusSql = "UPDATE users SET is_auth = FALSE WHERE username = ?";

        try (PreparedStatement ps = connection.prepareStatement(checkUserSql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Пользователь найден, обновляем поле is_auth на FALSE
                try (PreparedStatement updatePs = connection.prepareStatement(updateAuthStatusSql)) {
                    updatePs.setString(1, username);
                    int rowsUpdated = updatePs.executeUpdate();
                    if (rowsUpdated > 0) {
                        System.out.println("Logout successful! User is now deauthorized.");
                        return true;
                    } else {
                        System.out.println("Failed to update authorization status.");
                        return false;
                    }
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

    // Проверка авторизации пользователя
    public static boolean isAuthenticated(String username) {
        String checkAuthStatusSql = "SELECT is_auth FROM users WHERE username = ?";

        try (PreparedStatement ps = connection.prepareStatement(checkAuthStatusSql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getBoolean("is_auth"); // Возвращаем статус авторизации
            } else {
                System.out.println("User " + username + " not found.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean createOrderWithItems(String username, Map<Integer, Integer> products) {
        String createOrderSql = "INSERT INTO orders (user_id, status, order_date) VALUES ((SELECT id FROM users WHERE username = ?), 'pending', NOW())";
        String addItemSql = "INSERT INTO orderitems (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
        Connection conn = null;

        try {
            conn = connection; // Используем соединение из класса
            conn.setAutoCommit(false); // Отключаем автофиксацию

            // Шаг 1: Создаём заказ
            int orderId;
            try (PreparedStatement createOrderStmt = conn.prepareStatement(createOrderSql, Statement.RETURN_GENERATED_KEYS)) {
                createOrderStmt.setString(1, username);
                int rowsAffected = createOrderStmt.executeUpdate();

                if (rowsAffected > 0) {
                    ResultSet rs = createOrderStmt.getGeneratedKeys();
                    if (rs.next()) {
                        orderId = rs.getInt(1);
                    } else {
                        throw new SQLException("Failed to retrieve order ID.");
                    }
                } else {
                    throw new SQLException("Failed to create order.");
                }
            }

            // Шаг 2: Добавляем товары в заказ
            try (PreparedStatement addItemStmt = conn.prepareStatement(addItemSql)) {
                for (Map.Entry<Integer, Integer> entry : products.entrySet()) {
                    int productId = entry.getKey();
                    int quantity = entry.getValue();

                    // Получаем цену товара
                    double price = getProductPrice(productId);
                    if (price < 0) {
                        throw new SQLException("Product not found: ID = " + productId);
                    }

                    addItemStmt.setInt(1, orderId);
                    addItemStmt.setInt(2, productId);
                    addItemStmt.setInt(3, quantity);
                    addItemStmt.setDouble(4, price * quantity);
                    addItemStmt.addBatch();
                }

                int[] itemResults = addItemStmt.executeBatch();
                for (int result : itemResults) {
                    if (result == PreparedStatement.EXECUTE_FAILED) {
                        throw new SQLException("Failed to add item to order.");
                    }
                }
            }

            conn.commit(); // Фиксируем изменения
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Откатываем изменения в случае ошибки
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Восстанавливаем автофиксацию
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return false;
    }

    // Метод для получения цены товара
    private static double getProductPrice(int productId) {
        String sql = "SELECT price FROM products WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, productId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("price");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Если товар не найден
    }

}
