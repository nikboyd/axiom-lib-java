/**
 * Copyright 2015 Nikolas Boyd.
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
import org.axiom_tools.domain.Person;
import org.axiom_tools.storage.Hashed.Search;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * A storage mechanism for phone numbers.
 * @author nik
 */
public interface PersonStorage
        extends CrudRepository<Person, Long>, Search<Person> {

    @Query("SELECT p FROM Person p WHERE p.key = :key")
    Person findKey(@Param("key") Long key);

    @Override
    @Query("SELECT p FROM Person p WHERE p.hashKey = :hashKey")
    Person findHash(@Param("hashKey") Integer key);

    @Query("SELECT p FROM Person p WHERE p.name like :personName")
    List<Person> findLike(@Param("personName") String personName);
    
    @Query("SELECT p FROM Person p join p.contact c join c.emails em WHERE em.hashKey = :emailKey")
    List<Person> findEmail(@Param("emailKey") Integer emailKey);
    
    @Query("SELECT p FROM Person p join p.contact c join c.phones ph WHERE ph.hashKey = :phoneKey")
    List<Person> findPhone(@Param("phoneKey") Integer phoneKey);

} // PersonStorage
