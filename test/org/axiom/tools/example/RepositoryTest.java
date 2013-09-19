package org.axiom.tools.example;

import org.junit.*;

import static org.junit.Assert.*;

import org.axiom.tools.domain.Contact;
import org.axiom.tools.domain.EmailAddress;
import org.axiom.tools.domain.PhoneNumber;
import org.axiom.tools.domain.StreetAddress;

/**
 * Confirms proper operation of sample domain classes and their
 * respective repositories.
 */
public class RepositoryTest {
	
	@Test
//	@Ignore
	public void basicPhones() {
		PhoneNumber n = PhoneNumber.from("888-888-8888");
		assertTrue(n.formatNumber().equals("888-888-8888"));
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
		StreetAddress a = 
				new StreetAddress()
					.withStreet("1234 Main St")
					.withCity("Anytown")
					.withStateCode("CA")
					.withPostalCode("94005")
					.save();
		
		StreetAddress b = 
				new StreetAddress()
					.withStreet("1234 Main St")
					.withCity("Anytown")
					.withStateCode("CA")
					.withPostalCode("94005")
					.find();

		assertTrue(b != null);
		assertTrue(b.getKey() == a.getKey());
		assertTrue(b.getHashKey() == a.getHashKey());
		a.describe();
		b.describe();
	}

	@Test
//	@Ignore
	public void componentLifecycle() {
		StreetAddress a = 
			new StreetAddress()
				.withStreet("1234 Main St")
				.withCity("Anytown")
				.withStateCode("CA")
				.withPostalCode("94005")
				.save();
		
		assertTrue(a != null);
		assertTrue(a.getKey() > 0);
		
		StreetAddress b = a.reload();
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
		System.out.println("address count = " + StreetAddress.count());
		System.out.println("contact count = " + Contact.count());

		PhoneNumber p = PhoneNumber.from("415-717-2158").save();
		EmailAddress e = EmailAddress.from("nikboyd@sonic.net").save();

		StreetAddress a = 
			new StreetAddress()
				.withStreet("1234 Main St")
				.withCity("Anytown")
				.withStateCode("CA")
				.withPostalCode("94005")
				.save();

		StreetAddress b =
			new StreetAddress()
				.withStreet("1234 Main St")
				.withCity("Anytown")
				.withStateCode("CA")
				.withPostalCode("94005")
				.save();

		Contact c = 
			new Contact()
				.addAddress(Contact.Type.HOME, a)
				.addAddress(Contact.Type.WORK, b)
				.addEmail(Contact.Type.HOME, e)
				.addPhone(Contact.Type.HOME, p)
				.save();

		assertTrue(c != null);
		assertTrue(c.getKey() > 0);
		assertTrue(c.countPhones() > 0);
		assertTrue(c.countAddresses() > 0);
		
		System.out.println("phone count = " + PhoneNumber.count());
		System.out.println("email count = " + EmailAddress.count());
		System.out.println("address count = " + StreetAddress.count());
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
		System.out.println("address count = " + StreetAddress.count());
		System.out.println("contact count = " + Contact.count());
	}

} // RepositoryTest
