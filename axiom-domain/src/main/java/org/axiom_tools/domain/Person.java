/**
 * Copyright 2013,2015 Nikolas Boyd.
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
package org.axiom_tools.domain;

import java.util.List;
import javax.persistence.*;
import javax.xml.bind.annotation.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.axiom_tools.storage.QueryBuilder;
import org.hibernate.annotations.Index;

/**
 * Identifies and describes a person.
 * 
 * <h4>Person Responsibilities:</h4>
 * <ul>
 * <li>knows a personal name</li>
 * <li>knows personal contact information</li>
 * </ul>
 */
@Entity
@Table(name = "PERSON")
@XmlRootElement(name = "Person", namespace = "##default")
@SuppressWarnings("unchecked")
public class Person extends Party {

	private static final long serialVersionUID = 1001001L;
	private static final Log Logger = LogFactory.getLog(Person.class);

	@Override
	protected Log getLogger() {
		return Logger;
	}

	public static int count() {
		return Repository.count(Person.class);
	}

	@Override
	@Index(name = "IX_PARTY_HASH", columnNames = { "HASH_KEY" })
	public int hashCode() {
		String hashSource = getName();
		return hashSource.hashCode();
	}

	public Person withContact(Contact contact) {
		setContact(contact);
		return this;
	}

	/**
	 * Returns a new Person.
	 * @param name a name
	 * @return a new Person
	 */
	public static Person named(String name) {
		Person result = new Person();
		result.setName(name);
		return result;
	}
    
    /**
     * Returns a new Person (intended query).
     * @param text query text
     * @return a new Person
     */
    public static Person like(String text) {
		Person result = new Person();
		result.name = text;
		return result;
    }
    
    /**
     * Returns a new Person (intended query).
     * @param id identifies a person
     * @return a new Person
     */
    public static Person withKey(long id) {
        Person result = new Person();
        result.Id = id;
        return result;
    }

	@Override
	public Person save() {
		return (Person) super.save();
	}
	
	@Override
	public Person find() {
		return (Person) super.find();
	}
    
    @Override
    public Person reload() {
        return (Person) super.reload();
    }
    
    public List<Person> findLike() {
        return Repository.findItems(buildQuery());
    }
    
    private QueryBuilder buildQuery() {
        return QueryBuilder.withQueryText(Resemblance).withValue("personName", getName());
    }
    
    private static final String Resemblance = "select p from Person p where p.name like :personName";

} // Person