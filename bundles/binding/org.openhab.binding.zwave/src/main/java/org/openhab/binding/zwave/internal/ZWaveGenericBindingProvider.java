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


import org.apache.commons.lang.StringUtils;
import org.openhab.binding.zwave.ZWaveBindingConfig;
import org.openhab.binding.zwave.ZWaveBindingProvider;
import org.openhab.binding.zwave.ZWaveCommandClass;
import org.openhab.core.items.Item;
import org.openhab.model.item.binding.AbstractGenericBindingProvider;
import org.openhab.model.item.binding.BindingConfigParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class is responsible for parsing the binding configuration.
 * 
 * @author Victor Belov
 * @since 1.2.0
 */
public class ZWaveGenericBindingProvider extends AbstractGenericBindingProvider implements ZWaveBindingProvider {

	private static final Logger logger = LoggerFactory.getLogger(ZWaveGenericBindingProvider.class);

	/**
	 * {@inheritDoc}
	 */
	public String getBindingType() {
		return "zwave";
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	public void validateItemType(Item item, String bindingConfig) throws BindingConfigParseException {
		// All types are valid
		logger.info("validateItemType({}, {})", item.getName(), bindingConfig);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processBindingConfiguration(String context, Item item, String bindingConfig) throws BindingConfigParseException {
		logger.debug("processBindingConfiguration({}, {})", item.getName(), bindingConfig);
		super.processBindingConfiguration(context, item, bindingConfig);
		String[] segments = bindingConfig.split(":");

		String nodeId = segments[0];
		if (StringUtils.isBlank(nodeId)) {
			throw new BindingConfigParseException("node id must not be blank");
		}

		ZWaveCommandClass commandClass = ZWaveCommandClass.SWITCH; // default setting
		if(segments.length > 1) {
			try {
				commandClass = ZWaveCommandClass.valueOf(segments[1].toUpperCase());
			} catch(Exception e) {
				throw new BindingConfigParseException(segments[1] + " is an unknown Z-Wave command class");
			}
		}
		
		ZWaveBindingConfig config = new ZWaveBindingConfig(nodeId, commandClass);
		addBindingConfig(item, config);
	}

	public ZWaveBindingConfig getZwaveData(String itemName) {
		return (ZWaveBindingConfig) this.bindingConfigs.get(itemName);
	}
}
