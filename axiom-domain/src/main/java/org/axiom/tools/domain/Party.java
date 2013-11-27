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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;

import org.axiom.tools.storage.Hashed;
import org.axiom.tools.storage.SurrogatedComposite;
import org.axiom.tools.storage.SurrogatedItem;

/**
 * Identifies a party along with some contact information.
 * 
 * <h4>Party Responsibilities:</h4>
 * <ul>
 * <li>knows a party name (personal or business)</li>
 * <li>knows a party contact</li>
 * </ul>
 */
@MappedSuperclass
@SuppressWarnings("unchecked")
public abstract class Party extends Hashed<Party> implements SurrogatedComposite {

	@XmlAttribute(name = "name")
	@Column(name = "NAME", nullable = false, length = 75)
	@Size(min = 5, max = 75, message = "personal name too short or long")
	protected String name;

	@ManyToOne(
		fetch = FetchType.EAGER,
		cascade = CascadeType.ALL, 
		optional = true)
	@XmlElement(name = "Contact")
	protected Contact contact = new Contact();

	/**
	 * A name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * A name.
	 */
	protected void setName(String name) {
		this.name = normalizeWords(name);
	}

	/**
	 * A contact.
	 */
	public Contact getContact() {
		return this.contact;
	}

	/**
	 * A contact.
	 */
	protected void setContact(Contact contact) {
		this.contact = contact;
	}

	/**
	 * Adds a mail address to this party.
	 * @param kind a kind of mail address
	 * @param address a mail address
	 * @return this party
	 */
	public <PartyType extends Party> PartyType with(Contact.Type kind, MailAddress address) {
		getContact().withAddress(kind, address);
		return (PartyType) this;
	}

	/**
	 * Adds an email address to this party.
	 * @param kind a kind of email address
	 * @param email an email address
	 * @return this party
	 */
	public <PartyType extends Party> PartyType with(Contact.Type kind, EmailAddress email) {
		getContact().withEmail(kind, email);
		return (PartyType) this;
	}

	/**
	 * Adds a phone number to this party.
	 * @param kind a kind of phone
	 * @param phone a phone number
	 * @return this party
	 */
	public <PartyType extends Party> PartyType with(Contact.Type kind, PhoneNumber phone) {
		getContact().withPhone(kind, phone);
		return (PartyType) this;
	}

	@Override
	@XmlTransient
	public SurrogatedItem[] getComponents() {
		SurrogatedItem[] results = { getContact() };
		return results;
	}
	
	@Override
	public void setComponents(SurrogatedItem[] results) {
		setContact((Contact)results[0]);
	}

	@Override
	public void describe() {
		getLogger().info("key = " + getKey() + Blank + " named: " + getName());
		getContact().describe();
	}


} // Party