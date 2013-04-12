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
package org.openhab.binding.koubachi.internal;

import org.apache.commons.beanutils.BeanMap;
import org.openhab.binding.koubachi.KoubachiBindingProvider;
import org.openhab.binding.koubachi.internal.api.Device;
import org.openhab.binding.koubachi.internal.api.KoubachiConnector;
import org.openhab.binding.koubachi.internal.api.KoubachiUtil;
import org.openhab.binding.koubachi.internal.api.Plant;
import org.openhab.core.binding.AbstractBindingProvider;
import org.openhab.core.binding.BindingConfig;


/**
 * <p>Here are some examples for valid binding configuration strings:
 * <ul>
 * 	<li><code>{ koubachi="&lt;resourceId&gt;:/plant/vdm-light-pending" }</code></li>
 * </ul>
 * 
 * @author Thomas.Eichstaedt-Engelen
 * @since 1.2.0
 */
public class KoubachiAutoBindingProvider extends AbstractBindingProvider implements KoubachiBindingProvider {	

	
	public KoubachiAutoBindingProvider() {
		for (Device device : KoubachiConnector.getDevices()) {
			BeanMap beanMap = new BeanMap(device);
			for (Object key : beanMap.keySet()) {
				if (!"class".equals(key)) {
					KoubachiBindingConfig config = new KoubachiBindingConfig();
						config.type = "Device";
						config.id = device.getId();
						config.propertyName = (String) key;
						
					String itemName = KoubachiUtil.buildItemName(device, (String) key);
					addBindingConfig(itemName, config);
				}
			}
		}
		for (Plant plant: KoubachiConnector.getPlants()) {
			BeanMap beanMap = new BeanMap(plant);
			for (Object key : beanMap.keySet()) {
				if (!"class".equals(key)) {
					KoubachiBindingConfig config = new KoubachiBindingConfig();
						config.type = "Plant";
						config.id = plant.getId();
						config.propertyName = (String) key;
						
					String itemName = KoubachiUtil.buildItemName(plant, (String) key);
					addBindingConfig(itemName, config);
				}
			}
		}
		
		System.err.println("bindingConfigs: " + bindingConfigs);
	}
	
	
	
	
	@Override
	public KoubachiBindingConfig getConfig(String itemName) {
		return (KoubachiBindingConfig) (bindingConfigs.get(itemName) != null ? bindingConfigs.get(itemName) : null);
	}
	

	public class KoubachiBindingConfig implements BindingConfig {
		String type;
		String id;
		String propertyName;
	}
	
}
