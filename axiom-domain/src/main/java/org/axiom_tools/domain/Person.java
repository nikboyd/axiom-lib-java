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

import java.util.*;
import javax.persistence.*;
import javax.xml.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.annotations.Index;

import org.axiom_tools.codecs.EntityCodec;
import org.axiom_tools.storage.QueryBuilder;

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
	private static final Logger Log = LoggerFactory.getLogger(Person.class);
    
    public static List<Person> listFromJSON(String listJSON) {
        List<Person> sampleList = new ArrayList();
        return EntityCodec.to(sampleList.getClass()).fromJSON(listJSON);
    }
    
    public static Person fromJSON(String personJSON) {
        return EntityCodec.to(Person.class).fromJSON(personJSON);
    }
    
    public String toJSON() {
        return EntityCodec.from(this).toJSON();
    }

	@Override
	protected Logger getLogger() {
		return Log;
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
     * @param key identifies a person
     * @return a new Person
     */
    public static Person withKey(long key) {
        Person result = new Person();
        result.key = key;
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