package org.openhab.ui.gwt.client.node.model;

import java.util.List;

public interface Node {
	public void setId(String id);

	public String getId();

	public void setTitle(String title);

	public String getTitle();

	public void setUiElements(List<UiElement> childeren);

	public List<UiElement> getUiElements();

}
