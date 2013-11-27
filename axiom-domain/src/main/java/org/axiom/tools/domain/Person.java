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
package org.axiom.tools.domain;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

	private static final Log Logger = LogFactory.getLog(Person.class);

	@Override
	public Log getLogger() {
		return Logger;
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

	@Override
	public Person save() {
		return (Person) super.save();
	}
	
	@Override
	public Person find() {
		return (Person) super.find();
	}

} // Person