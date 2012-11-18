package org.openhab.ui.gwt.client;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class GenericPlace extends Place {
	private static final String DEFAULT = "1";
	
	
	public static final GenericPlace DEFAULT_PLACE = new GenericPlace(DEFAULT);

	public static class Tokenizer implements PlaceTokenizer<GenericPlace> {

		@Override
		public GenericPlace getPlace(String token) {
			if (token == null || "".equals(token)) {
				return DEFAULT_PLACE;
			}

			return new GenericPlace(token);
		}

		@Override
		public String getToken(GenericPlace place) {
			return place.getId();
		}

	}

	private String id;

	public GenericPlace(String id) {
		this.id = id;

	}

	public String getId() {
		return id;
	}

}
