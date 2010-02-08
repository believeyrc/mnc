package client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Main implements EntryPoint {
	private Long time;
	private List<Message> messages = new ArrayList<Message>();
	private ChatServiceAsync chatSvc = GWT.create(ChatService.class);
	private ScrollPanel scrollPanel; 
	private VerticalPanel messagePanel = new VerticalPanel();
	public void onModuleLoad() {
		VerticalPanel verticalSplitPanel = new VerticalPanel();
		FlowPanel flowPanel = new FlowPanel();
		Button b = new Button("Click me", new ClickHandler() {
			public void onClick(ClickEvent event) {
				refreshMsg();
			}
		});
		flowPanel.add(b);
		scrollPanel = new ScrollPanel(messagePanel);
		verticalSplitPanel.add(scrollPanel);
		verticalSplitPanel.add(flowPanel);
		RootPanel rootPanel = RootPanel.get();
		rootPanel.setHeight("100%");

		FlowPanel chatPanel = new FlowPanel();
		final TextBox input = new TextBox();
		chatPanel.add(input);
		chatPanel.add(new Button("Send", new ClickHandler() {
			public void onClick(ClickEvent event) {
				chatSvc.sendMessage(input.getText(), new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {

					}

					public void onSuccess(String result) {
						refreshMsg();
					}
				});
				input.setText("");
				input.setFocus(true);
			}
		}));
		verticalSplitPanel.add(chatPanel);

		rootPanel.add(verticalSplitPanel);
	}

	private void updateMessages(List<Message> result) {
		for (Message msg : result) {
			messagePanel.add(new Label(msg.getContent()));
		}

	}

	private void refreshMsg() {
		chatSvc.loadMessages(0L, new AsyncCallback<List<Message>>() {
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}

			public void onSuccess(List<Message> result) {
				updateMessages(result);
			}
		});
	}
}