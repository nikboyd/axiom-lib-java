/**
 * Copyright 2015 Nikolas Boyd.
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
import org.springframework.data.repository.CrudRepository;

/**
 * Associates a storage mechanism with its stored model type.
 *
 * @param <ModelType> a kind of Model
 * @param <StorageType> a kind of storage Repository
 * @author nik
 */
public class StorageMechanism<ModelType, StorageType extends CrudRepository<ModelType, Long>> {

    private final StorageType store;
    private final Class<StorageType> type;
    private final Class<?> modelType;

    /**
     * Constructs a new StorageBean.
     *
     * @param store a Repository
     * @param type a storage type
     * @param modelType a model type
     */
    public StorageMechanism(StorageType store, Class<StorageType> type, Class<?> modelType) {
        this.store = store;
        this.type = type;
        this.modelType = modelType;
    }

    /**
     * A Repository.
     *
     * @return a Repository
     */
    public StorageType getStore() {
        return this.store;
    }

    /**
     * A storage type.
     *
     * @return a kind of Repository
     */
    public Class<StorageType> getStorageType() {
        return this.type;
    }

    /**
     * A model type.
     *
     * @return a kind of model
     */
    public Class<?> getModelType() {
        return this.modelType;
    }

    /**
     * Returns the registered storage mechanism for a given modelType.
     *
     * @param <StorageType> a storage type
     * @param modelType a model type
     * @return a storage mechanism
     */
    public static <StorageType extends CrudRepository> StorageType get(Class<?> modelType) {
        if (Registry.Instance == null) {
            return null;
        }
        return (StorageType) Registry.Instance.getModelStorage(modelType);
    }

    /**
     * Returns the registered storage mechanism for a given modelType.
     *
     * @param <StorageType> a storage type
     * @param storeType a storage type
     * @return a storage mechanism
     */
    public static <StorageType extends CrudRepository> StorageType getStorage(Class<StorageType> storeType) {
        if (Registry.Instance == null) {
            return null;
        }
        return (StorageType) Registry.Instance.getStorage(storeType);
    }

    /**
     * A storage mechanism registry. Provides access to the configured storage mechanisms.
     */
    public static class Registry {

        public static Registry Instance = null;

        private final HashMap<String, StorageMechanism> map = new HashMap();
        private final HashMap<String, StorageMechanism> modelMap = new HashMap();

        /**
         * Returns a registered storage mechanism.
         *
         * @param <StorageType> a kind of Repository
         * @param storeType a storage type
         * @return a registered JPA Repository
         */
        public <StorageType extends CrudRepository> StorageType getStorage(Class<StorageType> storeType) {
            return (StorageType) map.get(storeType.getName()).getStore();
        }

        /**
         * Returns a registered storage mechanism.
         *
         * @param modelType a model type
         * @return a registered JPA Repository
         */
        public CrudRepository getModelStorage(Class<?> modelType) {
            return (CrudRepository) modelMap.get(modelType.getName()).getStore();
        }

        /**
         * Returns a registered storage type.
         *
         * @param modelType a model type
         * @return a registered storage type
         */
        public Class<?> getStorageType(Class<?> modelType) {
            return modelMap.get(modelType.getName()).getStorageType();
        }

        /**
         * Registers storage beans.
         *
         * @param beans the storage beans
         * @return a new Registry
         */
        public static Registry with(StorageMechanism... beans) {
            Registry result = new Registry();
            for (StorageMechanism bean : beans) {
                result.register(bean);
            }
            Instance = result;
            return result;
        }

        /**
         * Registers a storage bean.
         *
         * @param bean a storage bean
         */
        public void register(StorageMechanism bean) {
            map.put(bean.getStorageType().getName(), bean);
            modelMap.put(bean.getModelType().getName(), bean);
        }

        /**
         * A count of the registered stores.
         *
         * @return a count
         */
        public int size() {
            return this.map.size();
        }

    } // Registry

} // StorageMechanism
