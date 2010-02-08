package services;
 
import java.util.ArrayList;
import java.util.List;

import play.cache.Cache;
import play.modules.gwt.GWTService;
import play.modules.gwt.GWTServicePath;
import client.ChatService;
import client.Message;
 
@GWTServicePath("/app/main/chat")
public class ChatServiceImpl extends GWTService implements ChatService {
  
	public String sendMessage(String message) {
		System.out.println(message);
		ArrayList<Message> messages = (ArrayList<Message>) Cache.get("messages");
		if(messages==null) {
			System.out.println("add to cache");
			messages = new ArrayList<Message>();
			Cache.add("messages", messages,"10mn");
		}
		Message e = new Message();
		e.setContent(message);
		messages.add(e);
		System.out.println("get====================");
		return message;
	}

	public List<Message> loadMessages(Long time) {
		ArrayList<Message> messages = (ArrayList<Message>) Cache.get("messages");
		System.out.println(messages);
		if(messages==null)
			messages = new ArrayList<Message>(0);
		return messages;
	}
 
}
