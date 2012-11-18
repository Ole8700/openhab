package org.openhab.ui.gwt.client.node;

import org.openhab.ui.gwt.client.ui.UIBase;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;
import com.googlecode.mgwt.ui.client.widget.ScrollPanel;

public class NodeViewGwtImpl implements NodeView {

	private LayoutPanel main;
	private HTML title;
	private FlowPanel content;
	private ScrollPanel scrollPanel;

	public NodeViewGwtImpl() {
		main = new LayoutPanel();

		HeaderPanel headerPanel = new HeaderPanel();
		title = new HTML();

		headerPanel.setCenterWidget(title);
		main.add(headerPanel);

		scrollPanel = new ScrollPanel();
		main.add(scrollPanel);

		content = new FlowPanel();
		scrollPanel.setWidget(content);
	}

	@Override
	public Widget asWidget() {
		return main;
	}

	@Override
	public HasText getTitle() {
		return title;
	}

	@Override
	public void add(UIBase element) {
		Widget w = (Widget) element.getWidget();
		content.add(w);
	}

	@Override
	public void refresh() {
		scrollPanel.refresh();
		
	}

}
