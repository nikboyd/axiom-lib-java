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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * Builds queries or count queries.
 *
 * <h4>QueryBuilder Responsibilities:</h4>
 * <ul>
 * <li>knows either query text or a query name</li>
 * <li>knows either count text or a count name</li>
 * <li>knows query parameter values</li>
 * <li>builds a query and / or a count query from its values</li>
 * </ul>
 *
 * <h4>Client Responsibilities:</h4>
 * <ul>
 * <li>properly constructs a QueryBuilder</li>
 * </ul>
 */
public class QueryBuilder {

    private static final String QueryName = "queryName";
    private static final String QueryText = "queryText";
    private static final String CountName = "countName";
    private static final String CountText = "countText";
    private static final String[] ReservedWords = {QueryName, QueryText, CountName, CountText};
    private static final List<String> Reservations = Arrays.asList(ReservedWords);

    private final HashMap<String, Object> values = new HashMap<>();

    /**
     * Returns a new QueryBuilder.
     *
     * @param queryName a query name
     * @return a new QueryBuilder
     */
    public static QueryBuilder withQueryName(String queryName) {
        QueryBuilder result = new QueryBuilder();
        return result.withValue(QueryName, queryName);
    }

    /**
     * Returns a new QueryBuilder.
     *
     * @param queryText a query text
     * @return a new QueryBuilder
     */
    public static QueryBuilder withQueryText(String queryText) {
        QueryBuilder result = new QueryBuilder();
        return result.withValue(QueryText, queryText);
    }

    /**
     * Adds a named count to this builder.
     *
     * @param countName a count name
     * @return this QueryBuilder
     */
    public QueryBuilder withCountNamed(String countName) {
        return this.withValue(CountName, countName);
    }

    /**
     * Adds a text count to this builder.
     *
     * @param countText contains the text of a count query
     * @return this QueryBuilder
     */
    public QueryBuilder withCountText(String countText) {
        return this.withValue(CountText, countText);
    }

    /**
     * Adds a named value to this builder.
     *
     * @param valueName a value name
     * @param namedValue a named value
     * @return this QueryBuilder
     */
    public QueryBuilder withValue(String valueName, Object namedValue) {
        this.values.put(valueName, namedValue);
        return this;
    }

    /**
     * Adds some named values to this builder.
     *
     * @param namedValues some named values
     * @return this QueryBuilder
     */
    public QueryBuilder withValues(Map<String, Object> namedValues) {
        this.values.putAll(namedValues);
        return this;
    }

    /**
     * Indicates whether this builder contains a query name.
     */
    public boolean hasNamedQuery() {
        return this.values.containsKey(QueryName);
    }

    /**
     * Indicates whether this builder contains query text.
     */
    public boolean hasTextQuery() {
        return this.values.containsKey(QueryText);
    }

    /**
     * Indicates whether this builder contains a count name.
     */
    public boolean hasNamedCount() {
        return this.values.containsKey(QueryName);
    }

    /**
     * Indicates whether this builder contains count text.
     */
    public boolean hasTextCount() {
        return this.values.containsKey(QueryText);
    }

    /**
     * A query name.
     */
    public String getQueryName() {
        return (String) this.values.get(QueryName);
    }

    /**
     * The text of a query.
     */
    public String getQueryText() {
        return (String) this.values.get(QueryText);
    }

    /**
     * A count name.
     */
    public String getCountName() {
        return (String) this.values.get(CountName);
    }

    /**
     * The text of a count query.
     */
    public String getCountText() {
        return (String) this.values.get(CountText);
    }

    /**
     * Builds a new Query.
     *
     * @param manager a query factory
     * @return a new Query
     */
    public Query buildQuery(EntityManager manager) {
        if (hasNamedQuery()) {
            return queryWithValues(manager.createNamedQuery(getQueryName()));
        }

        if (hasTextQuery()) {
            return queryWithValues(manager.createQuery(getQueryText()));
        }

        return null;
    }

    /**
     * Builds a new count Query.
     *
     * @param manager a query factory
     * @return a new count Query
     */
    public Query buildCount(EntityManager manager) {
        if (hasNamedCount()) {
            return queryWithValues(manager.createNamedQuery(getCountName()));
        }

        if (hasTextCount()) {
            return queryWithValues(manager.createQuery(getCountText()));
        }

        return null;
    }

    /**
     * Returns a query after adding parameter values from this builder.
     *
     * @param query a query
     * @return a Query
     */
    private Query queryWithValues(Query query) {
        for (String valueName : this.values.keySet()) {
            if (!Reservations.contains(valueName)) {
                query.setParameter(valueName, this.values.get(valueName));
            }
        }
        return query;
    }

} // QueryBuilder
