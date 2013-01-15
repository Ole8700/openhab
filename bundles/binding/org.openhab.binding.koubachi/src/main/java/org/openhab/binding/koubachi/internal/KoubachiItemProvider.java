package org.openhab.binding.koubachi.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openhab.binding.koubachi.internal.api.Device;
import org.openhab.binding.koubachi.internal.api.KoubachiConnector;
import org.openhab.binding.koubachi.internal.api.KoubachiDeviceMapping;
import org.openhab.binding.koubachi.internal.api.KoubachiPlantMapping;
import org.openhab.binding.koubachi.internal.api.Plant;
import org.openhab.core.items.GroupItem;
import org.openhab.core.items.Item;
import org.openhab.model.item.AbstractItemProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Thomas.Eichstaedt-Engelen
 * @since 1.1.0
 */
public class KoubachiItemProvider extends AbstractItemProvider {

	private static final Logger logger = 
		LoggerFactory.getLogger(KoubachiItemProvider.class);
	
	
	@Override
	public Collection<Item> getItems() {
		List<Item> items = new ArrayList<Item>();

		// add items for each plant sensor
		items.add(new GroupItem("Devices"));
		for (Device device : KoubachiConnector.getDevices()) {

			String baseItemName = "Device_" + device.getId();
			items.add(new GroupItem(baseItemName));
			for (KoubachiDeviceMapping deviceMapping : KoubachiDeviceMapping.values()) {
				String itemName = baseItemName + deviceMapping.getItemPostfix();
				logger.debug("created new device item '{}'", itemName);
				items.add(getItemOfType(deviceMapping.getItemType(), itemName));
			}
			
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
			for (KoubachiPlantMapping plantMapping : KoubachiPlantMapping.values()) {
				String itemName = baseItemName + plantMapping.getItemPostfix();
				logger.debug("created new plant item '{}'", itemName);
				items.add(getItemOfType(plantMapping.getItemType(), itemName));
			}

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
	

}
