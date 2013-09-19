package org.axiom.tools.domain;

import javax.persistence.*;
import javax.mail.internet.InternetAddress;

import org.hibernate.annotations.Index;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.axiom.tools.storage.Hashed;

@Entity
@Table(name = "EMAIL")
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
	private EmailAddress() { }


	@Column(name = "ACCOUNT", nullable = false, length = 30)
	private String account = "";

	@Index(name = "IX_EMAIL_HASH", columnNames = { "HASH_KEY" })
	@Column(name = "HOST", nullable = false, length = 30)
	private String hostName = "";
	

	/**
	 * Formats this email address.
	 * @return a formatted email address
	 */
	public String formatAddress() {
		return this.account + AT + this.hostName;
	}
	
	@Override
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
