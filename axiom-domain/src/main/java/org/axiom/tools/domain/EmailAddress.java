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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.mail.internet.InternetAddress;

import org.hibernate.annotations.Index;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.axiom.tools.storage.Hashed;

/**
 * Contains an email address.
 */
@Entity
@Table(name = "EMAIL_ADDRESS")
@XmlRootElement(name = "EmailAddress", namespace = "##default")
@SuppressWarnings("unchecked")
public class EmailAddress extends Hashed<EmailAddress> {

	private static final Log Logger = LogFactory.getLog(EmailAddress.class);
	
	@Override
	public Log getLogger() {
		return EmailAddress.Logger;
	}

	/**
	 * Counts all the email addresses saved in storage.
	 * @return a count of all saved email addresses
	 */
	public static int count() {
		return Repository.count(EmailAddress.class);
	}
	
	private static final String AT = "@";
	
	/**
	 * Returns a new EmailAddress.
	 * @param emailAddress a formatted email address
	 * @return a new EmailAddress
	 */
	@SuppressWarnings("unused")
	public static EmailAddress from(String emailAddress) {
		try {
			InternetAddress test = new InternetAddress(emailAddress);
		} catch (Exception e) {
			throw new IllegalArgumentException("bad email address " + emailAddress, e);
		}

		EmailAddress result = new EmailAddress();
		String[] parts = emailAddress.split(AT);
		result.account  = parts[0];
		result.hostName = parts[1];
		return result;
	}
	
	/**
	 * Constructs a new EmailAddress.
	 */
	protected EmailAddress() { }


	@Column(name = "ACCOUNT", nullable = false, length = 30)
	private String account = Empty;

	@Column(name = "HOST", nullable = false, length = 30)
	private String hostName = Empty;


	/**
	 * A formatted email address.
	 */
	@XmlAttribute(name = "value")
	public String getFormattedAddress() {
		return formatAddress();
	}
	
	/**
	 * A formatted email address.
	 */
	protected void setFormattedAddress(String emailAddress) {
		String[] parts = emailAddress.split(AT);
		this.account  = parts[0];
		this.hostName = parts[1];
	}

	/**
	 * Formats this email address.
	 * @return a formatted email address
	 */
	public String formatAddress() {
		return this.account + AT + this.hostName;
	}
	
	@Override
	@Index(name = "IX_EMAIL_HASH", columnNames = { "HASH_KEY" })
	public int hashCode() {
		String hashSource = formatAddress();
		return hashSource.hashCode();
	}

	@Override
	public void describe() {
		describe(Contact.Type.HOME);
	}

	public void describe(Contact.Type type) {
		getLogger().info("key = " + getKey() + " " + type.kind + " " + formatAddress());
	}

} // EmailAddress
