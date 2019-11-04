import java.util.ArrayList;
import java.util.List;

abstract class Chat {

    private int id;
    private List<ChatMessage> history;

    public Chat(int id) {
        this.id = id;
        history = new ArrayList<>();
    }

    public List<ChatMessage> getHistory(){
        return history;
    }

    public abstract String getTitle();

}