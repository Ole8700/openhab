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
package org.openhab.binding.zwave.internal;

import java.util.Dictionary;

import org.apache.commons.lang.StringUtils;
import org.openhab.binding.zwave.ZWaveBindingProvider;
import org.openhab.binding.zwave.ZWaveCommandClass;
import org.openhab.binding.zwave.internal.protocol.SerialInterface;
import org.openhab.binding.zwave.internal.protocol.ZWaveController;
import org.openhab.core.binding.AbstractActiveBinding;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.types.Command;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implement this class if you are going create an actively polling service
 * like querying a Website/Device.
 * 
 * @author Victor Belov
 * @since 1.2.0
 */
public class ZWaveActiveBinding extends AbstractActiveBinding<ZWaveBindingProvider> implements ManagedService {

	private static final Logger logger = LoggerFactory.getLogger(ZWaveActiveBinding.class);

	private boolean isProperlyConfigured = false;

	/** the refresh interval which is used to poll values from the ZWave server (optional, defaults to 60000ms) */
	private long refreshInterval = 60000;
	private String port;
	private SerialInterface serialInterface;
	private ZWaveController zController;
	
	public ZWaveActiveBinding() {
	}

	public void activate() {
		logger.debug("activate()");
	}
	
	public void deactivate() {
		logger.debug("deactivate()");
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
		return "ZWave Refresh Service";
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
		// the frequently executed code goes here ...
	}

	@Override
	protected void internalReceiveCommand(String itemName, Command command) {
		// if we are not yet initialized, don't waste time and return
		if(!isProperlyConfigured) return;
		
		logger.debug("internalReceiveCommand({}, {})", itemName, command.toString());
		for (ZWaveBindingProvider provider : providers) {
			logger.debug("BindingProvider = {}", provider.toString());
			logger.debug("Got nodeId = {}, commandClass = {}", provider.getZwaveData(itemName).getNodeId(),
					provider.getZwaveData(itemName).getCommandClass());
			int nodeId = Integer.valueOf(provider.getZwaveData(itemName).getNodeId());
			ZWaveCommandClass commandClass = provider.getZwaveData(itemName).getCommandClass();
			if (this.zController.isConnected()) {
				logger.debug("ZWaveController is connected");
				if (command == OnOffType.ON) {
					logger.debug("Sending ON");
					this.zController.sendLevel(nodeId, 255);
				} else if (command == OnOffType.OFF) {
					logger.debug("Sending OFF");
					this.zController.sendLevel(nodeId, 0);					
				} else {
					logger.warn("Unknown command >{}<", command.toString());
				}
			} else {
				logger.warn("ZWaveController is not connected");
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
			if (StringUtils.isNotBlank((String) config.get("port"))) {
				port = (String) config.get("port");
				logger.info("Update config, port = {}", port);
				this.serialInterface = new SerialInterface(port);
				this.zController = new ZWaveController(serialInterface);
				zController.initialize();
			}
			isProperlyConfigured = true;
		}
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

}
