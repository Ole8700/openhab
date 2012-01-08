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
package org.openhab.core.events;

import java.util.Collection;
import java.util.HashSet;

import org.openhab.core.binding.BindingChangeListener;
import org.openhab.core.binding.BindingProvider;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;


/**
 * Enhances {@link AbstractEventSubscriber} with general binding capabilities.
 * Bindings which are going to react to events which were sent to the internal
 * eventbus should enhance this class. 
 * 
 * @author Thomas.Eichstaedt-Engelen
 * @since 0.8.0
 */
public abstract class AbstractEventSubscriberBinding<P extends BindingProvider> extends AbstractEventSubscriber implements BindingChangeListener {

	/** to keep track of all binding providers */
	protected Collection<P> providers = new HashSet<P>();
	

	public void addBindingProvider(P provider) {
		this.providers.add(provider);
		provider.addBindingChangeListener(this);
		allBindingsChanged(provider);
	}

	public void removeBindingProvider(P provider) {
		this.providers.remove(provider);
		provider.removeBindingChangeListener(this);
	}
	

	/**
	 * @{inheritDoc}
	 */
	@Override
	public void receiveCommand(String itemName, Command command) {

		// does any provider contain a binding config?
		if (!providesBindingFor(itemName)) {
			return;
		}

		internalReceiveCommand(itemName, command);
	}
	
	/**
	 * Is called by <code>receiveCommand()</code> only if one of the 
	 * {@link BindingProvider}s provide a binding for <code>itemName</code>.
	 * 
	 * @param itemName the item on which <code>command</code> will be executed
	 * @param command the {@link Command} to be executed on <code>itemName</code>
	 */
	protected void internalReceiveCommand(String itemName, Command command) {};
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	public void receiveUpdate(String itemName, State newState) {
		// does any provider contain a binding config?
		if (!providesBindingFor(itemName)) {
			return;
		}

		internalReceiveUpdate(itemName, newState);
	}
	
	/**
	 * Is called by <code>receiveUpdate()</code> only if one of the 
	 * {@link BindingProvider}s provide a binding for <code>itemName</code>.
	 * 
	 * @param itemName the item on which <code>command</code> will be executed
	 * @param newState the {@link State} to be update
	 */
	protected void internalReceiveUpdate(String itemName, State newState) {};

	/**
	 * checks if any of the bindingProviders contains an adequate mapping
	 * 
	 * @param itemName the itemName to check
	 * @return <code>true</code> if any of the bindingProviders contains an
	 *         adequate mapping for <code>itemName</code> and <code>false</code>
	 *         otherwise
	 */
	protected boolean providesBindingFor(String itemName) {

		for (P provider : providers) {
			if (provider.providesBindingFor(itemName)) {
				return true;
			}
		}

		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void allBindingsChanged(BindingProvider provider) {
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void bindingChanged(BindingProvider provider, String itemName) {
	}
	

}