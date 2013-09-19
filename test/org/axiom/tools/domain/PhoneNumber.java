package org.axiom.tools.domain;

import javax.persistence.*;

import org.hibernate.annotations.Index;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.axiom.tools.storage.Hashed;

@Entity
@Table(name = "PHONE")
@SuppressWarnings("unchecked")
public class PhoneNumber extends Hashed<PhoneNumber> {

	private static final Log Logger = LogFactory.getLog(PhoneNumber.class);

	@Override
	public Log getLogger() {
		return PhoneNumber.Logger;
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
		String[] parts = phoneNumber.split(DASH);
		result.areaCode = parts[0];
		result.prefix   = parts[1];
		result.suffix   = parts[2];		
		return result;
	}
	
	/**
	 * Constructs a new PhoneNumber.
	 */
	private PhoneNumber() { }


	@Column(name = "AREA", nullable = false, length = 3)
	private String areaCode = "";

	@Column(name = "PREFIX", nullable = false, length = 3)
	private String prefix = "";

	@Index(name = "IX_PHONE_HASH", columnNames = { "HASH_KEY" })
	@Column(name = "SUFFIX", nullable = false, length = 4)
	private String suffix = "";
	

	/**
	 * Formats this phone number.
	 * @return a formatted phone number
	 */
	public String formatNumber() {
		return this.areaCode + DASH + this.prefix + DASH + this.suffix;
	}
	
	@Override
	public int hashCode() {
		String hashSource = formatNumber();
		return hashSource.hashCode();
	}

	@Override
	public void describe() {
		describe(Contact.Type.HOME);
	}
	
	public void describe(Contact.Type type) {
		getLogger().info("key = " + getKey() + " " + type.kind + " " + formatNumber());
	}
	
} // PhoneNumber
