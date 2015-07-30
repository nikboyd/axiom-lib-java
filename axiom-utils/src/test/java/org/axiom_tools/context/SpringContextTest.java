/**
 * Copyright 2014,2015 Nikolas Boyd.
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
package org.axiom_tools.context;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.junit.*;

import static org.junit.Assert.*;
import org.slf4j.LoggerFactory;

/**
 * Confirms the proper operation of SpringContext.
 */
public class SpringContextTest {

	public static class Sample {
		public int x;
		public int getX() { return this.x; }
		public void setX(int x) { this.x = x; }
	}

	@Test
	public void codecTest() {
		byte[] sample = { 1, 3, 2, 4, 5, 14, 15, 120  };
		byte[] encoded = Base64.encodeBase64(sample);
		byte[] decoded = Base64.decodeBase64(encoded);
        getLogger().info(Hex.encodeHexString(decoded));
	}

	@Test
	public void loadTest() {
		Sample sample = SpringContext.getConfigured(Sample.class);
		assertTrue(sample != null);
		assertTrue(sample.getX() == 1);

		sample = SpringContext.getConfigured(Sample.class, "Simple");
		assertTrue(sample == null);

		sample = SpringContext.getConfigured(Sample.class, "Another");
		assertTrue(sample != null);
		assertTrue(sample.getX() == 2);
	}

    private org.slf4j.Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
    }
}
