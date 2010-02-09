package controllers;

import java.util.HashMap;
import java.util.List;

import models.Channel;
import models.Message;

public class Channelv extends Basez {
	public static void channel(String username, Long id) {
		Channel channel = Channel.findById(id);
		render(channel);
	}

	public static void update(Long id,Long state,int direct) {
		Channel channel = Channel.findById(id);
		List<Message> messages = null; 
		if(direct>0)
			messages = channel.getMessagesAfter(state);
		else {
			messages = channel.getMessagesBefore(state);
		}
		System.out.println(direct);
		HashMap<String, Object> messageState = new HashMap<String, Object>();
		if (messages.size() == 0)
			messageState.put("state", state);
		else 
			messageState.put("state", messages.get(messages.size() - 1).longTime);
		messageState.put("messages", messages);
		renderJSON(messageState);
	}

	public static void sendMessage(Long id, String nickname, String content) {
		Channel channel = Channel.findById(id);
		Message message = new Message();
		if (getLoginUser() != null) {
			message.author = getLoginUser();
		}
		message.nickname = nickname;
		message.content = content;
		message.channel = channel;
		message.save();
		renderJSON(message);
	}
}
