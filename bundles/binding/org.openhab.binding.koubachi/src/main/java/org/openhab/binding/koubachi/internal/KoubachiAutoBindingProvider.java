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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openhab.binding.koubachi.KoubachiBindingProvider;
import org.openhab.binding.koubachi.internal.api.Device;
import org.openhab.binding.koubachi.internal.api.KoubachiConnector;
import org.openhab.binding.koubachi.internal.api.KoubachiDataMapping;
import org.openhab.binding.koubachi.internal.api.KoubachiDeviceMapping;
import org.openhab.binding.koubachi.internal.api.KoubachiPlantMapping;
import org.openhab.binding.koubachi.internal.api.Plant;
import org.openhab.core.binding.BindingChangeListener;


/**
 * <p>Here are some examples for valid binding configuration strings:
 * <ul>
 * 	<li><code>{ koubachi="&lt;resourceId&gt;:/plant/vdm-light-pending" }</code></li>
 * </ul>
 * 
 * @author Thomas.Eichstaedt-Engelen
 * @since 1.1.0
 */
public class KoubachiAutoBindingProvider implements KoubachiBindingProvider {
	
	Map<String, KoubachiDataMapping> bindingConfig = new HashMap<String, KoubachiDataMapping>();
	

	public KoubachiAutoBindingProvider() {
		List<Device> devices = KoubachiConnector.getDevices();
		for (Device device : devices) {
			String baseItemName = "Device_" + device.getId();
			for (KoubachiDeviceMapping mapping : KoubachiDeviceMapping.values()) {
				bindingConfig.put(baseItemName + mapping.getItemPostfix(), mapping);
			}
		}
		
		List<Plant> plants = KoubachiConnector.getPlants();
		for (Plant plant : plants) {
			String baseItemName = "Plant_" + plant.getId();
			for (KoubachiPlantMapping mapping : KoubachiPlantMapping.values()) {
				bindingConfig.put(baseItemName + mapping.getItemPostfix(), mapping);
			}
		}
	}
	
	@Override
	public void addBindingChangeListener(BindingChangeListener listener) {
		// TODO Auto-generated method stub
	}

	@Override
	public void removeBindingChangeListener(BindingChangeListener listener) {
		// TODO Auto-generated method stub
	}


	@Override
	public boolean providesBindingFor(String itemName) {
		return bindingConfig.containsKey(itemName);
	}

	@Override
	public boolean providesBinding() {
		return bindingConfig.size() > 0;
	}

	@Override
	public Collection<String> getItemNames() {
		return bindingConfig.keySet();
	}

	
	@Override
	public KoubachiDeviceMapping getDeviceMappingBy(String itemName) {
		return (KoubachiDeviceMapping) (bindingConfig.containsKey(itemName) ? bindingConfig.get(itemName) : null);
	}

	@Override
	public KoubachiPlantMapping getPlantMappingBy(String itemName) {
		return (KoubachiPlantMapping) (bindingConfig.containsKey(itemName) ? bindingConfig.get(itemName) : null);
	}
	
	
}
