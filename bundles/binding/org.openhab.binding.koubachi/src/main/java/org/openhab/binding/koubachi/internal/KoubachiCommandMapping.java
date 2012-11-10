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

import org.openhab.core.items.Item;
import org.openhab.core.library.items.NumberItem;
import org.openhab.core.library.items.StringItem;


/**
 * @author Thomas.Eichstaedt-Engelen
 * @since 1.1.0
 */
public enum KoubachiCommandMapping {
	
	TEMPERATURE("/smart-device-device/recent-temperature-reading-value", NumberItem.class),
	BRIGHTNESS("/smart-device-device/recent-light-reading-value", NumberItem.class),
	MOISTURE("/smart-device-device/recent-soilmoisture-reading-value", NumberItem.class),
	WATERINSTRUCTIONS("/smart-device-device/plants/plant[id='119542']/vdm-water-instruction", StringItem.class);
	
	private String xpath;
	private Class<? extends Item> itemType;
	
	private KoubachiCommandMapping(String xpath, Class<? extends Item> itemType) {
		this.xpath = xpath;
		this.itemType = itemType;
	}
	
	public String getXpath() {
		return xpath;
	}
	
	public Class<? extends Item> getItemType() {
		return itemType;
	}

}
