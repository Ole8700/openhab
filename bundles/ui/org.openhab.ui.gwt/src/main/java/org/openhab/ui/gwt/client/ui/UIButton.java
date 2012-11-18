package org.openhab.ui.gwt.client.ui;

import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;

public interface UIButton extends UIBase{

	public void setText(String text);

	public HasTapHandlers getTapHandler();

	public void setSelected(boolean selected);

}
