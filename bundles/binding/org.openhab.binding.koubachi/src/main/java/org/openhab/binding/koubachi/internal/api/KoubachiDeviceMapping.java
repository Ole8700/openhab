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
package org.openhab.binding.koubachi.internal.api;

import org.apache.commons.lang.StringUtils;


/**
 * @author Thomas.Eichstaedt-Engelen
 * @since 1.1.0
 */
public enum KoubachiDeviceMapping implements KoubachiDataMapping {
	
	ID("mac_address", "String"),
	BATTERY_LEVEL("virtual_battery_level", "Number"),
	
	//LAST_TRANSMISSION("last_transmission", "DateTime"),
	//NEXT_TRANSMISSION("next_transmission", "DateTime"),
	
	SOILMOISTURE_VALUE("recent_soilmoisture_reading_value", "Number"),
	TEMPERATURE_VALUE("recent_temperature_reading_value", "Number"),
	LIGHT_VALUE("recent_light_reading_value", "Number");
	
	private String dataKey;
	private String itemType;
	
	private KoubachiDeviceMapping(String dataKey, String itemType) {
		this.dataKey = dataKey;
		this.itemType = itemType;
	}
	
	@Override
	public String getDataKey() {
		return dataKey;
	}

	@Override
	public String getItemType() {
		return itemType;
	}
	
	public String getItemPostfix() {
		String[] parts = getDataKey().split("_");
		
		String itemPostfix = "_";
		for (String part : parts) {
			itemPostfix += StringUtils.capitalize(part);
		}
		
		return itemPostfix;
	}
	
}
