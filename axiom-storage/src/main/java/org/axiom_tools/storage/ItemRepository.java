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

import java.util.*;

/**
 * An item repository, which provides a transaction context for item persistence operations.
 * 
 * <h4>ItemRepository Responsibilities:</h4>
 * <ul>
 * <li>provides a transaction context for item persistence</li>
 * <li>counts items that have been saved</li>
 * <li>saves items in a configured backing store</li>
 * <li>removes items from a backing store</li>
 * </ul>
 *
 * <h4>Client Responsibilities:</h4>
 * <ul>
 * <li>configure an appropriate backing store</li>
 * </ul>
 */
public class ItemRepository {
	
	/**
	 * Returns a new transactional context.
	 * @return a new TransactionalContext
	 */
	public TransactionalContext createContext()  {
		return new TransactionalContext();
	}
	
	/**
	 * Counts the saved items of a specific kind.
	 * @param clazz an item type
	 * @return an item count, or zero
	 */
	public <ItemType extends SurrogatedItem> 
	int count(Class<ItemType> clazz) {
		return createContext().count(clazz);
	}
	
	
	/**
	 * Saves an item (by persisting it).
	 * @param item an item
	 * @return an item (after persisting it)
	 */
	public <ItemType extends SurrogatedItem> 
	ItemType save(ItemType item) {
		return createContext().saveItem(item);
	}
	
	/**
	 * Saves a composite item (by persisting it and its components).
	 * @param item a composite item
	 * @return a composite item (after persisting it)
	 */
	public <ItemType extends SurrogatedComposite> 
	ItemType save(ItemType item) {
		return createContext().saveItem(item);
	}

	/**
	 * Counts the elements in a component map.
	 * @param item a composite item
	 * @param mapIndex a map index
	 * @return a count of the elements in the indicated map
	 */
	public <ItemType extends SurrogatedComposite> 
	int countElements(ItemType item, int mapIndex) {
		return createContext().countMapComponents(item, mapIndex);
	}
	
	/**
	 * Returns a component element from a map.
	 * @param item a composite item
	 * @param mapIndex a map index
	 * @param key a component key
	 * @return a component item
	 */
	public <ItemType extends SurrogatedComposite, ResultType extends SurrogatedItem> 
	ResultType getMapElement(ItemType item, int mapIndex, Object key) {
		return createContext().getMapComponent(item, mapIndex, key);
	}

	/**
	 * Removes an item (makes it transient).
	 * @param item an item
	 * @return indicates whether it was removed
	 */
	public <ItemType extends SurrogatedItem> 
	boolean remove(ItemType item) {
		return createContext().removeItem(item);
	}
	
	/**
	 * Finds a previously saved item.
	 * @param item a previously saved item
	 * @return an item, or null
	 */
	public <ItemType extends SurrogatedItem> 
	ItemType findWithId(ItemType item) {
		return createContext().withoutLogging().findItem(item);
	}
	
	/**
	 * Finds a previously saved item.
	 * @param item a previously saved item
	 * @return an item, or null
	 */
	public <ItemType extends HashedItem> 
	ItemType findWithHash(ItemType item) {
		return createContext().findItem(item);
	}
    
    /**
     * Finds matching items (if any).
     * @param <ItemType> an item type
     * @param builder a query builder
     * @return an item list, or empty
     */
    public <ItemType extends SurrogatedItem>
    List<ItemType> findItems(QueryBuilder builder) {
        return createContext().findItems(builder);
    }
	
	/**
	 * Uses data from (or with) a previously saved item.
	 * @param item a previously saved item
	 * @param usage an item usage
	 */
	public <ItemType extends SurrogatedItem> 
	void use(ItemType item, SurrogatedItem.Usage usage) {
		createContext().useItem(item, usage);
	}
	
} // ItemRepository
