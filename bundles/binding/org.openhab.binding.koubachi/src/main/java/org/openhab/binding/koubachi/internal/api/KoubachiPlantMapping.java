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
public enum KoubachiPlantMapping implements KoubachiDataMapping {
	
	ID("id", "Number"),
	NAME("name", "String"),
	LOCATION("location", "String"),
	
	WATER_INSTRUCTION("vdm_water_instruction", "String"),
	WATER_LEVEL("vdm_water_level", "Number"),
	MIST_INSTRUCTION("vdm_mist_instruction", "String"),
	MIST_LEVEL("vdm_mist_level", "Number"),
	FERTILIZER_INSTRUCTION("vdm_fertilizer_instruction", "String"),
	FERTILIZER_LEVEL("vdm_fertilizer_level", "Number"),
	LIGHT_INSTRUCTION("vdm_light_instruction", "String"),
	LIGHT_LEVEL("vdm_light_level", "Number");
	
	private String dataKey;
	private String itemType;
	
	private KoubachiPlantMapping(String dataKey, String itemType) {
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
