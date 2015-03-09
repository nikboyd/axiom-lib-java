/**
 * Copyright 2013,2015 Nikolas Boyd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axiom_tools.storage;

import javax.xml.bind.annotation.XmlTransient;

/**
 * Defines protocol for a persistent item.
 * 
 * <h4>SurrogatedItem Responsibilities:</h4>
 * <ul>
 * <li>knows a surrogate key</li>
 * </ul>
 */
public interface SurrogatedItem {

	/**
	 * A surrogate key which uniquely identifies this item.
	 * @return a surrogate key value
	 */
	public long getKey();
	
	/**
	 * Indicates whether this item was saved in persistent storage.
	 */
    @XmlTransient
	public boolean wasSaved();

	/**
	 * Logs a description of this item.
	 */
	public void describe();
	
	/**
	 * Returns this item properly typed.
	 * @return this item properly typed
	 */
	public <ItemType> ItemType asItem();
	
	/**
	 * Defines protocol for usage of a SurrogatedItem.
	 */
	public static interface Usage {

		/**
		 * Uses a saved item that's been retrieved from storage.
		 * @param item a previously saved item
		 */
		public <ItemType extends SurrogatedItem> void use(ItemType item);
	}

} // SurrogatedItem
