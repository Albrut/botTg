package telegram.Models;


public class Message {

   private String message;
   private String chat_id;
   private String  parse_mode = "HTML";

   public Message(String message, String chat_id) {
    this.message = message;
    this.chat_id = chat_id;
    }
    


}