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
package org.axiom_tools.context;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.*;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.*;

/**
 * Loads configured beans from a Spring context.
 *
 * <h4>SpringContext Responsibilities:</h4>
 * <ul>
 * <li>supports bean context configuration on the class path</li>
 * <li>supports bean context configuration in the file system</li>
 * <li>caches loaded contexts by name</li>
 * </ul>
 *
 * <h4>Client Responsibilities:</h4>
 * <ul>
 * <li>supply a bean type, and possibly a bean name</li>
 * </ul>
 */
public class SpringContext {

    private static final Logger Log = LoggerFactory.getLogger(SpringContext.class);

    private static final String Dot = ".";
    private static final String Empty = "";
    private static final String Dollar = "$";

    /**
     * The standard context file name.
     */
    private static final String StandardFile = "spring-context.xml";

    /**
     * The loaded context cache.
     */
    private static final HashMap<String, SpringContext> ContextMap = new HashMap();

    private String contextName;

    private ApplicationContext cachedContext;

    /**
     * Returns a bean loaded from a configured context.
     *
     * @param <BeanType> a bean type
     * @param beanType a kind of bean
     * @return a bean, or null
     */
    public static <BeanType> BeanType getConfigured(Class<BeanType> beanType) {
        return standardContext().getBean(beanType);
    }

    /**
     * Returns a bean loaded from a configured context.
     *
     * @param <BeanType> a bean type
     * @param beanType a kind of bean
     * @param beanName a bean name
     * @return a bean, or null
     */
    public static <BeanType> BeanType getConfigured(Class<BeanType> beanType, String beanName) {
        return standardContext().getBean(beanType, beanName);
    }

    /**
     * Returns the standard context.
     *
     * @return the standard context
     */
    public static SpringContext standardContext() {
        return SpringContext.named(StandardFile);
    }

    /**
     * Returns a specific named context.
     *
     * @param contextName a context (file) name
     * @return a specific named context
     */
    public static SpringContext named(String contextName) {
        if (StringUtils.defaultString(contextName).isEmpty()) {
            return null; // no such context
        }
        if (ContextMap.containsKey(contextName)) {
            // already cached context
            return ContextMap.get(contextName);
        }

        // cache the named context
        SpringContext result = new SpringContext();
        result.contextName = contextName;
        ContextMap.put(contextName, result);
        return result;
    }

    /**
     * Configures this context to load data from the class path.
     *
     * @return this context
     */
    public SpringContext fromClassPath() {
        this.cachedContext = new ClassPathXmlApplicationContext(this.contextName);
        return this;
    }

    /**
     * Configures this context to load data from the file system.
     *
     * @return this context
     */
    public SpringContext fromFileSystem() {
        this.cachedContext = new FileSystemXmlApplicationContext(this.contextName);
        return this;
    }

    /**
     * Returns a specific kind of bean.
     *
     * @param <BeanType> a bean type
     * @param beanType a kind of bean
     * @return a bean, or null
     */
    public <BeanType> BeanType getBean(Class<BeanType> beanType) {
        if (beanType == null) {
            return null;
        }
        return getBean(beanType, Empty);
    }

    /**
     * Returns a specific named bean.
     *
     * @param <BeanType> a bean type
     * @param beanType a kind of bean
     * @param beanName a bean name
     * @return a bean, or null
     */
    @SuppressWarnings("unchecked")
    public <BeanType> BeanType getBean(Class<BeanType> beanType, String beanName) {
        if (beanType == null) {
            return null; // unknown class
        }
        if (StringUtils.defaultString(beanName).isEmpty()) {
            beanName = getStandardBeanName(beanType);
        }

        // try locating with bean name
        if (getContext().containsBean(beanName)) {
            return (BeanType) getContext().getBean(beanName);
        }

        // report missing bean
        String className = beanType.getName();
        reportMissing(className, beanName);

        // try locating with simple class name
        String simpleName = beanType.getSimpleName();
        if (className.contains(Dollar)) {
            if (getContext().containsBean(simpleName)) {
                return (BeanType) getContext().getBean(simpleName);
            }
        }

        // report total failure to locate bean
        reportMissing(className, simpleName);
        return null;
    }

    /**
     * Returns the standard bean name for a given class.
     *
     * @param beanType a bean type
     * @return a bean name
     */
    private static String getStandardBeanName(Class<?> beanType) {
        String packageName = beanType.getPackage().getName();
        if (packageName.length() > 0) {
            packageName += Dot;
        }
        int index = packageName.length();
        return beanType.getName().substring(index).replace(Dollar, Empty);
    }

    /**
     * Reports that an expected bean was missing from its configuration.
     *
     * @param className a class name
     * @param beanName a bean name
     */
    private void reportMissing(String className, String beanName) {
        Log.warn("can't find a configured bean named '" + beanName + "' in "
                + this.contextName + " type " + className);
    }

    /**
     * Returns the configured context.
     *
     * @return the configured context
     */
    public ApplicationContext getContext() {
        if (this.cachedContext == null) {
            fromClassPath();
        }
        return this.cachedContext;
    }
}
