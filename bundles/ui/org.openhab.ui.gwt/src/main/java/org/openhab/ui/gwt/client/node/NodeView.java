package org.openhab.ui.gwt.client.node;

import org.openhab.ui.gwt.client.ui.UIParent;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;

public interface NodeView extends IsWidget, UIParent {
	public HasText getTitle();

	public void refresh();
}
