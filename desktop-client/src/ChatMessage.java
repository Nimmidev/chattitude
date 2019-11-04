
class ChatMessage {

    private int id;
    private String message;
    private byte[] attachment;
    private long timestamp;

    public ChatMessage(int id, String messsage, byte[] attachment, long timestamp){
        this.id = id;
        this.message = messsage;
        this.attachment = attachment;
        this.timestamp = timestamp;
    }

}