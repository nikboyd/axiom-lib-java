/**
 * Copyright 2015 Nikolas Boyd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axiom_tools.crypto;

import java.util.Arrays;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Contains a cryptographically secured payload.
 *
 * <h4>SecurityToken Responsibilities:</h4>
 * <ul>
 * <li>knows a token creation timestamp</li>
 * <li>knows some long value(s), often a single value</li>
 * <li>decrypts the contents of a token from hex</li>
 * <li>encrypts the contents of a token to hex</li>
 * <li>packages the contents of a token for usage</li>
 * <li>unpacks the contents of a token from a package</li>
 * </ul>
 *
 * <h4>Client Responsibilities:</h4>
 * <ul>
 * <li>properly configure the cryptography used</li>
 * <li>properly configure a map of token names</li>
 * <li>supply the values or hex content of a token during construction</li>
 * </ul>
 */
public class SecurityToken {

	private static final Logger Log = Logger.getLogger(SecurityToken.class);

	/** Formats token time stamps. */
	public static final DateTimeFormatter TokenTimestampFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

	private static final String Equals = "=";
	private static final String Zeros = "0000000000000000";
	private static final int LongNibbles = Zeros.length();
	private static final int HexBase = 16;

	private static final long Milliseconds = 1000;
	private static final long StandardValidity = 5 * 60 * Milliseconds; // 5 mins (in msecs)
	private static final int TimestampIndex = 0;
	private static final int ValidityIndex = 1;
	private static final int StandardValues = 2;

	private long[] values = { DateTime.now().getMillis(), StandardValidity, 0 };
	private String tokenName = "";

	/**
	 * Returns a token derived from a token package.
	 * @param tokenPackage contains a token name and encrypted token content
	 * @return a SecurityToken
	 */
	public static SecurityToken fromPackage(String tokenPackage) {
		String[] parts = tokenPackage.split(Equals);
		SecurityToken result = SecurityToken.named(parts[0]);
		return result.withValues(parts[1]);
	}

	/**
	 * Returns a named token.
	 * @param tokenName a token name
	 * @return a SecurityToken
	 */
	public static SecurityToken named(String tokenName) {
		SecurityToken result = new SecurityToken();
		result.tokenName = tokenName;
		result.checkCryptographer();
		return result.with(0);
	}

	/**
	 * Decrypts the values for this token.
	 * @param cryptText contains the token values
	 * @return this SecurityToken
	 */
	public SecurityToken withValues(String cryptText) {
		String hexBuffer = getCryptographer().decryptFromHex(cryptText);
		int count = hexBuffer.length() / LongNibbles;
		this.values = new long[count];
		for (int index = 0; index < count; index++) {
			int pos = index * LongNibbles;
			int end = pos + LongNibbles;
			String valueHex = hexBuffer.substring(pos, end);
			this.values[index] = Long.parseLong(valueHex, HexBase);
		}
		return this;
	}

	/**
	 * Indicates whether this token is still valid.
	 * @return whether this token is still valid (or has expired)
	 */
	public boolean isValid() {
		if (getValidity() == 0) return true;
		return getExpirationTime().isAfterNow();
	}

	/**
	 * Indicates whether this token has exceeded its configured validity period.
	 * @return whether this token has expired
	 */
	public boolean isExpired() {
		return !this.isValid();
	}

	/**
	 * Returns the expiration time.
	 * @return a DateTime
	 */
	public DateTime getExpirationTime() {
		return getTimestamp().plus(this.values[ValidityIndex]);
	}

	/**
	 * Returns the validity duration of this token.
	 * @return a validity duration (seconds)
	 */
	public long getValidity() {
		return this.values[ValidityIndex] / Milliseconds;
	}

	/**
	 * Sets the token validity duration (seconds).
	 * @param seconds indicates the valid duration of this token
	 * @return this SecurityToken
	 */
	public SecurityToken withValidity(long seconds) {
		if (seconds >= 0) {
			this.values[ValidityIndex] = seconds * Milliseconds;
		}

		return this;
	}

	/**
	 * Adds a value to this token.
	 * @param value a value
	 * @return this SecurityToken
	 */
	public SecurityToken with(long value) {
		this.values[StandardValues] = value;
		return this;
	}

	/**
	 * Includes some values in this token.
	 * @param values some values
	 * @return this SecurityToken
	 */
	public SecurityToken with(long[] values) {
		long[] oldValues = this.values;
		this.values = new long[values.length + StandardValues];
		this.values[TimestampIndex] = oldValues[TimestampIndex];
		this.values[ValidityIndex] = oldValues[ValidityIndex];
		System.arraycopy(values, 0, this.values, StandardValues, values.length);
		return this;
	}

	/**
	 * Encrypts the content of this token.
	 * @return encrypted token content
	 */
	public byte[] toBytes() {
		StringBuffer buffer = new StringBuffer();
		for (int index = 0; index < this.values.length; index++) {
			String hex = Long.toHexString(values[index]);
			int padWidth = Zeros.length() - hex.length();
			buffer.append(Zeros.substring(0, padWidth) + hex);
		}
		String hexBuffer = buffer.toString();
		return getCryptographer().encrypt(hexBuffer);
	}

	/**
	 * Encrypts the content of this token.
	 * @return encrypted token content encoded as hex
	 */
	public String toHex() {
		return Hex.encodeHexString(this.toBytes());
	}

	/**
	 * A package containing the encrypted content of this token.
	 * @return an encoded token package
	 */
	public String packaged() {
		return this.tokenName + Equals + this.toHex();
	}

	/**
	 * The name of this token.
	 * @return a name
	 */
	public String getName() {
		return this.tokenName;
	}

	/**
	 * Returns the creation time of this token.
	 * @return a DateTime
	 */
	public DateTime getTimestamp() {
		return new DateTime(this.values[TimestampIndex]);
	}

	/**
	 * Returns a value.
	 * @return a value
	 */
	public long getValue() {
		return this.values[StandardValues];
	}

	/**
	 * Returns an indicated value (if present).
	 * @param index a value index
	 * @return a value, or zero (0)
	 */
	public long getValue(int index) {
		int actualIndex = index + StandardValues;
		if (actualIndex < 0) return 0;
		if (actualIndex > this.values.length) return 0;
		return this.values[actualIndex];
	}

	/**
	 * Dumps a description of this token to the log.
	 */
	public void dumpToLog() {
		Log.info(this.toString());
	}

	@Override
	public String toString() {
		return getName() + formatExpirationTime() + formatValues();
	}

	private String formatExpirationTime() {
		if (getValidity() == 0) return " token good forever";

		if (this.isValid())
			return " token good until " + getExpirationTime().toString(TokenTimestampFormat);
		else
			return " token expired at " + getExpirationTime().toString(TokenTimestampFormat);
	}

	private String formatValues() {
		long[] values = Arrays.copyOfRange(this.values, StandardValues, this.values.length);
		return " " + Arrays.toString(values);
	}

	private void checkCryptographer() {
		if (getCryptographer() == null)
			throw new IllegalArgumentException(MissingSymmetry + tokenName);
	}

	private Symmetric getCryptographer() {
		return Symmetric.getCryptographer(getName());
	}

	private static final String MissingSymmetry = "No Symmetric cryptographer was configured to handle ";

} // SecurityToken