package bd.com.siba.siba_diuhelper.Model;

public class Message {
    private String messageId;
    private String message;
    private long messageTime;
    private String senderId;

    public Message() {
    }

    public Message(String messageId, String message, long messageTime, String senderId) {
        this.messageId = messageId;
        this.message = message;
        this.messageTime = messageTime;
        this.senderId = senderId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}
