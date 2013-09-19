package org.axiom.tools.storage;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * An item that can be saved persistently and uniquely identified 
 * with using a hash of the item contents.
 * @param <ItemType> a kind of persistent item
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
		this.Id = 0;
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
	public int getHashKey() {
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
