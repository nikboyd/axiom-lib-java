/**
 * Copyright 2013 Nikolas Boyd.
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
package org.axiom.tools.storage;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A persistent item with a surrogate key.
 * @param <ItemType> a kind of derived persistent item
 * 
 * <h4>Surrogated Responsibilities:</h4>
 * <ul>
 * <li>knows a surrogate key (ID)</li>
 * <li>saves this item persistently</li>
 * <li>reloads this item from its backing store</li>
 * <li>removes this item from its backing store</li>
 * </ul>
 *
 * <h4>Client Responsibilities:</h4>
 * <ul>
 * <li>derived classes extend this item with domain model data</li>
 * </ul>
 */
@MappedSuperclass
@SuppressWarnings("unchecked")
public abstract class Surrogated<ItemType> implements SurrogatedItem {

	private static final Log Logger = LogFactory.getLog(Surrogated.class);
	protected static final String Empty = "";
	protected static final String Blank = " ";
	protected static final String Comma = ",";
	private static final String AND = "&&";

	protected static final String PosixSymbols = "\\p{S}";
	protected static final String PosixPunctuators = "\\p{P}";
	protected static final String AllowedSymbols = "/#";
	protected static final String ExcludedSymbols = "[^" + AllowedSymbols + "]";
	protected static final String PunctuationFilter = "["+ PosixSymbols + PosixPunctuators + AND + ExcludedSymbols + "]";
	protected static final String MultipleSpaceFilter = " +";

	/**
	 * A repository shared by instances of this kind.
	 */
	protected static final ItemRepository Repository = new ItemRepository();

	/**
	 * A surrogate key. The key value is generated automatically by the configured
	 * persistence framework.
	 */
	@Id
	@Column(name="ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	protected long Id;

	/**
	 * A surrogate key.
	 */
	@Override
	@XmlTransient
	public long getKey() {
		return Id;
	}
	
	/**
	 * Indicates whether this item was previously saved.
	 */
	@XmlTransient
	public boolean isSaved() {
		return getKey() > 0;
	}

	/**
	 * Returns this item properly typed.
	 * @return this item properly typed
	 */
	@Override
	public ItemType asItem() {
		return (ItemType) this;
	}

	/**
	 * Saves this item.
	 * @return this item
	 */
	public ItemType save() {
		return Repository.save(this).asItem();
	}

	/**
	 * A default implementation for a SurrogatedComposite.
	 * Derived classes that implement SurrogatedComposite 
	 * override this method if needed.
	 * @return empty
	 */
	@XmlTransient
	public Object[] getComponentMaps() {
		Object[] results = { };
		return results;
	}

	/**
	 * A default implementation for a SurrogatedComposite.
	 * Derived classes that implement SurrogatedComposite 
	 * override this method if needed.
	 * @return empty
	 */
	@XmlTransient
	public Object[] getComponentSets() {
		Object[] results = { };
		return results;
	}

	/**
	 * A default implementation for a SurrogatedComposite.
	 * Derived classes that implement SurrogatedComposite 
	 * override this method if needed.
	 * @return empty
	 */
	@XmlTransient
	public SurrogatedItem[] getComponents() {
		SurrogatedItem[] results = { };
		return results;
	}

	/**
	 * A default implementation for a SurrogatedComposite.
	 * Derived classes that implement SurrogatedComposite 
	 * override this method if needed.
	 * @param components saved components
	 */
	public void setComponents(SurrogatedItem[] components) {
		// override this if needed
	}

	/**
	 * Reloads this item (if available).
	 * @return this item, or null
	 */
	public ItemType reload() {
		Surrogated<ItemType> result = Repository.findWithId(this);
		if (result == null) return null;
		return result.asItem();
	}
	
	/**
	 * Reloads this item and provides it to a scope for reference.
	 * @param usage an item usage scope
	 */
	public void useWithin(SurrogatedItem.Usage usage) {
		Repository.use(this, usage);
	}

	/**
	 * Removes this item from persistent storage.
	 * @return whether this item was removed
	 */
	public boolean remove() {
		return Repository.remove(this);
	}
	
	/**
	 * A logger for this kind of item.
	 */
	public Log getLogger() {
		return Logger;
	}

	/**
	 * Describes this item in the log.
	 */
	public void describe() {
		getLogger().info("key = " + getKey());
	}

	/**
	 * Normalizes text with full capitalization, without punctuation, and without extraneous whitespace.
	 * @param text some text
	 * @return normalized text
	 */
	public static String normalizeWords(String text) {
		return WordUtils.capitalizeFully(StringUtils.defaultString(text).trim())
				.replaceAll(PunctuationFilter, Empty).replaceAll(MultipleSpaceFilter, Blank);
	}

	/**
	 * Normalizes code as upper case.
	 * @param codeText code text
	 * @return a normalized code
	 */
	public static String normalizeCode(String codeText) {
		return StringUtils.defaultString(codeText).trim().toUpperCase();
	}

} // Surrogated<ItemType>
