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

import org.axiom_tools.domain.EmailAddress;
import org.axiom_tools.storage.Hashed.Search;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * A storage mechanism for email addresses.
 * @author nik
 */
public interface EmailStorage
        extends CrudRepository<EmailAddress, Long>, Search<EmailAddress> {

    @Query("SELECT e FROM EmailAddress e WHERE e.key = :key")
    EmailAddress findKey(@Param("key") Long key);

    @Override
    @Query("SELECT e FROM EmailAddress e WHERE e.hashKey = :hashKey")
    EmailAddress findHash(@Param("hashKey") Integer key);

} // EmailStorage
