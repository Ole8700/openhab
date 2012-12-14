package org.openhab.binding.koubachi.internal.api;

import java.util.HashMap;
import java.util.Map;


public class Device extends HashMap<String, Object> {

	/** generated serial version uid */
	private static final long serialVersionUID = 5347082191917344838L;
	
	public Device(Map<String, Object> clone) {
		putAll(clone);
	}
	
	public String getId() {
		return (String) get(KoubachiDeviceMapping.ID.getDataKey());		
	}
	
}
