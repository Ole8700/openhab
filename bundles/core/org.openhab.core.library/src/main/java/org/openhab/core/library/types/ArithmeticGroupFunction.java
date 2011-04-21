/**
 * openHAB, the open Home Automation Bus.
 * Copyright (C) 2011, openHAB.org <admin@openhab.org>
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

package org.openhab.core.library.types;

import java.math.BigDecimal;
import java.util.List;

import org.openhab.core.items.GroupFunction;
import org.openhab.core.items.Item;
import org.openhab.core.types.State;
import org.openhab.core.types.UnDefType;

/**
 * This interface is only a container for functions that require the core type library
 * for its calculations.
 * 
 * @author Kai Kreuzer
 * @since 0.7.0
 *
 */
public interface ArithmeticGroupFunction extends GroupFunction {

	/**
	 * This does a logical 'and' operation. Only if all items are of 'activeState' this
	 * is returned, otherwise the 'passiveState' is returned.
	 * 
	 * Through the getStateAs() method, it can be determined, how many
	 * items actually are not in the 'activeState'.
	 * 
	 * @author Kai Kreuzer
	 * @since 0.7.0
	 *
	 */
	static class And implements GroupFunction {
		
		private final State activeState;
		private final State passiveState;
		
		public And(State activeValue, State passiveValue) {
			this.activeState = activeValue;
			this.passiveState = passiveValue;
		}
		
		/**
		 * @{inheritDoc
		 */
		@Override
		public State calculate(List<Item> items) {
			for(Item item : items) {
				if(!activeState.equals(item.getState())) {
					return passiveState;
				}
			}
			return activeState;
		}

		/**
		 * @{inheritDoc
		 */
		@Override
		public State getStateAs(List<Item> items,
				Class<? extends State> stateClass) {
			State state = calculate(items);
			if(stateClass.isInstance(state)) {
				return state;
			} else {
				if(stateClass == DecimalType.class) {
					return new DecimalType(items.size() - count(items, activeState));
				} else {
					return null;
				}
			}
		}
		
		private int count(List<Item> items, State state) {
			int count = 0;
			for(Item item : items) {
				if(state.equals(item.getState())) {
					count++;
				}
			}
			return count;
			
		}
	}

	/**
	 * This does a logical 'or' operation. If at least one item is of 'activeState' this
	 * is returned, otherwise the 'passiveState' is returned.
	 * 
	 * Through the getStateAs() method, it can be determined, how many
	 * items actually are in the 'activeState'.
	 * 
	 * @author Kai Kreuzer
	 * @since 0.7.0
	 *
	 */
	static class Or implements GroupFunction {

		private final State activeState;
		private final State passiveState;
		
		public Or(State activeValue, State passiveValue) {
			this.activeState = activeValue;
			this.passiveState = passiveValue;
		}

		/**
		 * @{inheritDoc
		 */
		@Override
		public State calculate(List<Item> items) {			
			for(Item item : items) {
				if(activeState.equals(item.getState())) {
					return activeState;
				}
			}
			return passiveState;
		}
		
		/**
		 * @{inheritDoc
		 */
		@Override
		public State getStateAs(List<Item> items,
				Class<? extends State> stateClass) {
			State state = calculate(items);
			if(stateClass.isInstance(state)) {
				return state;
			} else {
				if(stateClass == DecimalType.class) {
					return new DecimalType(count(items, activeState));
				} else {
					return null;
				}
			}
		}
		
		private int count(List<Item> items, State state) {
			int count = 0;
			for(Item item : items) {
				if(state.equals(item.getState())) {
					count++;
				}
			}
			return count;
			
		}
	}
		
	/**
	 * This calculates the numeric average over all item states of decimal type.
	 * 
	 * @author Kai Kreuzer
	 * @since 0.7.0
	 *
	 */
	static class Avg implements GroupFunction {
		
		public Avg() {}

		/**
		 * @{inheritDoc
		 */
		@Override
		public State calculate(List<Item> items) {
			BigDecimal sum = BigDecimal.ZERO;
			int count = 0;
			for(Item item : items) {
				if(item.getState() instanceof DecimalType) {
					DecimalType itemState = (DecimalType) item.getState();
					sum = sum.add(itemState.toBigDecimal());
					count++;
				}
			}
			if(count>0) {
				return new DecimalType(sum.divide(new BigDecimal(count)));
			} else {
				return UnDefType.UNDEF;
			}
		}
		
		/**
		 * @{inheritDoc
		 */
		@Override
		public State getStateAs(List<Item> items,
				Class<? extends State> stateClass) {
			State state = calculate(items);
			if(stateClass.isInstance(state)) {
				return state;
			} else {
				return null;
			}
		}
	}

	/**
	 * This calculates the minimum value of all item states of decimal type.
	 * 
	 * @author Kai Kreuzer
	 * @since 0.7.0
	 *
	 */
	static class Min implements GroupFunction {
		
		public Min() {}

		/**
		 * @{inheritDoc
		 */
		@Override
		public State calculate(List<Item> items) {
			if(items.size()>0) {
				BigDecimal min = null;
				for(Item item : items) {
					if(item.getState() instanceof DecimalType) {
						DecimalType itemState = (DecimalType) item.getState();
						if(min==null || min.compareTo(itemState.toBigDecimal()) > 0) {
							min = itemState.toBigDecimal();
						}
					}
				}
				if(min!=null) {
					return new DecimalType(min);
				}
			}
			return UnDefType.UNDEF;
		}

		/**
		 * @{inheritDoc
		 */
		@Override
		public State getStateAs(List<Item> items,
				Class<? extends State> stateClass) {
			State state = calculate(items);
			if(stateClass.isInstance(state)) {
				return state;
			} else {
				return null;
			}
		}
}

	/**
	 * This calculates the maximum value of all item states of decimal type.
	 * 
	 * @author Kai Kreuzer
	 * @since 0.7.0
	 *
	 */
	static class Max implements GroupFunction {
		
		public Max() {}

		/**
		 * @{inheritDoc
		 */
		@Override
		public State calculate(List<Item> items) {
			if(items.size()>0) {
				BigDecimal max = null;
				for(Item item : items) {
					if(item.getState() instanceof DecimalType) {
						DecimalType itemState = (DecimalType) item.getState();
						if(max==null || max.compareTo(itemState.toBigDecimal()) < 0) {
							max = itemState.toBigDecimal();
						}
					}
				}
				if(max!=null) {
					return new DecimalType(max);
				}
			}
			return UnDefType.UNDEF;
		}

		/**
		 * @{inheritDoc
		 */
		@Override
		public State getStateAs(List<Item> items,
				Class<? extends State> stateClass) {
			State state = calculate(items);
			if(stateClass.isInstance(state)) {
				return state;
			} else {
				return null;
			}
		}
}
}
