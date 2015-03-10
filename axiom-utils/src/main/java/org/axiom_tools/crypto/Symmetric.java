/**
 * Copyright 2014,2015 Nikolas Boyd.
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
package org.axiom_tools.crypto;

import java.util.HashMap;
import java.security.Key;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import org.axiom_tools.context.SpringContext;

/**
 * Symmetrically encrypts and decrypts data under AES.
 * 
 * <h4>Symmetric Responsibilities:</h4>
 * <ul>
 * <li>knows which cryptographer supports a given kind of usage</li>
 * <li>knows an AES initialization vector</li>
 * <li>knows an AES key</li>
 * <li>encrypts clear text data under the configured IV and key</li>
 * <li>decrypts cypher data under the same IV and key</li>
 * </ul>
 *
 * <h4>Client Responsibilities:</h4>
 * <ul>
 * <li>properly configure an instance of this class with IV and key</li>
 * </ul>
 */
public class Symmetric {

	private static final Logger Log = LoggerFactory.getLogger(Symmetric.class);
	private static final String ConfigurationFile = "cryptographers.xml";
	
	private static final HashMap<String, String> CryptographerMap = new HashMap<String, String>();
	
	/**
	 * Returns the cryptographer configured to handle usage of a given kind.
	 * @param usageName identifies a kind of usage
	 * @return a Symmetric, or null if none was configured for the supplied usageName
	 */
	public static Symmetric getCryptographer(String usageName) {
		String cryptName = CryptographerMap.get(usageName);
		if (cryptName == null) return null;
		return Symmetric.named(cryptName);
	}
	
	/**
	 * Initializes mappings from usage names to cryptographer names.
	 */
	public static class Mapper {

		/**
		 * Initializes the name mappings.
		 * @param mapping a formatted description of the mappings
		 */
		public void setMapElements(String mapping) {
			String[] parts = mapping.split(Separator);
			for (String part : parts) {
				String[] map = part.trim().split(Equals);
				String[] terms = map[1].trim().split(Comma);
				for (String term : terms) {
					CryptographerMap.put(term.trim(), map[0].trim());
				}
				Log.info("registered cryptographer " + part.trim());
			}
		}
	}
	
	static {
		Security.addProvider(new BouncyCastleProvider());
		SpringContext.named(ConfigurationFile).getBean(Mapper.class);
	}
	
	private static final String Empty = "";
	private static final String Comma = ",";
	private static final String Equals = "=";
	private static final String Separator = ";";
	private static final String Blank = " ";
	private static final String Pad = Hex.encodeHexString(Blank.getBytes());

	private static final String Algorithm = "AES";
	private static final String Transform = Algorithm + "/CBC/NoPadding";
	private static final String Encoding = "UTF-8";

	private static final int BlockSize = 16;
	private static final int ByteNibbles = 2;
	private static final int VectorSize = BlockSize * ByteNibbles;
	private static final byte[] EmptyBuffer = { };
	
	private String seedValue;
	private String keyValue;
	
	/**
	 * Constructs a new Symmetric.
	 */
	public Symmetric() { }
	
	/**
	 * Returns a configured cryptographer.
	 * @param configuredName a configured cryptographer name.
	 * @return a Symmetric, or null
	 */
	public static Symmetric named(String configuredName) {
		return SpringContext.named(ConfigurationFile).getBean(Symmetric.class, configuredName);
	}
	
	/**
	 * Returns a new Symmetric.
	 * @param seedValue an AES initialization vector (32 hex digits)
	 * @return a new Symmetric
	 */
	public static Symmetric withSeed(String seedValue) {
		Symmetric result = new Symmetric();
		result.seedValue = checkLength(seedValue, BadSeed);
		return result;
	}
	
	/**
	 * Sets the AES key.
	 * @param keyValue an AES key (32 hex digits).
	 * @return this Symmetric
	 */
	public Symmetric withKey(String keyValue) {
		this.keyValue = checkLength(keyValue, BadKey);
		return this;
	}
	
	/**
	 * Encrypts clear text into a hex string.
	 * @param clearText clear text data
	 * @return a hex string containing cypher data, or empty
	 */
	public String encryptAsHex(String clearText) {
		return Hex.encodeHexString(encrypt(clearText));
	}
	
	/**
	 * Encrypts clear text data.
	 * @param clearText clear text data
	 * @return a buffer containing cypher data, or empty
	 */
	public byte[] encrypt(String clearText) {
		try {
			return encryptBytes(clearText.getBytes(Encoding));
		} catch (Exception e) {
			Log.error(e.getMessage(), e);
			return EmptyBuffer;
		}
	}
	
