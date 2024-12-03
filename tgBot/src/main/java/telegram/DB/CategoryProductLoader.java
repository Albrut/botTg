package telegram.DB;

import java.sql.*;
import java.util.*;

public class CategoryProductLoader {
    static Connection connection;

    static {
        connection = dbConnector.gConnection();
    }

    // Метод для получения всех категорий
    public static List<Category> getCategories() {
        String sql = "SELECT id, name FROM categories";
        List<Category> categories = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                categories.add(new Category(id, name));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }

    // Метод для получения товаров по категории
    public static List<Product> getProductsByCategory(int categoryId) {
        String sql = "SELECT id, name, description, price FROM products WHERE category_id = ?";
        List<Product> products = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int productId = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                double price = rs.getDouble("price");
                products.add(new Product(productId, name, description, price));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    // Метод для проверки роли пользователя
    public static boolean isUserAdmin(String username) {
        String sql = "SELECT role FROM users WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String role = rs.getString("role");
                return "admin".equalsIgnoreCase(role);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Метод для создания нового товара
    public static boolean createProduct(String username, String name, String description, double price, int categoryId) {
        if (!isUserAdmin(username)) {
            System.out.println("User does not have admin privileges.");
            return false;  // User must be admin to create a product
        }

        String sql = "INSERT INTO products (name, description, price, category_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, description);
            ps.setDouble(3, price);
            ps.setInt(4, categoryId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Добавляем метод для создания заказа с элементами



    // Класс для категории
    public static class Category {
        private int id;
        private String name;

        public Category(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    // Класс для товара
    public static class Product {
        private int id;
        private String name;
        private String description;
        private double price;

        public Product(int id, String name, String description, double price) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public String getDescription() {
            return description;
        }
    }
}
