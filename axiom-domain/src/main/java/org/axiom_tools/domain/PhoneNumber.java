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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.annotations.Index;

import org.axiom_tools.storage.Hashed;

/**
 * Contains a phone number.
 */
@Entity
@Table(name = "PHONE")
@XmlRootElement(name = "PhoneNumber", namespace = "##default")
@SuppressWarnings("unchecked")
public class PhoneNumber extends Hashed<PhoneNumber> implements Serializable {

	private static final long serialVersionUID = 1001001L;
	private static final Logger Log = LoggerFactory.getLogger(PhoneNumber.class);

	@Override
	protected Logger getLogger() {
		return Log;
	}

	/**
	 * Counts all the addresses saved in storage.
	 * @return a count of all saved addresses
	 */
	public static int count() {
		return Repository.count(PhoneNumber.class);
	}

	private static final String DASH = "-";
	private static final String FORMAT = "999-999-9999";
	private static final String PATTERN = "(\\d{3})-(\\d{3})-(\\d{4})";

	/**
	 * Returns a new PhoneNumber.
	 * @param phoneNumber a formatted phone number
	 * @return a new PhoneNumber
	 * @exception NumberFormatException if the supplied phone number cannot be parsed
	 */
	public static PhoneNumber from(String phoneNumber) {
		if (phoneNumber == null || !phoneNumber.matches(PATTERN)) {
			throw new NumberFormatException("PhoneNumbers must have a format like " + FORMAT);
		}

		PhoneNumber result = new PhoneNumber();
		result.setFormattedNumber(phoneNumber);
		return result;
	}

	/**
	 * Constructs a new PhoneNumber.
	 */
    protected PhoneNumber() {
        super();
    }


	@Column(name = "AREA", nullable = false, length = 3)
	private String areaCode = "";

	@Column(name = "PREFIX", nullable = false, length = 3)
	private String prefix = "";

	@Column(name = "SUFFIX", nullable = false, length = 4)
	private String suffix = "";

	/**
	 * A formatted phone number.
	 */
	@XmlAttribute(name = "value")
	public String getFormattedNumber() {
		return formatNumber();
	}

	/**
	 * A formatted phone number.
	 */
	protected void setFormattedNumber(String phoneNumber) {
		String[] parts = phoneNumber.split(DASH);
		this.areaCode = parts[0];
		this.prefix   = parts[1];
		this.suffix   = parts[2];
	}

	/**
	 * Formats this phone number.
	 * @return a formatted phone number
	 */
	public String formatNumber() {
		return this.areaCode + DASH + this.prefix + DASH + this.suffix;
	}

	@Override
	@Index(name = "IX_PHONE_HASH", columnNames = { "HASH_KEY" })
	public int hashCode() {
		String hashSource = formatNumber();
		return hashSource.hashCode();
	}

	@Override
	public void describe() {
		describe(Contact.Kind.HOME);
	}

	public void describe(Contact.Kind type) {
		getLogger().info("key = " + getKey() + " " + type.name() + " " + formatNumber());
	}

} // PhoneNumber
