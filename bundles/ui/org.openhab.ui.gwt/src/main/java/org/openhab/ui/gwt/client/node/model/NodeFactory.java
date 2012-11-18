package org.openhab.ui.gwt.client.node.model;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

public interface NodeFactory extends AutoBeanFactory {

	public AutoBean<Node> createNode();

	public AutoBean<UiElement> createUiElement();

}
