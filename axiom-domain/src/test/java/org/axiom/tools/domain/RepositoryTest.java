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

import org.apache.log4j.Logger;
import org.axiom.tools.codecs.EntityCodec;
import org.axiom.tools.storage.TransactionalContext;
import org.junit.*;

import static org.junit.Assert.*;

/**
 * Confirms proper operation of sample domain classes and their
 * respective repositories.
 */
public class RepositoryTest {

	private static final Logger Log = Logger.getLogger(RepositoryTest.class);
	
	@BeforeClass
	public static void initialize() {
		TransactionalContext context = new TransactionalContext();
		assertFalse(context == null);
	}
	
	@Test
	public void fullContact() {
		Person sample = 
			Person.named("George Jungleman")
			.with(Contact.Type.HOME, MailAddress.with("1234 Main St", "Anytown", "CA", "94005"))
			.with(Contact.Type.HOME, PhoneNumber.from("415-888-8899"));
		
		Person p = sample.save();
		Person x = Person.named("George Jungleman").find();
		assertTrue(x.getKey() == p.getKey());
		assertTrue(x.getHashKey() == p.getHashKey());
		x.describe();
		x.remove();
	}
	
	@Test
	public void invalidAddress() {
		MailAddress a = MailAddress.with("1234 Main St", "Anytown", "CAA", "94005");				
		String[] results = a.validate();
		assertTrue(results.length > 0);
		Log.info(results[0]);
	}
	
	@Test
	public void validation() {
		MailAddress a = MailAddress.with("1234 Main St", "Anytown", "CA", "94005");				
		String[] results = a.validate();
		assertTrue(results.length == 0);
	}
	
	@Test
	public void addressCodec() {
		MailAddress a = MailAddress.with("1234 Main St", "Anytown", "CA", "94005");				
		String json = EntityCodec.from(a).toJSON();
		Log.info(json);
		String xml = EntityCodec.from(a).toXML();
		Log.info(xml);
	}
	
	@Test
//	@Ignore
	public void basicPhones() {
		PhoneNumber n = PhoneNumber.from("888-888-8888");
		assertTrue(n.formatNumber().equals("888-888-8888"));
		String xml = EntityCodec.from(n).toXML();
		Log.info(xml);
	}
	
	@Test
//	@Ignore
	public void phoneStability() {
		PhoneNumber n = PhoneNumber.from("888-888-8888").save();
		PhoneNumber p = PhoneNumber.from("888-888-8888").save();
		assertTrue(n.getKey() == p.getKey());
		assertTrue(n.getHashKey() == p.getHashKey());
		
		PhoneNumber x = PhoneNumber.from("888-888-8888").find();
		assertTrue(x != null);
		assertTrue(x.getKey() == p.getKey());
	}
	
	@Test
//	@Ignore
	public void addressStability() {
		MailAddress a = MailAddress.with("1234 Main St", "Anytown", "CA", "94005").save();		
		MailAddress b = MailAddress.with("1234 Main St", "Anytown", "CA", "94005").find();

		assertTrue(b != null);
		assertTrue(b.getKey() == a.getKey());
		assertTrue(b.getHashKey() == a.getHashKey());
		a.describe();
		b.describe();
	}

	@Test
//	@Ignore
	public void componentLifecycle() {
		MailAddress a = MailAddress.with("1234 Main St", "Anytown", "CA", "94005").save();
		
		assertTrue(a != null);
		assertTrue(a.getKey() > 0);
		
		MailAddress b = a.reload();
		assertTrue(b != null);
		assertTrue(b.getKey() > 0);
		a.describe();
		b.describe();
		
		b = b.withCity("Uptown").save();
		assertTrue(b != null);
		assertFalse(b.getKey() == a.getKey());
		assertFalse(b.getHashKey() == a.getHashKey());
		assertFalse(b.getCity().equals(a.getCity()));
		b.describe();

		assertTrue(b.remove());
		assertTrue(b.reload() == null);
	}
	
	@Test
//	@Ignore
	public void compositeLifecycle() {
		System.out.println("phone count = " + PhoneNumber.count());
		System.out.println("email count = " + EmailAddress.count());
		System.out.println("address count = " + MailAddress.count());
		System.out.println("contact count = " + Contact.count());

		PhoneNumber p = PhoneNumber.from("415-888-8899").save();
		EmailAddress e = EmailAddress.from("sample@educery.com").save();
		Log.info(EntityCodec.from(e).toXML());

		MailAddress a = MailAddress.with("1234 Main St", "Anytown", "CA", "94005").save();
		MailAddress b = MailAddress.with("1234 Main St", "Anytown", "CA", "94005").save();

		Contact c = 
			new Contact()
				.withAddress(Contact.Type.HOME, a)
				.withAddress(Contact.Type.WORK, b)
				.withEmail(Contact.Type.HOME, e)
				.withPhone(Contact.Type.HOME, p)
				.save();

		assertTrue(c != null);
		assertTrue(c.getKey() > 0);
		assertTrue(c.countPhones() > 0);
		assertTrue(c.countAddresses() > 0);
		
		System.out.println("phone count = " + PhoneNumber.count());
		System.out.println("email count = " + EmailAddress.count());
		System.out.println("address count = " + MailAddress.count());
		System.out.println("contact count = " + Contact.count());
		
		Contact d = c.reload();
		assertTrue(d != null);
		assertTrue(d.getKey() > 0);
		assertTrue(c.countPhones() > 0);
		assertTrue(d.countAddresses() > 0);
		
		a = b.find();
		assertTrue(a != null);
		assertTrue(a.getKey() > 0);
		assertTrue(a.getHashKey() == b.getHashKey());
		
		PhoneNumber n = d.getPhone(Contact.Type.HOME);
		assertTrue(n != null);
		assertTrue(n.getKey() > 0);
		assertTrue(n.formatNumber().equals(p.formatNumber()));
		
		PhoneNumber pn = n.find();
		assertTrue(pn != null);
		assertTrue(pn.getKey() > 0);
		assertTrue(pn.formatNumber().equals(p.formatNumber()));
		
		b = d.getAddress(Contact.Type.WORK);
		assertTrue(b != null);
		assertTrue(b.getKey() > 0);
		assertTrue(b.getCity().equals(a.getCity()));
		
		d.removeAddress(Contact.Type.WORK);

		assertTrue(d.remove());
		assertTrue(d.reload() == null);
		assertTrue(b.reload() == null);
		
		System.out.println("phone count = " + PhoneNumber.count());
		System.out.println("email count = " + EmailAddress.count());
		System.out.println("address count = " + MailAddress.count());
		System.out.println("contact count = " + Contact.count());
	}

} // RepositoryTest
