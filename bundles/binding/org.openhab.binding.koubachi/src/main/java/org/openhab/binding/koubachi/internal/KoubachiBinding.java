/**
 * openHAB, the open Home Automation Bus.
 * Copyright (C) 2010-2012, openHAB.org <admin@openhab.org>
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
package org.openhab.binding.koubachi.internal;

import java.util.Dictionary;

import org.apache.commons.lang.StringUtils;
import org.openhab.binding.koubachi.KoubachiBindingProvider;
import org.openhab.core.binding.AbstractActiveBinding;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.transform.TransformationException;
import org.openhab.core.transform.TransformationHelper;
import org.openhab.core.transform.TransformationService;
import org.openhab.core.types.State;
import org.openhab.io.net.http.HttpUtil;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
	

/**
 * @author Thomas.Eichstaedt-Engelen
 * @since 1.1.0
 */
public class KoubachiBinding extends AbstractActiveBinding<KoubachiBindingProvider> implements ManagedService {

	private static final Logger logger = 
		LoggerFactory.getLogger(KoubachiBinding.class);

	private boolean isProperlyConfigured = false;
	
	/** the refresh interval which is used to poll values from the Koubachi server (optional, defaults to 60000ms) */
	private long refreshInterval = 60000;
	
	/** the  (optional, defaults to 'https://api.koubachi.com/v2/') */
	private String apiBaseUrl = "https://api.koubachi.com/v2/";
	
	private String credentials;
	
	private String appKey;

	
	public KoubachiBinding() {
	}
	
	
	public void activate() {
	}
	
	public void deactivate() {
	}
	
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	protected long getRefreshInterval() {
		return refreshInterval;
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	protected String getName() {
		return "Koubachi Refresh Service";
	}
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	public boolean isProperlyConfigured() {
		return isProperlyConfigured;
	}
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	protected void execute() {
		for (KoubachiBindingProvider provider : providers) {
			for (String itemName : provider.getItemNames()) {
				
				String plantId = provider.getPlantId(itemName);
				String url = apiBaseUrl + "plants/" + plantId + "?user_credentials=" + credentials +"&app_key=" + appKey;
				
				String response = HttpUtil.executeUrl("GET", url, null, "application/xml", 5000);

				if(response==null) {
					logger.error("No response received from '{}'", url);
				} else {
					String transformationFunction = provider.getTransformExpression(itemName);
					String transformedResponse;
					
					try {
						TransformationService transformationService = 
							TransformationHelper.getTransformationService(KoubachiActivator.getContext(), "XPATH");
						if (transformationService != null) {
							transformedResponse = transformationService.transform(transformationFunction, response);
						} else {
							transformedResponse = response;
							logger.warn("couldn't transform response because transformationService of type 'XPATH' is unavailable");
						}
					}
					catch (TransformationException te) {
						logger.error("transformation throws exception [transformation=" + transformationFunction + ", response=" + response + "]", te);
						
						// in case of an error we return the response without any
						// transformation
						transformedResponse = response;
					}
					
					if (StringUtils.isNotBlank(transformedResponse)) {
						logger.debug("transformed response is '{}'", transformedResponse);
						
						State state = transformedResponse.equals("true") ? OnOffType.ON : OnOffType.OFF;
						
						if (state != null) {
							eventPublisher.postUpdate(itemName, state);
						}
					} else {
						logger.debug("response is empty cannot convert into a proper state");
					}
				}
			}
		}
	}
	
	
	/**
	 * @{inheritDoc}
	 */
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
