package telegram.Services.RegLogServices;

import telegram.DB.RegLogBd;
import telegram.Models.UserLogin;

public class RegService {

    /**
     * Регистрация нового пользователя.
     *
     * @param message строка с именем пользователя и паролем, разделенными пробелом.
     */
    public static void register(String message) {
        UserLogin userLogin = validateRegistrationMessage(message);

        if (userLogin == null) {
            System.err.println("Invalid registration message. Registration aborted.");
            return;
        }

        userLogin.setRole("user"); // Устанавливаем роль по умолчанию
        RegLogBd.registration(userLogin.getUsername(), userLogin.getPassword(), userLogin.getRole());
    }

    /**
     * Авторизация пользователя.
     *
     * @param message строка с именем пользователя и паролем, разделенными пробелом.
     */
    public static void login(String message) {
        UserLogin userLogin = validateRegistrationMessage(message);

        if (userLogin == null) {
            System.err.println("Invalid login message. Login aborted.");
            return;
        }

        RegLogBd.login(userLogin.getUsername(), userLogin.getPassword());
    }

    /**
     * Проверка и разбор сообщения для регистрации или авторизации.
     *
     * @param message строка с именем пользователя и паролем, разделенными пробелом.
     * @return объект UserLogin, если сообщение корректное; null в случае ошибки.
     */
    public static UserLogin validateRegistrationMessage(String message) {
        if (message == null || message.isBlank()) {
            System.err.println("Message is empty or null.");
            return null;
        }

        // Разделяем сообщение по пробелам
        String[] parts = message.trim().split("\\s+");

        // Проверяем формат сообщения
        if (parts.length != 2) {
            System.err.println("Invalid format. Expected: 'username password'.");
            return null;
        }

        // Создаем и заполняем объект UserLogin
        UserLogin userLogin = new UserLogin();
        userLogin.setUsername(parts[0]);
        userLogin.setPassword(parts[1]);

        return userLogin;
    }
}
