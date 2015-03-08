/**
 * Copyright 2015 Nikolas Boyd.
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

import org.junit.*;
import static org.junit.Assert.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.axiom_tools.codecs.EntityCodec;
import org.axiom_tools.domain.Contact.Kind;

/**
 * Confirms proper formatting of content.
 * @author nik
 */
public class ContentTest {

    private static final Log Logger = LogFactory.getLog(ContentTest.class);
    
    @Test
    public void personCodec() {
		Person sample = 
			Person.named("George Jungleman")
                .with(Kind.HOME, MailAddress.with("1234 Main St", "Anytown", "CA", "94005"))
                .with(Kind.HOME, PhoneNumber.from("415-888-8899"));
        
		String json = EntityCodec.from(sample).toJSON();
		Logger.info(json);
        
        Person test = EntityCodec.to(Person.class).fromJSON(json);
        assertFalse(test == null);
        test.describe();
    }
	
	@Test
	public void addressCodec() {
		MailAddress sample = MailAddress.with("1234 Main St", "Anytown", "CA", "94005");	
		String json = EntityCodec.from(sample).toJSON();
		Logger.info(json);
		String xml = EntityCodec.from(sample).toXML();
		Logger.info(xml);
        
        MailAddress test = EntityCodec.to(MailAddress.class).fromJSON(json);
        assertFalse(test == null);
        
        ContactMechanism mech = ContactMechanism.with(Kind.HOME, sample);
        Logger.info(EntityCodec.from(mech).toJSON());
	}
	
	@Test
	public void basicPhones() {
		PhoneNumber sample = PhoneNumber.from("888-888-8888");
		assertTrue(sample.formatNumber().equals("888-888-8888"));
		String xml = EntityCodec.from(sample).toXML();
		Logger.info(xml);
	}

} // ContentTest
