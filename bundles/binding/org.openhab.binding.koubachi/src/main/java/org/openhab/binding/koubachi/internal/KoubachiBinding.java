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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.openhab.binding.koubachi.KoubachiBindingProvider;
import org.openhab.binding.koubachi.internal.KoubachiAutoBindingProvider.KoubachiBindingConfig;
import org.openhab.binding.koubachi.internal.api.AbstractKoubachiData;
import org.openhab.binding.koubachi.internal.api.Device;
import org.openhab.binding.koubachi.internal.api.KoubachiConnector;
import org.openhab.binding.koubachi.internal.api.Plant;
import org.openhab.core.binding.AbstractActiveBinding;
import org.openhab.core.library.types.DateTimeType;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.types.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
	

/**
 * @author Thomas.Eichstaedt-Engelen
 * @since 1.2.0
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
				KoubachiBindingConfig config = provider.getConfig(itemName);
				
				if ("Device".equals(config.type)) {
					Device device = findDataElement(config.id, devices);
					postValue(itemName, config, device);
				} else if ("Plant".equals(config.type)){
					Plant plant = findDataElement(config.id, plants);
					postValue(itemName, config, plant);
				} else {
					throw new IllegalArgumentException("Item '" + itemName + "' cannot be processed");
				}
			}
		}
	}

	private void postValue(String itemName, KoubachiBindingConfig config, AbstractKoubachiData data) {
		try {
			Object value = PropertyUtils.getProperty(data, config.propertyName);
			State state = createState(value);
			if (state != null) {
				eventPublisher.postUpdate(itemName, state);
			}
		} catch (Exception e) {
			logger.error("getting property '{}' from item '{}' failed.", config.propertyName, itemName);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private <D extends AbstractKoubachiData> D findDataElement(String id, List<? extends AbstractKoubachiData> dataElements) {
		for (AbstractKoubachiData dataElement : dataElements) {
			if (dataElement.getId().equals(id)) {
				return (D) dataElement;
			}
		}
		return null;
	}
	
	private State createState(Object value) {
		if (value instanceof BigDecimal) {
			return new DecimalType((String) value);
		} else if (value instanceof Date) {
			Calendar calendar = Calendar.getInstance();
				calendar.setTime((Date) value);
			return new DateTimeType(calendar);
		} else {
			return new StringType((String) value);
		}
	}
	
	
}
