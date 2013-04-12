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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanMap;
import org.openhab.binding.koubachi.internal.api.AbstractKoubachiData;
import org.openhab.binding.koubachi.internal.api.Device;
import org.openhab.binding.koubachi.internal.api.KoubachiConnector;
import org.openhab.binding.koubachi.internal.api.KoubachiUtil;
import org.openhab.binding.koubachi.internal.api.Plant;
import org.openhab.core.items.AbstractItemProvider;
import org.openhab.core.items.GenericItem;
import org.openhab.core.items.GroupItem;
import org.openhab.core.items.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Thomas.Eichstaedt-Engelen
 * @since 1.2.0
 */
public class KoubachiItemProvider extends AbstractItemProvider {

	private static final Logger logger = 
		LoggerFactory.getLogger(KoubachiItemProvider.class);
	
	
	public KoubachiItemProvider() {
	}
	
	@Override
	public Collection<Item> getItems() {
		ArrayList<Item> items = new ArrayList<Item>();
		
		// add items for each plant sensor
		items.add(new GroupItem("Devices"));
		for (Device device : KoubachiConnector.getDevices()) {

			String baseItemName = "Device_" + device.getId();
			items.add(new GroupItem(baseItemName));
			
			createAndAddItem(items, device);
			
			for (Item item : items) {
				if (item != null && !(item instanceof GroupItem)) {
					item.getGroupNames().add("Devices");
					item.getGroupNames().add(baseItemName);
				}
			}
		}

		// add items for each plant
		items.add(new GroupItem("Plants"));
		for (Plant plant : KoubachiConnector.getPlants()) {

			String baseItemName = "Plant_" + plant.getId();
			items.add(new GroupItem(baseItemName));
			
			createAndAddItem(items, plant);
			
			for (Item item : items) {
				if (item != null && !(item instanceof GroupItem)) {
					item.getGroupNames().add("Plants");
					item.getGroupNames().add(baseItemName);
					item.getGroupNames().add(plant.getLocation());
				}
			}
		}

		System.err.println(items);
		
		return items;
	}
	
	private void createAndAddItem(List<Item> items, AbstractKoubachiData device) {
		BeanMap beanMap = new BeanMap(device);
		for (Object methodName : beanMap.keySet()) {
			if (methodName.equals("class")) {
				continue;
			}
			
			String itemName = KoubachiUtil.buildItemName(device, (String) methodName);
			
			String itemType = translateItemType(beanMap.getType((String) methodName));
			GenericItem item = getItemOfType(itemType, itemName);
			if (item != null) {
				logger.debug("created new device item '{}'", itemName);
				items.add(item);
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private String translateItemType(Class type) {
		if (type.isAssignableFrom(String.class)) {
			return "String";
		} else if (type.isAssignableFrom(BigDecimal.class)) {
			return "Number";
		} else if (type.isAssignableFrom(Date.class)) {
			return "DateTime";
		} else {
			throw new IllegalArgumentException("Cannot handle type '" + type + "'");
		}
	}
	

}