	/**
	 * Encrypts clear text data.
	 * @param clearData a buffer containing the clear text data
	 * @return a buffer containing cypher data, or empty
	 */
	public byte[] encryptBytes(byte[] clearData) {
		if (clearData == null) return EmptyBuffer;
		if (clearData.length == 0) return EmptyBuffer;
		
		try {
			clearData = normalize(clearData);
			return buildEncrypter().doFinal(clearData);
		} catch (Exception e) {
			Log.error(e.getMessage(), e);
			return EmptyBuffer;
		}
	}
	
	/**
	 * Decrypts cypher text encoded as hex.
	 * @param cypherText a hex string that contains cypher data
	 * @return a clear text result, or empty
	 */
	public String decryptFromHex(String cypherText) {
		try {
			return decrypt(Hex.decodeHex(cypherText.toCharArray()));
		} catch (Exception e) {
			Log.error(e.getMessage(), e);
			return Empty;
		}
	}
	
	/**
	 * Decrypts cypher text data.
	 * @param cypherData a buffer that contains cypher data
	 * @return a buffer containing a clear text result, or empty
	 */
	public byte[] decryptBytes(byte[] cypherData) {
		if (cypherData == null) return EmptyBuffer;
		if (cypherData.length == 0) return EmptyBuffer;
		
		try {
			return buildDecrypter().doFinal(cypherData);
		} catch (Exception e) {
			Log.error(e.getMessage(), e);
			return EmptyBuffer;
		}
	}
	
	/**
	 * Decrypts cypher text data.
	 * @param cypherData a buffer that contains cypher data
	 * @return a clear text result, or empty
	 */
	public String decrypt(byte[] cypherData) {
		try {
			return new String(buildDecrypter().doFinal(cypherData), Encoding).trim();
		} catch (Exception e) {
			Log.error(e.getMessage(), e);
			return Empty;
		}
	}
	
	/**
	 * A seed value.
	 * @return a seedValue
	 */
	public String getSeedValue() {
		return this.seedValue;
	}

	/**
	 * A seed value.
	 * @param seedValue a seedValue
	 */
	public void setSeedValue(String seedValue) {
		this.seedValue = seedValue;
	}

	/**
	 * A key value.
	 * @return a keyValue
	 */
	public String getKeyValue() {
		return this.keyValue;
	}

	/**
	 * A key value.
	 * @param keyValue a keyValue
	 */
	public void setKeyValue(String keyValue) {
		this.keyValue = keyValue;
	}
	
	/**
	 * Normalizes a buffer to the length needed for encryption.
	 * @param clearData a buffer containing clear text data
	 * @return a buffer containing padded clear text data
	 * @throws Exception if raised during conversion
	 */
	private static byte[] normalize(byte[] clearData) throws Exception {
		int length = clearData.length;
		int extra = length % BlockSize;
		if (extra < 1) return clearData;

		String hex = Hex.encodeHexString(clearData);
		int padding = BlockSize - extra;
		while (padding-- > 0) hex += Pad;
		return Hex.decodeHex(hex.toCharArray());
	}
	
	private Cipher buildEncrypter() throws Exception {
		Cipher result = getCipher();
		result.init(Cipher.ENCRYPT_MODE, buildKey(), buildSeed());
		return result;
	}
	
	private Cipher buildDecrypter() throws Exception {
		Cipher result = getCipher();
		result.init(Cipher.DECRYPT_MODE, buildKey(), buildSeed());
		return result;
	}
	
	private Cipher getCipher() throws Exception {
		return Cipher.getInstance(Transform, BouncyCastleProvider.PROVIDER_NAME);
	}
	
	private IvParameterSpec buildSeed() throws Exception {
		return new IvParameterSpec(Hex.decodeHex(getSeedValue().toCharArray()));
	}
	
	private Key buildKey() throws Exception {
		return new SecretKeySpec(Hex.decodeHex(getKeyValue().toCharArray()), Algorithm);
	}
	
	@SuppressWarnings("unused")
	private static String checkLength(String value, String failMessage) {
		String result = StringUtils.defaultString(value).trim();
		if (result.length() != VectorSize) {
			throw new IllegalArgumentException(failMessage);
		}
		
		try {
			byte[] bytes = Hex.decodeHex(result.toCharArray());
		} catch (Exception e) {
			throw new IllegalArgumentException(failMessage);
		}
		
		return result;
	}

	private static final String BadSeed = "seed value must be a hex value of length " + VectorSize + " digits";
	private static final String BadKey = "key value must be a hex value of length " + VectorSize + " digits";

} // Symmetric