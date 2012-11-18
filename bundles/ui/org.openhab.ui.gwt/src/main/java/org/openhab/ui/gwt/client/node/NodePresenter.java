package org.openhab.ui.gwt.client.node;

import java.util.List;
import java.util.Stack;

import org.openhab.ui.gwt.client.GenericPlace;
import org.openhab.ui.gwt.client.node.model.Node;
import org.openhab.ui.gwt.client.node.model.UiElement;
import org.openhab.ui.gwt.client.ui.UIBase;
import org.openhab.ui.gwt.client.ui.UIButton;
import org.openhab.ui.gwt.client.ui.UIFactory;
import org.openhab.ui.gwt.client.ui.UIGroup;
import org.openhab.ui.gwt.client.ui.UIList;
import org.openhab.ui.gwt.client.ui.UIParent;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;

public class NodePresenter extends AbstractActivity {

	private NodeView view;
	private NodeService service;
	private UIFactory uiFactory;
	private String id;
	private PlaceController controller;

	public NodePresenter(String id, NodeView view, NodeService service,
			UIFactory uiFactory, PlaceController controller) {
		this.id = id;
		this.view = view;
		this.service = service;
		this.uiFactory = uiFactory;
		this.controller = controller;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {

		service.loadPage(id, new AsyncCallback<Node>() {

			@Override
			public void onSuccess(Node page) {
				renderPage(page);

			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}
		});

		panel.setWidget(view);

	}

	protected void renderPage(Node page) {
		view.getTitle().setText(page.getTitle());

		List<UiElement> uiElements = page.getUiElements();

		render(view, uiElements);

		view.refresh();

	}

	protected void render(UIParent currentParent,
			List<UiElement> elementsToRender) {

		if (elementsToRender == null)
			return;

		for (final UiElement uiElement : elementsToRender) {
			String type = uiElement.getType();

			if ("button".equals(type)) {
				UIButton createButton = uiFactory.createButton();

				createButton.setText(uiElement.getLabel());

				createButton.getTapHandler().addTapHandler(new TapHandler() {

					@Override
					public void onTap(TapEvent event) {
						controller.goTo(new GenericPlace(uiElement
								.getActionId()));

					}
				});

				currentParent.add(createButton);
				continue;
			}
			if ("frame".equals(type)) {
				UIList list = uiFactory.createList();

				currentParent.add(list);

				render(list, uiElement.getChildren());
				continue;
			}
			
			if("group".equals(type)){
				UIGroup group = uiFactory.createGroup();
				
				group.setLabel(uiElement.getLabel());
				group.setIcon(uiElement.getIcon());
				group.setRightLabel(uiElement.getRightLabel());
				
				currentParent.add(group);
				continue;
			}
		}

	}

}
