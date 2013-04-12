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
package org.openhab.binding.koubachi.internal.api;

import java.math.BigDecimal;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Device extends AbstractKoubachiData {
	
	BigDecimal virtualBatteryLevel;
	
	Date lastTransmission;
	Date nextTransmission;
	
	String recentSoilmoistureReadingValue;
	String recentTemperatureReadingValue;
	String recentLightReadingValue;
	
	
	@JsonProperty("mac_address")
	public void setId(String id) {
		this.id = id;
	}
	
		
	@JsonProperty("virtual_battery_level")
	public BigDecimal getVirtualBatteryLevel() {
		return virtualBatteryLevel;
	}
	
	@JsonProperty("last_transmission")
	public Date getLastTransmission() {
		return lastTransmission;
	}
	
	@JsonProperty("next_transmission")
	public Date getNextTransmission() {
		return nextTransmission;
	}
	
	@JsonProperty("recent_soilmoisture_reading_value")
	public String getRecentSoilmoistureReadingValue() {
		return recentSoilmoistureReadingValue;
	}
	
	@JsonProperty("recent_temperature_reading_value")
	public String getRecentTemperatureReadingValue() {
		return recentTemperatureReadingValue;
	}
	
	@JsonProperty("recent_light_readingValue")
	public String getRecentLightReadingValue() {
		return recentLightReadingValue;
	}
	
}
