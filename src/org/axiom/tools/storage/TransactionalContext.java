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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A transaction context for persistence operations.
 * 
 * <h4>TransactionalContext Responsibilities:</h4>
 * <ul>
 * <li>provides a persistent entity manager for use</li>
 * <li>performs various persistence operations for specific kinds of items</li>
 * </ul>
 *
 * <h4>Client Responsibilities:</h4>
 * <ul>
 * <li>properly configure an Hibernate backing stores</li>
 * </ul>
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class TransactionalContext {

	private static final Log Logger = LogFactory.getLog(TransactionalContext.class);
	private static final String FactoryName = "entity.manager.factory";
	private static final EntityManagerFactory CachedFactory = createFactory();
	
	/**
	 * JPA entity manager.
	 */
	protected EntityManager manager;

	/**
	 * Indicates whether to log exceptions during usage.
	 */
	protected boolean reportProblems = true;
	
	/**
	 * Report exceptions that are raised during usage.
	 * @return this usage
	 */
	public TransactionalContext withoutLogging() {
		this.reportProblems = false;
		return this;
	}
	
	/**
	 * Report exceptions that are raised during usage.
	 * @return this usage
	 */
	public TransactionalContext withLogging() {
		this.reportProblems = true;
		return this;
	}

	/**
	 * Counts the saved items of a specific kind.
	 * @param clazz an item type
	 * @return a count, or zero
	 */
	public <ItemType extends SurrogatedItem> 
	int count(final Class<ItemType> clazz) {
		if (clazz == null) return 0;
		return countWith(new Callable<Integer>() {
			public Integer call() throws Exception { return countAll(clazz); }
		});
	}

	/**
	 * Finds an item (if previously saved).
	 * @param item describes an item
	 * @return an item, or null
	 */
	public <ItemType extends SurrogatedItem> 
	ItemType findItem(final ItemType item) {
		if (item == null || item.getKey() == 0) return null;
		return commitWith(new Callable<ItemType>() {
			public ItemType call() throws Exception { return read(item); }
		});
	}

	/**
	 * Finds an item (if previously saved).
	 * @param item describes an item
	 * @return an item, or null
	 */
	public <ItemType extends HashedItem> 
	ItemType findItem(final ItemType item) {
		if (item == null) return null;
		return commitWith(new Callable<ItemType>() {
			public ItemType call() throws Exception { return find(item); }
		});
	}
	
	/**
	 * Provides an item for use within this transaction context.
	 * @param item describes an item
	 * @param usage an item usage scope
	 * @return an item (after use)
	 */
	public <ItemType extends SurrogatedItem> 
	ItemType useItem(final ItemType item, final SurrogatedItem.Usage usage) {
		if (usage == null) return null;
		if (item == null || item.getKey() == 0) return null;
		return commitWith(new Callable<ItemType>() {
			public ItemType call() throws Exception { 
				ItemType result = find(item);
				usage.use(result);
				return result;
			}
		});
	}

	/**
	 * Saves an item persistently.
	 * @param item an item
	 * @return the saved item
	 */
	public <ItemType extends SurrogatedItem> 
	ItemType saveItem(final ItemType item) {
		return commitWith(new Callable<ItemType>() {
			public ItemType call() throws Exception { return save(item); }
		});
	}

	/**
	 * Saves a composite item persistently.
	 * @param item a composite item
	 * @return the save item
	 */
	public <ItemType extends SurrogatedComposite> 
	ItemType saveItem(final ItemType item) {
		return commitWith(new Callable<ItemType>() {
			public ItemType call() throws Exception { return save(item); }
		});
	}
	
	public <ItemType extends SurrogatedComposite> 
	int countMapComponents(final ItemType item, final int mapIndex) {
		return commitWith(new Callable<Integer>() {
			public Integer call() throws Exception { return countMapElements(item, mapIndex); }
		});
	}
	
	public <ItemType extends SurrogatedComposite, ResultType extends SurrogatedItem> 
	ResultType getMapComponent(final ItemType item, final int mapIndex, final Object key) {
		return commitWith(new Callable<ResultType>() {
			public ResultType call() throws Exception { return getMapElement(item, mapIndex, key); }
		});
	}

	/**
	 * Removes an item from persistent storage.
	 * @param item an item
	 * @return whether the item was removed
	 */
	public <ItemType extends SurrogatedItem> 
	boolean removeItem(final ItemType item) {
		if (item == null || item.getKey() == 0) return false;
		return commitWith(new Callable<Boolean>() {
			public Boolean call() throws Exception { return remove(item); }
		});
	}

	/**
	 * Counts all saved items of a specific kind.
	 * @param clazz an item type
	 * @return a count, or zero
	 * @throws Exception if raised while saving this item
	 */
	private <ItemType extends SurrogatedItem> 
	int countAll(Class<ItemType> clazz) throws Exception {
		try {
			return buildCountQuery(clazz).getSingleResult().intValue();
		} catch (Exception e) {
			reportCountProblem(clazz, e);
			throw e;
		}
	}

	/**
	 * Finds an item (if previously saved).
	 * @param item a hashed item
	 * @return an item, or null
	 * @throws Exception if raised while saving this item
	 */
	private <ItemType extends HashedItem> 
	ItemType find(ItemType item) throws Exception {
		if (item == null) return null;
		Query query = buildHashQuery(item);
		try {
			List results = query.getResultList();
			if (results.size() == 0) return null;
			return (ItemType) query.getSingleResult();
		} catch (Exception e) {
			this.reportFindHashProblem(item);
			return null;
		}
	}

	/**
	 * Finds an item (if previously saved).
	 * @param item describes an item
	 * @return an item, or null
	 * @throws Exception if raised while saving this item
	 */
	private <ItemType extends SurrogatedItem> 
	ItemType find(ItemType item) throws Exception {
		if (item == null) return null;
		try {
			return (ItemType) manager.find(item.getClass(), item.getKey());
		} catch (Exception e) {
			reportFindProblem(item, e);
			throw e;
		}
	}
	
	/**
	 * Reads an item (if previously saved).
	 * @param item describes an item
	 * @return an item, or null
	 * @throws Exception if raised while saving this item
	 */
	private <ItemType extends SurrogatedItem> 
	ItemType read(ItemType item) throws Exception {
		try {
			ItemType result = (ItemType) manager.find(item.getClass(), item.getKey());
			if (result == null) reportMissingItem(item);
			return result;
		} catch (Exception e) {
			reportFindProblem(item, e);
			throw e;
		}
	}
	
	/**
	 * Saves an item persistently.
	 * @param item an item
	 * @return the saved item
	 * @throws Exception if raised while saving this item
	 */
	private <ItemType extends SurrogatedItem> 
	ItemType save(ItemType item) throws Exception {
		if (item == null) return null;
		try {
			if (item.getKey() > 0) {
				ItemType result = manager.merge(item);
				if (result == null) reportMissingItem(item);
				return result;
			}
			else {
				manager.persist(item);
				return item;
			}
		} catch (Exception e) {
			reportSaveProblem(item, e);
			throw e;
		}
	}
	
	/**
	 * Counts the mapped elements of a composite item.
	 * @param item a composite item
	 * @param mapIndex a map index
	 * @return a count of the selected components
	 * @throws Exception if raised while counting components
	 */
	private <ItemType extends SurrogatedComposite> 
	int countMapElements(ItemType item, int mapIndex) throws Exception {
		if (item == null) return 0;
		try {
			ItemType found = find(item);
			Object[] componentMaps = found.getComponentMaps();
			return ((Map<Object, SurrogatedItem>) componentMaps[mapIndex]).size();
		} catch (Exception e) {
			reportFindProblem(item, e);
			throw e;
		}
	}
	
	/**
	 * Returns a mapped component item from a composite.
	 * @param item a composite item
	 * @param mapIndex a map index
	 * @param key a map key
	 * @return a mapped component
	 * @throws Exception if raised during this operation
	 */
	private <ItemType extends SurrogatedComposite, ResultType extends SurrogatedItem> 
	ResultType getMapElement(ItemType item, int mapIndex, Object key) throws Exception {
		if (item == null) return null;
		try {
			ItemType found = find(item);
			Object[] componentMaps = found.getComponentMaps();
			return ((Map<Object, ResultType>) componentMaps[mapIndex]).get(key);
		} catch (Exception e) {
			reportFindProblem(item, e);
			throw e;
		}
	}
	
	/**
	 * Saves a composite along with all its associated components.
	 * @param item a composite item
	 * @return the saved composite item (after saving it)
	 * @throws Exception if raised while saving this item
	 */
	private <ItemType extends SurrogatedComposite> 
	ItemType save(ItemType item) throws Exception {
		if (item == null) return null;
		try {
			Object[] componentMaps = item.getComponentMaps();
			for (Object componentMap : componentMaps) {
				saveComponents((Map<Object, SurrogatedItem>) componentMap);
			}
			
			Object[] componentSets = item.getComponentSets();
			for (Object componentSet : componentSets) {
				saveComponents((Set<SurrogatedItem>) componentSet);
			}
			
			if (item.getKey() > 0) {
				ItemType result = manager.merge(item);
				if (result == null) reportMissingItem(item);
				return result;
			}
			else {
				manager.persist(item);
				return item;
			}
		} catch (Exception e) {
			reportSaveProblem(item, e);
			throw e;
		}
	}

	/**
	 * Removes an item from persistent storage.
	 * @param item an item
	 * @return whether the item was removed
	 * @throws Exception if raised while saving this item
	 */
	private <ItemType extends SurrogatedItem> 
	boolean remove(ItemType item) throws Exception {
		try {
			ItemType foundItem = find(item);
			if (foundItem == null) return false;
			manager.remove(foundItem);
			return true;
		} catch (Exception e) {
			reportRemoveProblem(item, e);
			throw e;
		}
	}
	
	/**
	 * Saves the components associated with a composite within 
	 * the active scope of a transaction.
	 * @param components some components
	 * @throws Exception if raised while saving this item
	 */
	private <KeyType, ItemType extends SurrogatedItem>
	void saveComponents(Map<KeyType, ItemType> components) throws Exception {
		for (KeyType key : components.keySet()) {
			components.put(key, save(components.get(key)));
		}
	}
	
	/**
	 * Saves the components associated with a composite within 
	 * the active scope of a transaction.
	 * @param components some components
	 * @throws Exception if raised while saving this item
	 */
	private <ItemType extends SurrogatedItem> 
	void saveComponents(Set<ItemType> components) throws Exception {
		HashSet<ItemType> results = new HashSet<ItemType>(components);
		for (ItemType item : components) {
			results.add(save(item));
		}
		
		components.clear();
		components.addAll(results);
	}

	/**
	 * Counts all saved items of a specific kind.
	 * @param operation a counting operation
	 * @return a count, or zero
	 */
	public int countWith(Callable<Integer> operation) {
		int result = 0;
		if (operation == null) return result;

		manager = createManager();
		try {
			manager.getTransaction().begin();
			result = operation.call().intValue();
			manager.getTransaction().commit();
		} catch (Exception e) {
			manager.getTransaction().rollback();
		}
		finally {
			if (manager.isOpen())
				manager.close();
		}
		return result;
	}
	
	/**
	 * Performs a persistence operation within a transaction context.
	 * @param operation a persistence operation
	 * @return an item, or null
	 */
	public <ResultType> 
	ResultType commitWith(Callable<ResultType> operation) {
		ResultType result = null;
		if (operation == null) return result;

		manager = createManager();
		try {
			manager.getTransaction().begin();
			result = operation.call();
			manager.getTransaction().commit();
		} catch (Exception e) {
			manager.getTransaction().rollback();
		}
		finally {
			if (manager.isOpen())
				manager.close();
		}
		return result;
	}

	/**
	 * Returns a configured entity manager factory.
	 * @return a new EntityManagerFactory
	 */
	private static EntityManagerFactory createFactory() {
		return Persistence.createEntityManagerFactory(FactoryName);
	}
	
	/**
	 * Returns a new entity manager.
	 * @return a new EntityManager
	 */
	private static EntityManager createManager() {
		return CachedFactory.createEntityManager();
	}
	
	/**
	 * Builds a count query.
	 * @param clazz am item type
	 * @return a count query
	 */
	private <ItemType extends SurrogatedItem> 
	TypedQuery<Long> buildCountQuery(Class<ItemType> clazz) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		Root<ItemType> root = query.from(clazz);
		query.select(builder.count(root));
		return manager.createQuery(query);
	}
	
	/**
	 * Builds a hashed item query.
	 * @param item a hashed item
	 * @return a query
	 */
	private <ItemType extends HashedItem> 
	Query buildHashQuery(ItemType item) {
		String className = item.getClass().getSimpleName();
		String query = "select x from " + className + " x where x.hashKey = :key";		
		return manager.createQuery(query)
						.setParameter("key", item.getHashKey())
						.setMaxResults(1);
	}
	
	/**
	 * Reports a problem.
	 * @param message a message
	 * @param e an exception
	 */
	private void reportProblem(String message, Exception e) {
		if (this.reportProblems) {
			Logger.error(message + e.getMessage(), e);
		}
	}

	/**
	 * Reports a problem raised during element counting.
	 * @param itemClass an item class
	 * @param e and exception
	 */
	private <ItemType extends SurrogatedItem> 
	void reportCountProblem(Class<ItemType> itemClass, Exception e) {
		String message = itemClass.getSimpleName() + " COUNT failed ";
		reportProblem(message, e);
	}
	
	/**
	 * Reports a problem raised during a save operation.
	 * @param item an item
	 * @param e an exception
	 */
	private <ItemType extends SurrogatedItem> 
	void reportSaveProblem(ItemType item, Exception e) {
		item.describe();
		String message = item.getClass().getSimpleName() + " SAVE failed ";
		if (item.getKey() > 0) {
			message += "ID = " + item.getKey() + " ";
		}
		reportProblem(message, e);
	}
	
	/**
	 * Reports a problem raised during a find operation.
	 * @param item a hashed item
	 */
	private <ItemType extends HashedItem> 
	void reportFindHashProblem(ItemType item) {
		String message = item.getClass().getSimpleName() + " FIND failed hash = " + item.getHashKey() + " ";
		Logger.info(message);
	}
	
	private <ItemType extends SurrogatedItem> 
	void reportFindProblem(ItemType item, Exception e) {
		String message = item.getClass().getSimpleName() + " FIND failed ID = " + item.getKey() + " ";
		reportProblem(message, e);
	}
	
	private <ItemType extends SurrogatedItem> 
	void reportRemoveProblem(ItemType item, Exception e) {
		String message = item.getClass().getSimpleName() + " REMOVE failed ID = " + item.getKey() + " ";
		reportProblem(message, e);
	}
	
	private <ItemType extends SurrogatedItem> 
	void reportMissingItem(ItemType item) {
		if (this.reportProblems) {
			String message = item.getClass().getSimpleName() + " MISSING for ID = " + item.getKey() + " ";
			Logger.warn(message);
		}
	}

} // EntityUsage
