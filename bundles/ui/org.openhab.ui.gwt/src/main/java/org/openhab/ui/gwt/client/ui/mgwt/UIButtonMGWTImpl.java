package org.openhab.ui.gwt.client.ui.mgwt;

import org.openhab.ui.gwt.client.ui.UIButton;

import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.ui.client.widget.Button;

public class UIButtonMGWTImpl implements UIButton {

	private Button button;

	public UIButtonMGWTImpl() {
		button = new Button();
	}

	@Override
	public void setText(String text) {
		button.setText(text);

	}

	@Override
	public HasTapHandlers getTapHandler() {
		return button;
	}

	@Override
	public void setSelected(boolean selected) {
		// TODO set some selected color

	}

	@Override
	public Object getWidget() {
		return button;
	}

}
