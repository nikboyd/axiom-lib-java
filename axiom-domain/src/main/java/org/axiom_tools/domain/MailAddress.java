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

import java.io.Serializable;
import javax.persistence.*;
import javax.xml.bind.annotation.*;
import javax.validation.constraints.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.axiom_tools.storage.Hashed;
import org.axiom_tools.validations.ModelValidator;

/**
 * Contains a (unique) mailing address.
 */
@Entity
@Table(name = "mail_address", indexes = {
    @Index(name = "ix_address_hash", columnList = "hash_key") })
@XmlRootElement(name = "MailAddress", namespace = "##default")
@SuppressWarnings("unchecked")
public class MailAddress extends Hashed<MailAddress> implements Serializable {

	private static final long serialVersionUID = 1001001L;
	private static final Logger Log = LoggerFactory.getLogger(MailAddress.class);

	// allow forward slash and pound sign in addresses and units
	private static final String StreetAddressValidationPattern = "((\\d+\\s)[\\w\\s/#]+){0,1}"; // must be number(s) + name(s)
	private static final String BuildingUnitValidationPattern = "[\\w\\s/#]*"; // must be some word(s) and number(s)
	private static final String CityNameValidationPattern = "[a-zA-Z\\s]+"; // must be some word(s)
	private static final String StateCodeValidationPattern = "[A-Z]{2}"; // must be a code with 2 upper case letters
	private static final String PostalCodeValidationPattern = "[\\w\\s]+"; // must be a code with some number(s) and/or word(s)
	//private static final String CountryCodeValidationPattern = "[A-Z]{3}"; // must be a code with 3 upper case letters
	private static final String FailedMatchMessage = "failed.match";
    private static final MailAddress SampleAddress = new MailAddress();

	/**
	 * A logger for this class.
     * @return a Logger
	 */
	@Override
	protected Logger getLogger() {
		return Log;
	}

	/**
	 * Counts all the addresses saved in storage.
	 * @return a count of all saved addresses
	 */
	public static int count() {
        return (int) SampleAddress.getStore().count();
	}

	public int countReferences() {
		return 0;
	}


    @Column(name = "street", nullable = true, length = 50)
	@Size(min = 0, max = 50, message = FailedMatchMessage)
	@Pattern(regexp = StreetAddressValidationPattern, message = FailedMatchMessage)
	protected String street;

    @Column(name = "office", nullable = true, length = 50)
	@Size(min = 0, max = 50, message = FailedMatchMessage)
	@Pattern(regexp = BuildingUnitValidationPattern, message = FailedMatchMessage)
	protected String office;

    @Column(name = "city", nullable = false, length = 50)
	@Size(min = 5, max = 50, message = FailedMatchMessage)
	@Pattern(regexp = CityNameValidationPattern, message = FailedMatchMessage)
	protected String city;

    @Column(name = "state_code", nullable = false, length = 2)
	@Pattern(regexp = StateCodeValidationPattern, message = FailedMatchMessage)
	protected String stateCode;

    @Column(name = "postal_code", nullable = false, length = 15)
	@Size(min = 5, max = 15, message = FailedMatchMessage)
	@Pattern(regexp = PostalCodeValidationPattern, message = FailedMatchMessage)
	protected String postalCode;

