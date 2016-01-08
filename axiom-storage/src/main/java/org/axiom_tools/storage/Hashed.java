/**
 * Copyright 2013,2015 Nikolas Boyd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.axiom_tools.storage;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import org.springframework.data.repository.CrudRepository;

/**
 * An item uniquely identified using a hash of its contents.
 *
 * @param <ItemType> a kind of derived persistent item
 */
@MappedSuperclass
@SuppressWarnings("unchecked")
public abstract class Hashed<ItemType>
        extends Surrogated<ItemType> implements HashedItem {

    /**
     * A hash of the item contents.
     */
    @Column(name = "hash_key", nullable = false)
    protected int hashKey = 0;

    /**
     * Returns this item.
     *
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
     * Finds this item.
     *
     * @return this item
     */
    @Override
    public ItemType findItem() {
        if (getKey() == 0) {
            return findWithHash();
        }
        return getStore().findOne(this.getKey());
    }

    /**
     * Finds this item with its hash.
     *
     * @return this item
     */
    public ItemType findWithHash() {
        prepareHash();
        return getSearchStore().findHash(hashKey());
    }

    protected Search<ItemType> getSearchStore() {
        return (Search<ItemType>) getStore();
    }

    /**
     * Saves this item.
     *
     * @return this item after saving it
     */
    @Override
    public ItemType saveItem() {
        if (getKey() > 0) {
            return findItem();
        }

        ItemType result = findWithHash();
        if (result == null) {
            return super.saveItem();
        } else {
            return result;
        }
    }

    /**
     * Defines protocol for searching for a hashed item.
     *
     * @param <ItemType> an item type
     */
    public static interface Search<ItemType> extends CrudRepository<ItemType, Long> {

        /**
         * Finds a hashed item.
         *
         * @param hashKey a hash key value
         * @return a hashed item, or null
         */
        public ItemType findHash(Integer hashKey);
    }

} // Hashed<ItemType>
