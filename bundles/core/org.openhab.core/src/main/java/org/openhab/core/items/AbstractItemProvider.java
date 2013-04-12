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
package org.openhab.core.items;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.openhab.core.items.GenericItem;
import org.openhab.core.items.ItemFactory;
import org.openhab.core.items.ItemProvider;
import org.openhab.core.items.ItemsChangeListener;


/**
 * @author Thomas.Eichstaedt-Engelen
 * @since 1.2.0
 */
public abstract class AbstractItemProvider implements ItemProvider {

	/** to keep track of all item change listeners */
	protected Collection<ItemsChangeListener> listeners = new HashSet<ItemsChangeListener>();
	
	/** to keep track of all item factories */
	protected Collection<ItemFactory> itemFactorys = new ArrayList<ItemFactory>();
		

	@Override
	public void addItemChangeListener(ItemsChangeListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeItemChangeListener(ItemsChangeListener listener) {
		listeners.remove(listener);
	}
	

	public void addItemFactory(ItemFactory factory) {
		itemFactorys.add(factory);
	}
	
	public void removeItemFactory(ItemFactory factory) {
		itemFactorys.remove(factory);
	}


	protected GenericItem getItemOfType(String itemType, String itemName) {
		
		System.err.println("itemType: " + itemType + ", itenName=" + itemName);
		if (itemType == null) {
			return null;
		}
		
		for (ItemFactory factory : itemFactorys) {
			GenericItem item = factory.createItem(itemType, itemName);
			System.err.println("create item: " + item);
			if (item != null) {
				return item;
			}
		}
		
		return null;
	}

}
