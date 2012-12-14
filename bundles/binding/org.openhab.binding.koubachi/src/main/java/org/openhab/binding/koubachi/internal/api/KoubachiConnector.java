package org.openhab.binding.koubachi.internal.api;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openhab.io.net.http.HttpUtil;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import flexjson.JSONDeserializer;

public class KoubachiConnector implements ManagedService {

	private static final Logger logger = 
		LoggerFactory.getLogger(KoubachiConnector.class);
	
	// TODO ETag verarbeiten
	private String eTag = "";
	
	/** the  (optional, defaults to 'https://api.koubachi.com/v2/') */
	private String apiBaseUrl = "https://api.koubachi.com/v2/";
	
	private String credentials;
	
	private String appKey;
	
	private boolean isProperlyConfigured = false;
	
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
	
	
	@SuppressWarnings("unchecked")
	protected static List<Device> discoverDevices() {
		List<Device> devices = new ArrayList<Device>();
		
		String credentials = "fMWNa7zR-LNtoidLe15j";
		String appKey = "KLABPLQP365CNQRIG0HY5HEX";
		String url = String.format("https://api.koubachi.com/v2/user/smart_devices?format=json&user_credentials=%1$s&app_key=%2$s", credentials, appKey);
		String response = HttpUtil.executeUrl("GET", url, null, "application/json", 10000);

		if (response==null) {
			logger.error("No response received from '{}'", url);
		} else {
			List<Map<String, Object>> devicesList = new JSONDeserializer<List<Map<String, Object>>>().deserialize(response);
			for (Map<String, Object> element : devicesList) {
				devices.add(new Device((Map<String, Object>) element.get("device")));
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

	@SuppressWarnings("unchecked")
	protected static List<Plant> discoverPlants() {
		List<Plant> plants = new ArrayList<Plant>();
		
		String credentials = "fMWNa7zR-LNtoidLe15j";
		String appKey = "KLABPLQP365CNQRIG0HY5HEX";
		String url = String.format("https://api.koubachi.com/v2/plants?format=json&user_credentials=%1$s&app_key=%2$s", credentials, appKey);
		String response = HttpUtil.executeUrl("GET", url, null, "application/json", 10000);

		if (response==null) {
			logger.error("No response received from '{}'", url);
		} else {
			List<Map<String, Object>> plantsList = new JSONDeserializer<List<Map<String, Object>>>().deserialize(response);
			for (Map<String, Object> element : plantsList) {
				plants.add(new Plant((Map<String, Object>) element.get("plant")));
			}
		}
		
		return plants;
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
			
			isProperlyConfigured = true;
		}
	}
	

}
