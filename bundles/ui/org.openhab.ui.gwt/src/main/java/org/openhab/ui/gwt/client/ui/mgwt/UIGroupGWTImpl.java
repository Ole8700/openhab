package org.openhab.ui.gwt.client.ui.mgwt;

import org.openhab.ui.gwt.client.ui.UIGroup;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.googlecode.mgwt.ui.client.MGWTStyle;

public class UIGroupGWTImpl implements UIGroup {

	private FlowPanel main;
	private Image image;
	private Label mainLabel;
	private Label rightLabel;

	public UIGroupGWTImpl() {
		main = new FlowPanel();
		main.getElement().getStyle().setProperty("display", "-webkit-box");
		
		image = new Image();
		main.add(image);
		image.setVisible(false);
		//TODO css
		image.setSize("32px", "32px");
		
		mainLabel = new Label();
		mainLabel.getElement().getStyle().setProperty("WebkitBoxFlex", "1");
		main.add(mainLabel);
		
		rightLabel = new Label();
		rightLabel.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		main.add(rightLabel);
	}

	@Override
	public Object getWidget() {
		return main;
	}

	@Override
	public void setLabel(String label) {
		mainLabel.setText(label);

	}

	@Override
	public void setIcon(String url) {
		image.setUrl(url);
		image.setVisible(true);

	}

	@Override
	public void setRightLabel(String label) {
		rightLabel.setText(label);
	}

}
