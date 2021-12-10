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

import java.util.*;
import java.io.Serializable;
import javax.persistence.*;
import javax.xml.bind.annotation.*;

import org.slf4j.Logger;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.springframework.data.repository.CrudRepository;

/**
 * A persistent item with a surrogate key.
 *
 * @param <ItemType> a kind of derived persistent item
 */
@MappedSuperclass
@SuppressWarnings("unchecked")
public abstract class Surrogated<ItemType> implements SurrogatedItem, Serializable {

    protected static final String Empty = "";
    protected static final String Blank = " ";
    protected static final String Comma = ",";
    private static final String AND = "&&";

    protected static final String PosixSymbols = "\\p{S}";
    protected static final String PosixPunctuators = "\\p{P}";
    protected static final String AllowedSymbols = "/#";
    protected static final String ExcludedSymbols = "[^" + AllowedSymbols + "]";
    protected static final String PunctuationFilter = "[" + PosixSymbols + PosixPunctuators + AND + ExcludedSymbols + "]";
    protected static final String MultipleSpaceFilter = " +";

    /**
     * Returns the storage mechanism for a given model type.
     *
     * @param <ItemType> a kind of model
     * @param itemType a kind of model
     * @return a storage mechanism
     */
    protected static <ItemType> CrudRepository<ItemType, Long> getStore(Class<?> itemType) {
        CrudRepository<ItemType, Long> store = (CrudRepository<ItemType, Long>) StorageMechanism.get(itemType);
        return (store == null ? null : store);
    }

    /**
     * Returns the storage mechanism for items of this kind.
     *
     * @param <StoreType> a storage type
     * @return a storage mechanism
     */
    protected <StoreType extends CrudRepository<ItemType, Long>> StoreType getStore() {
        return (StoreType) getStore(getClass());
    }

    /**
     * A logger for this kind of item.
     */
    protected abstract Logger getLogger();

    /**
     * A surrogate key. The key value is generated automatically by the configured persistence framework.
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected long key;

    /**
     * A surrogate key.
     */
    @Override
    @XmlAttribute
    public long getKey() {
        return this.key;
    }

    public void setKey(long key) {
        this.key = key;
    }

    /**
     * Indicates whether this item was previously saved.
     */
    @Override
    public boolean wasSaved() {
        return getKey() > 0;
    }

    /**
     * Returns this item properly typed.
     *
     * @return this item properly typed
     */
    @Override
    public ItemType asItem() {
        return (ItemType) this;
    }

    @Override
    public ItemType saveItem() {
        if (this.isComposite()) {
            saveComponents();
        }
        return getStore().save(this.asItem());
    }

    private boolean isComposite() {
        return this instanceof SurrogatedComposite;
    }

    private SurrogatedComposite asComposite() {
        return (SurrogatedComposite) this;
    }

    private void saveComponents() {
        try {
            Object[] componentMaps = this.asComposite().componentMaps();
            for (Object componentMap : componentMaps) {
                saveComponents((Map<Object, SurrogatedItem>) componentMap);
            }

            Object[] componentSets = this.asComposite().componentSets();
            for (Object componentSet : componentSets) {
                saveComponents((Set<SurrogatedItem>) componentSet);
            }

            saveComponents(this.asComposite().components());
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
        }
    }

    private <KeyType, ComponentType extends SurrogatedItem>
            void saveComponents(Map<KeyType, ComponentType> components) throws Exception {
        for (KeyType mapKey : components.keySet()) {
            components.put(mapKey, (ComponentType) components.get(mapKey).saveItem());
        }
    }

    private <ComponentType extends SurrogatedItem>
            void saveComponents(Set<ComponentType> components) throws Exception {
        HashSet<ComponentType> results = new HashSet<>(components);
        for (ComponentType component : components) {
            results.add((ComponentType) component.saveItem());
        }

        components.clear();
        components.addAll(results);
    }

    private void saveComponents(SurrogatedItem[] components) {
        if (components.length == 0) {
            return;
        }
        for (int index = 0; index < components.length; index++) {
            components[index] = components[index].saveItem();
        }
        this.asComposite().components(components);
    }

    /**
     * Removes this item from its backing store.
     *
     * @return whether this item was removed
     */
    public boolean removeItem() {
        if (getKey() == 0) {
            return false;
        }
        getStore().delete(this.asItem());
        return true;
    }

    /**
     * Finds this item.
     *
     * @return this item
     */
    public ItemType findItem() {
        if (getKey() == 0) {
            return this.asItem();
        }
        Optional<ItemType> result = getStore().findById(this.getKey());
        return result.isPresent() ? result.get() : null;
    }

    /**
     * A default implementation for a SurrogatedComposite. Derived classes that implement SurrogatedComposite override
     * this method if needed.
     *
     * @return empty
     */
    public Object[] componentMaps() {
        Object[] results = {};
        return results;
    }

    /**
     * A default implementation for a SurrogatedComposite. Derived classes that implement SurrogatedComposite override
     * this method if needed.
     *
     * @return empty
     */
    public Object[] componentSets() {
        Object[] results = {};
        return results;
    }

    /**
     * A default implementation for a SurrogatedComposite. Derived classes that implement SurrogatedComposite override
     * this method if needed.
     *
     * @return empty
     */
    public SurrogatedItem[] components() {
        SurrogatedItem[] results = {};
        return results;
    }

    /**
     * A default implementation for a SurrogatedComposite. Derived classes that implement SurrogatedComposite override
     * this method if needed.
     *
     * @param components saved components
     */
    public void components(SurrogatedItem[] components) {
        // override this if needed
    }

    /**
     * Describes this item in the log.
     */
    @Override
    public void describe() {
        getLogger().info("key = " + getKey());
    }

    /**
     * Normalizes text with full capitalization, without punctuation, and without extraneous whitespace.
     *
     * @param text some text
     * @return normalized text
     */
    public static String normalizeWords(String text) {
        return WordUtils.capitalizeFully(StringUtils.defaultString(text).trim())
                .replaceAll(PunctuationFilter, Empty).replaceAll(MultipleSpaceFilter, Blank);
    }

    /**
     * Normalizes code as upper case.
     *
     * @param codeText code text
     * @return a normalized code
     */
    public static String normalizeCode(String codeText) {
        return StringUtils.defaultString(codeText).trim().toUpperCase();
    }

} // Surrogated<ItemType>
