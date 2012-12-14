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

import java.util.List;

import org.openhab.binding.koubachi.KoubachiBindingProvider;
import org.openhab.binding.koubachi.internal.api.Device;
import org.openhab.binding.koubachi.internal.api.KoubachiConnector;
import org.openhab.binding.koubachi.internal.api.KoubachiDeviceMapping;
import org.openhab.binding.koubachi.internal.api.KoubachiPlantMapping;
import org.openhab.binding.koubachi.internal.api.Plant;
import org.openhab.core.binding.AbstractActiveBinding;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.types.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
	

/**
 * @author Thomas.Eichstaedt-Engelen
 * @since 1.1.0
 */
public class KoubachiBinding extends AbstractActiveBinding<KoubachiBindingProvider> {

	private static final Logger logger = 
		LoggerFactory.getLogger(KoubachiBinding.class);	
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	protected long getRefreshInterval() {
		return 30000;
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
		return true;
	}
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	protected void execute() {
		
		List<Device> devices = KoubachiConnector.getDevices();
		List<Plant> plants = KoubachiConnector.getPlants();
		
		for (KoubachiBindingProvider provider : providers) {
			for (String itemName : provider.getItemNames()) {
				
				if (itemName.startsWith("Device")) {
					KoubachiDeviceMapping deviceMapping = provider.getDeviceMappingBy(itemName);
					Device device = findDevice(itemName, devices);
					
					Object value = device.get(deviceMapping.getDataKey());
					if (value != null) {
						State state = createState(deviceMapping.getItemType(), value);
						if (state != null) {
							eventPublisher.postUpdate(itemName, state);
						}
					}
				} else if (itemName.startsWith("Plant")){
					KoubachiPlantMapping plantMapping = provider.getPlantMappingBy(itemName);
					Plant plant = findPlant(itemName, plants);
					
					Object value = plant.get(plantMapping.getDataKey());
					if (value != null) {
						State state = createState(plantMapping.getItemType(), value);
						if (state != null) {
							eventPublisher.postUpdate(itemName, state);
						}
					}
				} else {
					throw new IllegalArgumentException("Item '" + itemName + "' cannot be processed");
				}
			}
		}
	}
	
	private Device findDevice(String itemName, List<Device> devices) {
		String[] itemNameElements = itemName.split("_");
		String id = itemNameElements[1];
		for (Device device : devices) {
			if (device.getId().equals(id)) {
				return device;
			}
		}
		return null;
	}
	
	private Plant findPlant(String itemName, List<Plant> plants) {
		String[] itemNameElements = itemName.split("_");
		Integer id = Integer.valueOf(itemNameElements[1]);
		for (Plant plant : plants) {
			if (plant.getId().equals(id)) {
				return plant;
			}
		}
		return null;
	}
	
	private State createState(String itemType, Object value) {
		if ("String".equals(itemType)) {
			return new StringType((String) value);
		} else if ("Number".equals(itemType)) {
			if (value instanceof Number) {
				return new DecimalType(value.toString());
			} else if (value instanceof String) {
				String stringValue = ((String) value).replaceAll("[^\\d|.]", "");
				return new DecimalType(stringValue);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	
}
