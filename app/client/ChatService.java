package client;
 
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
 
@RemoteServiceRelativePath("chat")
public interface ChatService extends RemoteService {
 
	String sendMessage(String message);
    
    List<Message> loadMessages(Long time);
    
}
