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

import java.util.*;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import org.axiom_tools.codecs.ModelCodec;
import org.axiom_tools.domain.Contact.Kind;
import org.axiom_tools.storage.StorageMechanism;
import org.axiom_tools.storage.PersistenceContext;

/**
 * Confirms proper operation of sample models and their persistence.
 * @author nik
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PersistenceContext.class })
public class RepositoryTest {

    @BeforeClass
    public static void prepare() {
        LoggerFactory.getLogger(RepositoryTest.class).info("started SampleTest");
    }

    @Autowired
    private StorageMechanism.Registry registry;

    @Test
    public void registeredStores() {
        getLogger().info("registered stores count = " + registry.size());
    }

    @Test
    public void invalidAddress() {
        MailAddress a = MailAddress.with("1234 Main St", "Anytown", "CAA", "94005");
        String[] results = a.validate();
        assertTrue(results.length > 0);
        getLogger().info(results[0]);
    }

    @Test
    public void validation() {
        MailAddress a = MailAddress.with("1234 Main St", "Anytown", "CA", "94005");
        String[] results = a.validate();
        assertTrue(results.length == 0);
    }

    @Test
    public void phoneSample() throws Exception {
        int count = PhoneNumber.count();
        getLogger().info("count = " + count);

        PhoneNumber p = PhoneNumber.from("888-888-8888").saveItem();
        assertFalse(p == null);
        getLogger().info("saved: " + p.formatNumber() + " hash: " + p.hashKey());

        int testCount = PhoneNumber.count();
        assertTrue(testCount > count);
        getLogger().info("count = " + testCount);

        PhoneNumber n = p.findItem();
        assertTrue(n != null);
        getLogger().info("found: " + n.formatNumber() + " hash: " + n.hashKey());

        n = p.findWithHash();
        assertTrue(n != null);
        getLogger().info("found: " + n.formatNumber() + " hash: " + n.hashKey());

        n.removeItem();
        int afterCount = PhoneNumber.count();
        assertTrue(afterCount == count);
        getLogger().info("count = " + afterCount);
    }

    @Test
    public void phoneStability() {
        PhoneNumber n = PhoneNumber.from("888-888-8888").saveItem();
        PhoneNumber p = PhoneNumber.from("888-888-8888").saveItem();
        assertTrue(n.getKey() == p.getKey());
        assertTrue(n.hashKey() == p.hashKey());

        PhoneNumber x = PhoneNumber.from("888-888-8888").findWithHash();
        assertTrue(x != null);
        assertTrue(x.getKey() == p.getKey());
    }

    @Test
    public void addressStability() {
        MailAddress a = MailAddress.with("1234 Main St", "Anytown", "CA", "94005").saveItem();
        MailAddress b = MailAddress.with("1234 Main St", "Anytown", "CA", "94005").findWithHash();

        assertTrue(b != null);
        assertTrue(b.getKey() == a.getKey());
        assertTrue(b.hashKey() == a.hashKey());
        a.describe();
        b.describe();
    }

    @Test
    public void componentLifecycle() {
        MailAddress a = MailAddress.with("1234 Main St", "Anytown", "CA", "94005").saveItem();

        assertTrue(a != null);
        assertTrue(a.getKey() > 0);

        MailAddress b = a.findItem();
        assertTrue(b != null);
        assertTrue(b.getKey() > 0);
        a.describe();
        b.describe();

        b = b.withCity("Uptown").saveItem();
        assertFalse(b == null);
        assertFalse(b.getKey() == a.getKey());
        assertFalse(b.hashKey() == a.hashKey());
        assertFalse(b.getCity().equals(a.getCity()));
        b.describe();

        assertTrue(b.removeItem());
        assertTrue(b.findItem() == null);
    }

    @Test
    @Transactional
    public void compositeLifecycle() {
        getLogger().info("phone count = " + PhoneNumber.count());
        getLogger().info("email count = " + EmailAddress.count());
        getLogger().info("address count = " + MailAddress.count());
        getLogger().info("contact count = " + Contact.count());

        PhoneNumber p = PhoneNumber.from("415-888-8899").saveItem();
        EmailAddress e = EmailAddress.from("sample@educery.com").saveItem();
        getLogger().info(ModelCodec.from(e).toJSON());

        MailAddress a = MailAddress.with("1234 Main St", "Anytown", "CA", "94005").saveItem();
        MailAddress b = MailAddress.with("4321 Main St", "Anytown", "CA", "94005").saveItem();

        Contact c = new Contact()
            .withAddress(Kind.HOME, a)
            .withAddress(Kind.WORK, b)
            .withEmail(Kind.HOME, e)
            .withPhone(Kind.HOME, p)
            .saveItem();

        assertTrue(c != null);
        assertTrue(c.getKey() > 0);
        assertTrue(c.countPhones() > 0);
        assertTrue(c.countAddresses() > 0);

        getLogger().info("phone count = " + PhoneNumber.count());
        getLogger().info("email count = " + EmailAddress.count());
        getLogger().info("address count = " + MailAddress.count());
        getLogger().info("contact count = " + Contact.count());

        Contact d = c.findItem();
        assertTrue(d != null);
        assertTrue(d.getKey() > 0);
        assertTrue(c.countPhones() > 0);
        assertTrue(d.countAddresses() > 0);

        a = b.findItem();
        assertTrue(a != null);
        assertTrue(a.getKey() > 0);
        assertTrue(a.hashKey() == b.hashKey());

        PhoneNumber n = d.getPhone(Kind.HOME);
        assertTrue(n != null);
        assertTrue(n.getKey() > 0);
        assertTrue(n.formatNumber().equals(p.formatNumber()));

        PhoneNumber pn = n.findItem();
        assertTrue(pn != null);
        assertTrue(pn.getKey() > 0);
        assertTrue(pn.formatNumber().equals(p.formatNumber()));

        b = d.getAddress(Kind.WORK);
        assertTrue(b != null);
        assertTrue(b.getKey() > 0);
        assertTrue(b.getCity().equals(a.getCity()));

        d.removeAddress(Kind.WORK);

        assertTrue(d.removeItem());
        assertTrue(d.findItem() == null);
//        assertTrue(b.findItem() == null);

        getLogger().info("phone count = " + PhoneNumber.count());
        getLogger().info("email count = " + EmailAddress.count());
        getLogger().info("address count = " + MailAddress.count());
        getLogger().info("contact count = " + Contact.count());
    }

    @Test
    @Transactional
    public void fullContact() {
        Person sample = Person.named("George Jungleman")
            .with(Kind.HOME, MailAddress.with("1234 Main St", "Anytown", "CA", "94005"))
            .with(Kind.HOME, EmailAddress.from("george@jungleman.com"))
            .with(Kind.HOME, PhoneNumber.from("415-888-8899"));

        Person p = sample.saveItem();
        Person x = Person.named("George Jungleman").findWithHash();
        assertTrue(x.getKey() == p.getKey());
        assertTrue(x.hashKey() == p.hashKey());
        x.describe();

        List<Person> ps = Person.like("%Jungle%");
        assertFalse(ps.isEmpty());

        p = Person.withKey(p.getKey()).findItem();
        assertFalse(p == null);
        
        ps = Person.findSimilar(EmailAddress.from("george@jungleman.com"));
        assertFalse(ps.isEmpty());
        
        ps = Person.findSimilar(PhoneNumber.from("415-888-8899"));
        assertFalse(ps.isEmpty());

        x.removeItem();
    }

    private Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
    }

}
