package org.openhab.ui.gwt.client.node.model;

import java.util.List;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

public interface UiElement {

	public void setId(String id);

	public String getId();

	public void setLabel(String label);

	public String getLabel();

	public String getType();

	public void setType(String type);

	public void setChildren(List<UiElement> children);

	public List<UiElement> getChildren();

	public String getActionId();

	public void setActionId(String actionId);
	
	public String getIcon();
	
	public void setIcon(String icon);

	public String getRightLabel();
	
	public void setRightLabel(String rightlabel);
}
