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
import java.io.Serializable;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.axiom_tools.storage.Surrogated;
import org.axiom_tools.storage.SurrogatedComposite;

/**
 * Contains contact information.
 */
@Entity
@Table(name = "CONTACT")
@XmlRootElement(name = "Contact", namespace = "##default")
@SuppressWarnings("unchecked")
public class Contact extends Surrogated<Contact> implements SurrogatedComposite, Serializable {

	private static final long serialVersionUID = 1001001L;
	private static final Log Logger = LogFactory.getLog(Contact.class);
    
    /**
     * Indicates a kind of contact.
     */
    public static enum Kind {
        
        HOME,
        WORK,
        MOBILE,
        BILLING,
        SHIPPING,
        
    } // Kind


	// component map indices for this composite
	private static final int AddressIndex = 0;
	private static final int EmailIndex = 1;
	private static final int PhoneIndex = 2;

	/**
	 * A logger.
	 */
	@Override
	protected Log getLogger() {
		return Logger;
	}
    
    public List<ContactMechanism> getMechanisms() {
        ArrayList<ContactMechanism> results = new ArrayList();
		for (Kind addressType : addresses.keySet()) {
            results.add(ContactMechanism.with(addressType, addresses.get(addressType)));
		}
		
		for (Kind phoneType : phones.keySet()) {
            results.add(ContactMechanism.with(phoneType, phones.get(phoneType)));
		}
		
		for (Kind emailType : emails.keySet()) {
            results.add(ContactMechanism.with(emailType, emails.get(emailType)));
		}
        return results;
    }
    
    public void setMechanisms(List<ContactMechanism> mechanisms) {
        for (ContactMechanism mechanism : mechanisms) {
            if (mechanism.getMechanism() instanceof MailAddress) {
                updateAddress(Kind.valueOf(mechanism.getType()), (MailAddress)mechanism.getMechanism());
            }
            if (mechanism.getMechanism() instanceof EmailAddress) {
                updateEmail(Kind.valueOf(mechanism.getType()), (EmailAddress)mechanism.getMechanism());
            }
            if (mechanism.getMechanism() instanceof PhoneNumber) {
                updatePhone(Kind.valueOf(mechanism.getType()), (PhoneNumber)mechanism.getMechanism());
            }
        }
    }
	
	/**
	 * Any components that are managed as maps.
	 * @return the component maps associated with this Contact
	 */
	@Override
	public Object[] componentMaps() {
		Object[] results = { this.addresses, this.emails, this.phones };
		return results;
	}
	
	/**
	 * Any street addresses associated with this contact.
	 */
	@ManyToMany(
		fetch = FetchType.EAGER,
		cascade = CascadeType.ALL)
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "kind", length = 10, nullable = false)
	private Map<Kind, MailAddress> 
		addresses = new HashMap<Kind, MailAddress>();
	
	/**
	 * Any phone numbers associated with this contact.
	 */
	@OneToMany(
		fetch = FetchType.EAGER,
		cascade = CascadeType.ALL, 
		orphanRemoval = true)
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "kind", length = 10, nullable = false)
	private Map<Kind, PhoneNumber> 
		phones = new HashMap<Kind, PhoneNumber>();

	/**
	 * Any email addresses associated with this contact.
	 */
	@OneToMany(
		fetch = FetchType.EAGER,
		cascade = CascadeType.ALL, 
		orphanRemoval = true)
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "kind", length = 10, nullable = false)
	private Map<Kind, EmailAddress> 
		emails = new HashMap<Kind, EmailAddress>();
	
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
	public MailAddress getAddress(Kind kind) {
		return Repository.getMapElement(this, AddressIndex, kind);
	}
	
	/**
	 * Adds an address of a given kind.
	 * @param kind a kind of address
	 * @param address a StreetAddress
	 * @return this contact
	 */
	public Contact withAddress(Kind kind, MailAddress address) {
		if (address == null) return this;
		updateAddress(kind, address);
		return this;
	}
	
	/**
	 * Removes an address of a given kind.
	 * @param kind a kind of address
	 * @return this contact
	 */
	public Contact removeAddress(final Kind kind) {
		updateAddress(kind, null);
		return this;
	}
	
	/**
	 * Returns a phone of a given kind.
	 * @param kind a kind of phone
	 * @return a PhoneNumber, or null
	 */
	public PhoneNumber getPhone(Kind kind) {
		return Repository.getMapElement(this, PhoneIndex, kind);
	}
	
	/**
	 * Adds a phone to this contact.
	 * @param kind a kind of phone
	 * @param phone a phone
	 * @return this contact
	 */
	public Contact withPhone(Kind kind, PhoneNumber phone) {
		if (phone == null) return this;
		updatePhone(kind, phone);
		return this;
	}
	
	/**
	 * Removes a phone from this contact.
	 * @param kind a kind of phone
	 * @return this contact
	 */
	public Contact removePhone(Kind kind) {
		updatePhone(kind, null);
		return this;
	}
	
	/**
	 * Returns an email address of a given kind.
	 * @param kind a kind of email address
	 * @return an EmailAddress, or null
	 */
	public EmailAddress getEmail(Kind kind) {
		return Repository.getMapElement(this, EmailIndex, kind);
	}
	
	/**
	 * Adds an email address of a given kind.
	 * @param kind a kind of email address
	 * @param email an email address
	 * @return this contact
	 */
	public Contact withEmail(Kind kind, EmailAddress email) {
		if (email == null) return this;
		updateEmail(kind, email);
		return this;
	}
	
	/**
	 * Removes an email address of a given kind.
	 * @param kind a kind of email address
	 * @return this contact
	 */
	public Contact removeEmail(final Kind kind) {
		updateEmail(kind, null);
		return this;
	}

	/**
	 * Describes this contact and its components.
	 */
	@Override
	public void describe() {
		super.describe();
		for (Kind addressType : addresses.keySet()) {
			addresses.get(addressType).describe(addressType);
		}
		
		for (Kind phoneType : phones.keySet()) {
			phones.get(phoneType).describe(phoneType);
		}
		
		for (Kind emailType : emails.keySet()) {
			emails.get(emailType).describe(emailType);
		}
	}
	
	private void updateAddress(Kind kind, MailAddress address) {
		if (address == null) {
			this.addresses.remove(kind);
		}
		else {
			this.addresses.put(kind, address);
		}
	}
	
	private void updatePhone(Kind kind, PhoneNumber phoneNumber) {
		if (phoneNumber == null) {
			this.phones.remove(kind);
		}
		else {
			this.phones.put(kind, phoneNumber);
		}
	}
	
	private void updateEmail(Kind kind, EmailAddress emailAddress) {
		if (emailAddress == null) {
			this.emails.remove(kind);
		}
		else {
			this.emails.put(kind, emailAddress);
		}
	}

} // Contact
