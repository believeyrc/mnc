package client;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ChatServiceAsync {

  void loadMessages(Long time, AsyncCallback<List<Message>> callback);
  void sendMessage(String message,AsyncCallback<String> callback);
}