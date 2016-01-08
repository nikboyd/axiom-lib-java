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
package org.axiom_tools.domain;

import java.util.*;
import javax.persistence.*;
import javax.xml.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.axiom_tools.codecs.ModelCodec;
import org.axiom_tools.storage.PersonStorage;
import org.axiom_tools.storage.StorageMechanism;

/**
 * Identifies and describes a person.
 */
@Entity
@Table(name = "person", indexes = {
    @Index(name = "ix_party_hash", columnList = "hash_key")})
@XmlRootElement(name = "Person", namespace = "##default")
@SuppressWarnings("unchecked")
public class Person extends Party {

    private static final long serialVersionUID = 1001001L;
    private static final Person SamplePerson = new Person();

    public static List<Person> listFromJSON(String listJSON) {
        List<Person> sampleList = new ArrayList();
        return ModelCodec.to(sampleList.getClass()).fromJSON(listJSON);
    }

    public static Person fromJSON(String personJSON) {
        return ModelCodec.to(Person.class).fromJSON(personJSON);
    }

    public String toJSON() {
        return ModelCodec.from(this).toJSON();
    }

    @Override
    protected Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
    }

    public static int count() {
        return (int) SamplePerson.getStore().count();
    }

    @Override
    public boolean equals(Object candidate) {
        if (candidate == null) {
            return false;
        }
        if (getClass() != candidate.getClass()) {
            return false;
        }
        return hashCode() == candidate.hashCode();
    }

    @Override
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
     *
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
     *
     * @param text query text
     * @return a list of similar Persons
     */
    public static List<Person> like(String text) {
        Person result = new Person();
        result.name = text;
        return result.findSimilar();
    }

    /**
     * Returns a new Person (intended query).
     *
     * @param key identifies a person
     * @return a new Person
     */
    public static Person withKey(long key) {
        Person result = new Person();
        result.key = key;
        return result;
    }

    @Override
    public Person findItem() {
        return (Person) super.findItem();
    }

    @Override
    public Person saveItem() {
        return (Person) super.saveItem();
    }

    @Override
    public Person findWithHash() {
        return (Person) super.findWithHash();
    }

    public List<Person> findSimilar() {
        return StorageMechanism.getStorage(PersonStorage.class).findLike(getName());
    }

    public static List<Person> findSimilar(EmailAddress email) {
        return StorageMechanism.getStorage(PersonStorage.class).findEmail(email.hashKey());
    }

    public static List<Person> findSimilar(PhoneNumber phone) {
        return StorageMechanism.getStorage(PersonStorage.class).findPhone(phone.hashKey());
    }

} // Person
