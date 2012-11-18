package org.openhab.ui.gwt.client.node;

import org.openhab.ui.gwt.client.node.model.Node;
import org.openhab.ui.gwt.client.node.model.NodeFactory;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

public class NodeServiceGwtImpl implements NodeService {

	private static final NodeFactory FACTORY = GWT.create(NodeFactory.class);

	public NodeServiceGwtImpl() {

	}

	@Override
	public void loadPage(String id, final AsyncCallback<Node> callback) {

		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, id + ".json");

		builder.setCallback(new RequestCallback() {

			@Override
			public void onResponseReceived(Request request, Response response) {

				if (response.getStatusCode() == 200) {
					String json = response.getText();
					Node node = parseJSON(json);
					callback.onSuccess(node);
					return;
				}

				Window.alert("http code: " + response.getStatusCode());

				callback.onFailure(null);

			}

			@Override
			public void onError(Request request, Throwable exception) {
				Window.alert("something went very wrong in service");
				callback.onFailure(null);
			}
		});

		try {
			builder.send();
		} catch (RequestException e) {
			Window.alert("error sending request");
			callback.onFailure(null);
		}

	}

	protected Node parseJSON(String json) {
		AutoBean<Node> ab = AutoBeanCodex.decode(FACTORY, Node.class, json);
		return ab.as();
	}
}
