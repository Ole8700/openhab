package org.openhab.ui.gwt.client.node;

import org.openhab.ui.gwt.client.node.model.Node;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface NodeService {
	
	public void loadPage(String id, AsyncCallback<Node> page);
	
}
