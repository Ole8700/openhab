/**
 * openHAB, the open Home Automation Bus.
 * Copyright (C) 2010-2013, openHAB.org <admin@openhab.org>
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or
 * combining it with Eclipse (or a modified version of that library),
 * containing parts covered by the terms of the Eclipse Public License
 * (EPL), the licensors of this Program grant you additional permission
 * to convey the resulting work.
 */
package org.openhab.binding.koubachi.internal.api;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.openhab.io.net.http.HttpUtil;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KoubachiConnector implements ManagedService {

	private static final Logger logger = 
		LoggerFactory.getLogger(KoubachiConnector.class);
	
	/** the  (optional, defaults to 'https://api.koubachi.com/v2/') */
	private String apiBaseUrl = "https://api.koubachi.com/v2/";
	
	private String credentials;
	
 	private String appKey;
 	
	/** the refresh interval which is used to poll values from the Koubachi server (optional, defaults to 60000ms) */
	private long refreshInterval = 60000;
	
	private static List<Device> devices;
	private static List<Plant> plants;
	
	
	public static List<Device> getDevices() {
		if (devices == null) {
			devices = discoverDevices();
		}
		return devices;
	}
	
	
	protected static List<Device> discoverDevices() {
		List<Device> devices = new ArrayList<Device>();
		
		String credentials = "fMWNa7zR-LNtoidLe15j";
		String appKey = "KLABPLQP365CNQRIG0HY5HEX";
		String url = String.format("https://api.koubachi.com/v2/user/smart_devices?user_credentials=%1$s&app_key=%2$s", credentials, appKey);
		Properties headers = new Properties();
			headers.put("Accept", "application/json");
		
		String response = HttpUtil.executeUrl("GET", url, headers, null, null, 10000);

		if (response==null) {
			logger.error("No response received from '{}'", url);
		} else {
			logger.debug("Response from Koubachi is '{}'", response);
			
			List<Map<String, Device>> deviceList = fromJSON(new TypeReference<List<Map<String,Device>>>() {}, response);
			for (Map<String, Device> element : deviceList) {
				devices.add(element.get("device"));
			}
		}
		
		return devices;
	}
	
	
	public static List<Plant> getPlants() {
		if (plants == null) {
			plants = discoverPlants();
		}
		return plants;
	}

	protected static List<Plant> discoverPlants() {
		List<Plant> plants = new ArrayList<Plant>();
		
		String credentials = "fMWNa7zR-LNtoidLe15j";
		String appKey = "KLABPLQP365CNQRIG0HY5HEX";
		String url = String.format("https://api.koubachi.com/v2/plants.json?user_credentials=%1$s&app_key=%2$s", credentials, appKey);
		Properties headers = new Properties();
			headers.put("Accept", "application/json");
	
		String response = HttpUtil.executeUrl("GET", url, headers, null, null, 10000);

		if (response==null) {
			logger.error("No response received from '{}'", url);
		} else {
			List<Map<String, Plant>> plantList = fromJSON(new TypeReference<List<Map<String,Plant>>>() {}, response);
			for (Map<String, Plant> element : plantList) {
				plants.add(element.get("plant"));
			}
		}
		
		return plants;
	}

	public static <T> T fromJSON(final TypeReference<T> type, final String jsonPacket) {
		T data = null;

		try {
			data = new ObjectMapper().readValue(jsonPacket, type);
		} catch (Exception e) {
			e.printStackTrace();
			// Handle the problem
		}
		return data;
	}

	@Override
	public void updated(Dictionary<String, ?> config) throws ConfigurationException {
		if (config != null) {
			String refreshIntervalString = (String) config.get("refresh");
			if (StringUtils.isNotBlank(refreshIntervalString)) {
				refreshInterval = Long.parseLong(refreshIntervalString);
			}
			String apiBaseUrlString = (String) config.get("apiurl");
			if (StringUtils.isNotBlank(apiBaseUrlString)) {
				apiBaseUrl = apiBaseUrlString;
			}
			credentials = (String) config.get("credentials");
			if (StringUtils.isBlank(credentials)) {
				throw new ConfigurationException("koubachi:credentials", "Users' credentials parameter must be set");
			}
			appKey = (String) config.get("appkey");
			if (StringUtils.isBlank(appKey)) {
				throw new ConfigurationException("koubachi:appkey", "AppKey parameter must be set");
			}
		}
	}
	

}
