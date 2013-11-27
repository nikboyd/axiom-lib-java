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

import java.util.Map;
import java.util.HashMap;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.axiom.tools.storage.Surrogated;
import org.axiom.tools.storage.SurrogatedComposite;

/**
 * Contains contact information.
 */
@Entity
@Table(name = "CONTACT")
@XmlRootElement(name = "Contact", namespace = "##default")
@SuppressWarnings("unchecked")
public class Contact extends Surrogated<Contact> implements SurrogatedComposite {

	private static final Log Logger = LogFactory.getLog(Contact.class);

	/**
	 * A kind of contact.
	 */
	@Embeddable
	public static class Type {
		
		/**
		 * Describes a kind of contact.
		 */
		@Basic
		public String kind;
		
		/**
		 * Constructs a new Type.
		 */
		public Type() {}
		public Type(String kind) {
			this.kind = kind;
		}
		
		public static final Type HOME = new Type("HOME");
		public static final Type WORK = new Type("WORK");
		public static final Type MOBILE = new Type("MOBILE");
		public static final Type BILLING = new Type("BILLING");
		public static final Type SHIPPING = new Type("SHIPPING");
		
		/**
		 * A hash code.
		 */
		@Override
		public int hashCode() {
			return this.kind.hashCode();
		}

		/**
		 * Indicates whether this kind equals another.
		 */
		@Override
		public boolean equals(Object candidate) {
			Type kindred = (Type) candidate;
			if (kindred == null) return false;
			return this.kind.equals(kindred.kind);
		}
		
	} // Type

	// component map indices for this composite
	private static final int AddressIndex = 0;
	private static final int EmailIndex = 1;
	private static final int PhoneIndex = 2;
	
	/**
	 * Any components that are managed as maps.
	 * @return the component maps associated with this Contact
	 */
	@Override
	@XmlTransient
	public Object[] getComponentMaps() {
		Object[] results = { this.addresses, this.emails, this.phones };
		return results;
	}
	
	/**
	 * Any street addresses associated with this contact.
	 */
	@ManyToMany(
		fetch = FetchType.EAGER,
		cascade = CascadeType.ALL)
	@MapKeyClass(Contact.Type.class)
	private Map<Contact.Type, MailAddress> 
		addresses = new HashMap<Contact.Type, MailAddress>();
	
	/**
	 * Any phone numbers associated with this contact.
	 */
	@OneToMany(
		fetch = FetchType.EAGER,
		cascade = CascadeType.ALL, 
		orphanRemoval = true)
	@MapKeyClass(Contact.Type.class)
	private Map<Contact.Type, PhoneNumber> 
		phones = new HashMap<Contact.Type, PhoneNumber>();

	/**
	 * Any email addresses associated with this contact.
	 */
	@OneToMany(
		fetch = FetchType.EAGER,
		cascade = CascadeType.ALL, 
		orphanRemoval = true)
	@MapKeyClass(Contact.Type.class)
	private Map<Contact.Type, EmailAddress> 
		emails = new HashMap<Contact.Type, EmailAddress>();
	
	/**
	 * Counts the number of saved contacts.
	 * @return a count, or zero
	 */
	public static int count() {
		return Repository.count(Contact.class);
	}

	/**
	 * A count of the mail addresses associated with this contact.
	 */
	public int countAddresses() {
		return Repository.countElements(this, AddressIndex);
	}

	/**
	 * A count of the phones associated with this contact.
	 */
	public int countPhones() {
		return Repository.countElements(this, PhoneIndex);
	}

	/**
	 * A count of the email addresses associated with this contact.
	 */
	public int countEmails() {
		return Repository.countElements(this, EmailIndex);
	}
	
	/**
	 * Returns an address of a given kind.
	 * @param kind a kind of address
	 * @return a StreetAddress, or null
	 */
	public MailAddress getAddress(Contact.Type kind) {
		return Repository.getMapElement(this, AddressIndex, kind);
	}
	
	/**
	 * Adds an address of a given kind.
	 * @param kind a kind of address
	 * @param address a StreetAddress
	 * @return this contact
	 */
	public Contact withAddress(Contact.Type kind, MailAddress address) {
		if (address == null) return this;
		updateAddress(kind, address);
		return this;
	}
	
	/**
	 * Removes an address of a given kind.
	 * @param kind a kind of address
	 * @return this contact
	 */
	public Contact removeAddress(final Contact.Type kind) {
		updateAddress(kind, null);
		return this;
	}
	
	/**
	 * Returns a phone of a given kind.
	 * @param kind a kind of phone
	 * @return a PhoneNumber, or null
	 */
	public PhoneNumber getPhone(Contact.Type kind) {
		return Repository.getMapElement(this, PhoneIndex, kind);
	}
	
	/**
	 * Adds a phone to this contact.
	 * @param kind a kind of phone
	 * @param phone a phone
	 * @return this contact
	 */
	public Contact withPhone(Contact.Type kind, PhoneNumber phone) {
		if (phone == null) return this;
		updatePhone(kind, phone);
		return this;
	}
	
	/**
	 * Removes a phone from this contact.
	 * @param kind a kind of phone
	 * @return this contact
	 */
	public Contact removePhone(Contact.Type kind) {
		updatePhone(kind, null);
		return this;
	}
	
	/**
	 * Returns an email address of a given kind.
	 * @param kind a kind of email address
	 * @return an EmailAddress, or null
	 */
	public EmailAddress getEmail(Contact.Type kind) {
		return Repository.getMapElement(this, EmailIndex, kind);
	}
	
	/**
	 * Adds an email address of a given kind.
	 * @param kind a kind of email address
	 * @param email an email address
	 * @return this contact
	 */
	public Contact withEmail(Contact.Type kind, EmailAddress email) {
		if (email == null) return this;
		updateEmail(kind, email);
		return this;
	}
	
	/**
	 * Removes an email address of a given kind.
	 * @param kind a kind of email address
	 * @return this contact
	 */
	public Contact removeEmail(final Contact.Type kind) {
		updateEmail(kind, null);
		return this;
	}

	/**
	 * Describes this contact and its components.
	 */
	@Override
	public void describe() {
		super.describe();
		for (Contact.Type addressType : addresses.keySet()) {
			addresses.get(addressType).describe(addressType);
		}
		
		for (Contact.Type phoneType : phones.keySet()) {
			phones.get(phoneType).describe(phoneType);
		}
		
		for (Contact.Type emailType : emails.keySet()) {
			emails.get(emailType).describe(emailType);
		}
	}

	/**
	 * A logger.
	 */
	@Override
	public Log getLogger() {
		return Contact.Logger;
	}
	
	private void updateAddress(Contact.Type kind, MailAddress address) {
		if (address == null) {
			this.addresses.remove(kind);
		}
		else {
			this.addresses.put(kind, address);
		}
	}
	
	private void updatePhone(Contact.Type kind, PhoneNumber phoneNumber) {
		if (phoneNumber == null) {
			this.phones.remove(kind);
		}
		else {
			this.phones.put(kind, phoneNumber);
		}
	}
	
	private void updateEmail(Contact.Type kind, EmailAddress emailAddress) {
		if (emailAddress == null) {
			this.emails.remove(kind);
		}
		else {
			this.emails.put(kind, emailAddress);
		}
	}

} // Contact
