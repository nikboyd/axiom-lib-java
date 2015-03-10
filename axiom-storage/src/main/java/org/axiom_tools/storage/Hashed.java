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

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * An item that can be saved persistently and uniquely 
 * identified using a hash of its contents.
 * @param <ItemType> a kind of derived persistent item
 * 
 * <h4>Hashed&lt;ItemType&gt; Responsibilities:</h4>
 * <ul>
 * <li>knows a surrogate key (ID)</li>
 * <li>knows a hash key (HASH_KEY)</li>
 * <li>immutable once constructed</li>
 * </ul>
 *
 * <h4>Client Responsibilities:</h4>
 * <ul>
 * <li>derived classes must override hashCode()</li>
 * <li>derived classes must remain immutable after construction</li>
 * </ul>
 */
@MappedSuperclass
@SuppressWarnings("unchecked")
public abstract class Hashed<ItemType> 
	extends Surrogated<ItemType> implements HashedItem {

	/**
	 * A hash of the item contents.
	 */
	@Column(name = "HASH_KEY", nullable = false)
	protected int hashKey = 0;

	/**
	 * @return this item
	 */
	@Override
	public ItemType asItem() {
		return (ItemType) this;
	}
	
	/**
	 * Resets the id and hash of this item.
	 */
	protected void reset() {
		this.key = 0;
		this.hashKey = 0;
	}
	
	/**
	 * Prepares this item for saving.
	 */
	public void prepareHash() {
		if (this.hashKey == 0) {
			this.hashKey = hashCode();
		}
	}

	/**
	 * The hash of the item contents.
	 */
	@Override
	public int hashKey() {
		prepareHash();
		return this.hashKey;
	}

	/**
	 * Finds a saved instance of this item (if possible).
	 * @return a saved item whose hash matches this, or null
	 */
	public ItemType find() {
		return (ItemType) Repository.findWithHash(this);
	}

	/**
	 * A saved instance whose hash matches that of this item.
	 */
	@Override
	public ItemType save() {
		if (getKey() > 0) 
			return this.asItem();
		
		prepareHash();
		ItemType result = find();
		if (result == null) {
			return super.save();
		}
		else {
			return result;
		}
	}

} // Hashed<ItemType>