	/**
	 * Builds a new StreetAddress.
	 * @param street a street number and name
	 * @param city a city name
	 * @param stateCode a state code
	 * @param postalCode a postal code
	 * @return a new StreetAddress
	 */
	public static MailAddress with(String street, String city, String stateCode, String postalCode) {
		return with(street, Empty, city, stateCode, postalCode);
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
	public static MailAddress with(String street, String unit, String city, String stateCode, String postalCode) {
		return new MailAddress()
					.withStreet(street)
					.withOffice(unit)
					.withCity(city)
					.withStateCode(stateCode)
					.withPostalCode(postalCode);
	}

	/**
	 * Constructs a new StreetAddress.
	 */
	protected MailAddress() {
        super();
		this.street = Empty;
		this.office = Empty;
		this.city = Empty;
		this.stateCode = Empty;
		this.postalCode = Empty;
	}

	@Override
	public int hashCode() {
		String hashSource = getStreet() + getOffice() + getCity() + getPostalCode();
		return hashSource.hashCode();
	}

    @Override
    public boolean equals(Object candidate) {
        if (candidate == null) return false;
        if (getClass() != candidate.getClass()) return false;
        final MailAddress other = (MailAddress) candidate;
        return other.formatAddress().equals(formatAddress());
    }

	/**
	 * Validates this address.
	 * @return any problems detected
	 */
	public String[] validate() {
		return ModelValidator.getConfiguredValidator().validate(this);
	}

	/**
	 * A street number, name, and type.
     * @return a street number, name, and type
	 */
	@XmlAttribute(name = "street")
	public String getStreet() {
		return this.street;
	}

	/**
	 * A street address.
	 * @param street a street number, name, and type
	 */
	protected void setStreet(String street) {
		this.street = normalizeWords(street);
		reset();
	}

	/**
	 * Sets the street of this address.
	 * @param street a street number, name, and type
	 * @return this MailAddress
	 */
	public MailAddress withStreet(String street) {
		setStreet(street);
		return this;
	}

	/**
	 * A building unit (office).
	 * @return a building unit
	 */
	@XmlAttribute(name = "office")
	public String getOffice() {
		return this.office;
	}

	/**
	 * A building unit (office).
	 * @param office a building unit
	 */
	protected void setOffice(String office) {
		this.office = normalizeWords(office);
		reset();
	}

	/**
	 * Sets the building unit of this address.
	 * @param office a building unit
	 * @return this MailAddress
	 */
	public MailAddress withOffice(String office) {
		setOffice(office);
		return this;
	}

	/**
	 * A city name.
	 * @return a city name
	 */
	@XmlAttribute(name = "city")
	public String getCity() {
		return this.city;
	}

	/**
	 * A city name.
	 * @param city a city name
	 */
	protected void setCity(String city) {
		this.city = normalizeWords(city);
		reset();
	}

	/**
	 * Sets the city name of this address.
	 * @param city a city name
	 * @return this MailAddress
	 */
	public MailAddress withCity(String city) {
		setCity(city);
		return this;
	}

	/**
	 * A state code.
	 * @return a state code
	 */
	@XmlAttribute(name = "state")
	public String getStateCode() {
		return this.stateCode;
	}

	/**
	 * A state code.
	 * @param stateCode a state code
	 */
	protected void setStateCode(String stateCode) {
		this.stateCode = normalizeCode(stateCode);
		reset();
	}

	/**
	 * Sets the state code of this address.
	 * @param stateCode a state code
	 * @return this MailAddress
	 */
	public MailAddress withStateCode(String stateCode) {
		setStateCode(stateCode);
		return this;
	}

	/**
	 * A postal code.
	 * @return a postal code
	 */
	@XmlAttribute(name = "zip")
	public String getPostalCode() {
		return this.postalCode;
	}

	/**
	 * A postal code.
	 * @param postalCode a postal code
	 */
	protected void setPostalCode(String postalCode) {
		this.postalCode = normalizeCode(postalCode);
		reset();
	}

	/**
	 * Sets the postal code of this address.
	 * @param postalCode a postal code
	 * @return this MailAddress
	 */
	public MailAddress withPostalCode(String postalCode) {
		setPostalCode(postalCode);
		return this;
	}

	/**
	 * Formats the full description of this address.
	 * @return a single line description of this address
	 */
	public String formatAddress() {
		StringBuilder builder = new StringBuilder();
		builder.append(getStreet());

		if (!getOffice().isEmpty()) {
			builder.append(Comma + Blank);
			builder.append(getOffice());
		}

		builder.append(Comma + Blank);
		builder.append(getCity());
		builder.append(Comma + Blank);
		builder.append(getStateCode());
		builder.append(Blank);
		builder.append(getPostalCode());

		return builder.toString();
	}

	/**
	 * A description of this address.
	 */
	@Override
	public void describe() {
		describe(Contact.Kind.HOME);
	}

	/**
	 * Logs a description of this address.
	 * @param type a contact type
	 */
	public void describe(Contact.Kind type) {
		getLogger().info("key = " + getKey() + Blank + type.name() + Blank + formatAddress());
	}

} // StreetAddress
