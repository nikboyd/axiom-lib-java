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

import org.hibernate.annotations.Index;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.axiom.tools.storage.Hashed;

/**
 * Contains a street address.
 */
@Entity
@Table(name = "ADDRESS")
@SuppressWarnings("unchecked")
public class StreetAddress extends Hashed<StreetAddress> {

	private static final Log Logger = LogFactory.getLog(StreetAddress.class);
	private static final String Blank = " ";
	private static final String Comma = ", ";
	
	/**
	 * A logger for this class.
	 */
	@Override
	public Log getLogger() {
		return StreetAddress.Logger;
	}

	/**
	 * Counts all the addresses saved in storage.
	 * @return a count of all saved addresses
	 */
	public static int count() {
		return Repository.count(StreetAddress.class);
	}
	
	public int countReferences() {
		return 0;
	}
	
	
	@Column(name = "STREET", nullable = true, length = 50)
	protected String street;
	
	@Column(name = "OFFICE", nullable = true, length = 50)
	protected String office;
	
	@Column(name = "CITY", nullable = false, length = 50)
	protected String city;
	
	@Column(name = "STATE_CODE", nullable = false, length = 2)
	protected String stateCode;
	
	@Index(name = "IX_ADDRESS_HASH", columnNames = { "HASH_KEY" })
	@Column(name = "POSTAL_CODE", nullable = false, length = 10)
	protected String postalCode;

	/**
	 * Builds a new StreetAddress.
	 * @param street a street number and name
	 * @param city a city name
	 * @param stateCode a state code
	 * @param postalCode a postal code
	 * @return a new StreetAddress
	 */
	public static StreetAddress with(String street, String city, String stateCode, String postalCode) {
		return with(street, "", city, stateCode, postalCode);
	}
	
	/**
	 * Builds a new StreetAddress.
	 * @param street a street number and name
	 * @param unit a building unit
	 * @param city a city name
	 * @param stateCode a state code
	 * @param postalCode a postal code
	 * @return a new StreetAddress
	 */
	public static StreetAddress with(String street, String unit, String city, String stateCode, String postalCode) {
		return new StreetAddress()
					.withStreet(street)
					.withOffice(unit)
					.withCity(city)
					.withStateCode(stateCode)
					.withPostalCode(postalCode);
	}
	
	/**
	 * Constructs a new StreetAddress.
	 */
	protected StreetAddress() {
		this.street = "";
		this.office = "";
		this.city = "";
		this.stateCode = "";
		this.postalCode = "";
	}
	
	@Override
	public int hashCode() {
		String hashSource = getStreet() + getOffice() + getCity() + getPostalCode();
		return hashSource.hashCode();
	}

	public String getStreet() {
		return this.street;
	}

	protected void setStreet(String street) {
		this.street = street;
		reset();
	}

	public StreetAddress withStreet(String street) {
		setStreet(street);
		return this;
	}

	public String getOffice() {
		return this.office;
	}

	protected void setOffice(String office) {
		this.office = office;
		reset();
	}

	public StreetAddress withOffice(String office) {
		setOffice(office);
		return this;
	}

	public String getCity() {
		return this.city;
	}

	protected void setCity(String city) {
		this.city = city;
		reset();
	}

	public StreetAddress withCity(String city) {
		setCity(city);
		return this;
	}

	public String getStateCode() {
		return this.stateCode;
	}

	protected void setStateCode(String stateCode) {
		this.stateCode = stateCode;
		reset();
	}

	public StreetAddress withStateCode(String stateCode) {
		setStateCode(stateCode);
		return this;
	}

	public String getPostalCode() {
		return this.postalCode;
	}

	protected void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
		reset();
	}

	public StreetAddress withPostalCode(String postalCode) {
		setPostalCode(postalCode);
		return this;
	}
	public String formatAddress() {
		return getStreet() + Blank + getOffice() + Blank + 
				getCity() + Comma + getStateCode() + Blank + getPostalCode();
	}

	@Override
	public void describe() {
		describe(Contact.Type.HOME);
	}
	
	public void describe(Contact.Type type) {
		getLogger().info("key = " + getKey() + Blank + type.kind + Blank + formatAddress());
	}

} // StreetAddress
