package org.openhab.binding.koubachi.internal.api;

import java.util.HashMap;
import java.util.Map;


public class Plant extends HashMap<String, Object> {

	/** generated serial version uid */
	private static final long serialVersionUID = -8624002656675727289L;	
	
	public Plant(Map<String, Object> clone) {
		putAll(clone);
	}
	
	public Integer getId() {
		return (Integer) get(KoubachiPlantMapping.ID.getDataKey());
	}

	public String getLocation() {
		return (String) get(KoubachiPlantMapping.LOCATION.getDataKey());
	}
	
}
