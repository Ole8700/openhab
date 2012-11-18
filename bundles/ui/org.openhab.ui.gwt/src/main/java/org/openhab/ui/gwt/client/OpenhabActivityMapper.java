package org.openhab.ui.gwt.client;

import org.openhab.ui.gwt.client.node.NodePresenter;
import org.openhab.ui.gwt.client.node.NodeServiceGwtImpl;
import org.openhab.ui.gwt.client.node.NodeViewGwtImpl;
import org.openhab.ui.gwt.client.ui.mgwt.UIFactoryMGwtImpl;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;

public class OpenhabActivityMapper implements ActivityMapper {

	private PlaceController placeController;

	public OpenhabActivityMapper(PlaceController placeController) {
		this.placeController = placeController;
	}

	@Override
	public Activity getActivity(Place place) {

		if (place instanceof GenericPlace) {
			GenericPlace genericPlace = (GenericPlace) place;
			return new NodePresenter(genericPlace.getId(),
					new NodeViewGwtImpl(), new NodeServiceGwtImpl(),
					new UIFactoryMGwtImpl(), placeController);
		}

		return null;
	}

}
