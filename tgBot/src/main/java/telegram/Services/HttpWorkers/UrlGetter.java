package telegram.Services.HttpWorkers;

import javax.crypto.SecretKey;
//import telegram.EncryptionUtils;

public class UrlGetter {
    public static String getUrl() {

        return String.format("https://api.telegram.org/bot%s/", "7324919968:AAHHo8nlSkXbiPhiEIV1WwMOyfMLHUwGx-c");
//        try {
//            SecretKey key = EncryptionUtils.getStaticKey();
//            String encryptedToken = "n73KxrAAdH9oiZVqagNec8rRYWnjyy34f8KcLCPkT6O4b+soz76+It7qS+gE8xne";
//            String decryptedToken = EncryptionUtils.decrypt(encryptedToken, key);
//            return String.format("https://api.telegram.org/bot%s/", decryptedToken);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
    }
}
