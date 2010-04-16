package org.gazi.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class reader implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network " + "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		SplitLayoutPanel main = new SplitLayoutPanel();
		final FlowPanel panel = new FlowPanel();
		final Image loader = new Image("http://www.jsxnc.com/public/css/galleriffic/loader.gif");
		panel.add(loader);
		final HorizontalPanel header = new HorizontalPanel();
		header.add(new Label("Reader"));
		final ListBox zoom = new ListBox();
		zoom.addItem("25%", "25");
		zoom.addItem("50%", "50");
		zoom.addItem("75%", "75");
		zoom.addItem("100%", "100");
		zoom.addItem("125%", "125");
		zoom.addItem("150%", "150");
		zoom.addItem("175%", "175");
		zoom.addItem("200%", "200");
		zoom.setSelectedIndex(3);
		header.add(zoom);
		
		final String pagedomain = "http://www.jsxnc.com";
		final String domain = "http://static.jsxnc.com";

		final FlowPanel navigation = new FlowPanel();

		for (int j = 0; j < 5; j++) {
			Image img = new Image(domain + "/public/books/2010/04/13/c2cfbcf2-c2a1-4f4a-bb1e-324e0f3e3ab3/file.pdf/../" + j
					+ "_thumbnail.jpg");
			navigation.add(img);
			final int page = j;
			ClickHandler handler = new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					// /reader/page?id=8&scale=100&page=0
					panel.add(loader);
					final Image image = new Image(pagedomain + "/reader/page?id=8&scale="
							+ zoom.getValue(zoom.getSelectedIndex()) + "&page=" + page);
					image.addLoadHandler(new LoadHandler() {
						@Override
						public void onLoad(LoadEvent event) {
							System.out.println("loaded");
							panel.remove(0);
						}
					});
					panel.add(image);
				}
			};
			img.addClickHandler(handler);
		}

		ScrollPanel scroll = new ScrollPanel(navigation);
		String height = Window.getClientHeight() + "px";
		main.setHeight(height);
		main.addNorth(header, 20);
		main.addWest(scroll, 160);
		main.add(new ScrollPanel(panel));

		RootPanel.get().add(main);
	}
}
