package org.openhab.ui.gwt.client;

import java.util.Stack;

import com.google.gwt.place.shared.Place;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.googlecode.mgwt.collection.shared.LightArray;
import com.googlecode.mgwt.collection.shared.LightMap;
import com.googlecode.mgwt.mvp.client.Animation;
import com.googlecode.mgwt.mvp.client.AnimationMapper;
import com.googlecode.mgwt.mvp.client.history.HistoryHandler;
import com.googlecode.mgwt.mvp.client.history.HistoryObserver;

public class OpenhabHistoryObserver implements HistoryObserver, AnimationMapper {

	private LightMap<LightArray<String>> children;
	private LightMap<String> parents;

	public OpenhabHistoryObserver() {
		children = StructureJSO.getChildren();
		parents = StructureJSO.getParents();
	}

	@Override
	public void onPlaceChange(Place place, HistoryHandler handler) {

	}

	@Override
	public void onHistoryChanged(Place place, HistoryHandler handler) {

	}

	@Override
	public void onAppStarted(Place place, HistoryHandler historyHandler) {
		if (place instanceof GenericPlace) {
			GenericPlace genericPlace = (GenericPlace) place;
			String id = genericPlace.getId();

			System.out.println("id: " + id);
			
			Stack<String> history = getStack(id);

			if (history.size() > 0) {
				String pop = history.pop();
				historyHandler.replaceCurrentPlace(new GenericPlace(pop));
			}

			while (!history.isEmpty()) {
				historyHandler.pushPlace(new GenericPlace(history.pop()));
			}
			
			

		}

	}

	private Stack<String> getStack(String id) {
		Stack<String> history = new Stack<String>();
		String parent = id;

		while (true) {
			String string = parents.get(parent);
			if (string == null) {
				break;
			}
			history.push(string);
			parent = string;
		}

		return history;
	}

	@Override
	public HandlerRegistration bind(EventBus eventBus,
			HistoryHandler historyHandler) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Animation getAnimation(Place oldPlace, Place newPlace) {
		if (oldPlace == null) {
			return Animation.FADE;
		}

		if (isParent(oldPlace, newPlace)) {
			return Animation.SLIDE;
		} else {
			return Animation.SLIDE_REVERSE;
		}

	}

	private boolean isParent(Place oldPlace, Place newPlace) {
		if (oldPlace instanceof GenericPlace
				&& newPlace instanceof GenericPlace) {

			GenericPlace nP = (GenericPlace) newPlace;
			GenericPlace oP = (GenericPlace) oldPlace;

			String parentId = parents.get(nP.getId());
			if (oP.getId().equals(parentId)) {
				return true;
			}

		}

		return false;

	}

}
