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
package org.axiom_tools.services;

import java.util.*;
import javax.ws.rs.core.Response;
import org.axiom_tools.codecs.ValueMap;

import org.junit.*;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;

import server.ServiceController;
import org.axiom_tools.domain.*;
import org.axiom_tools.context.SpringContext;
import static org.axiom_tools.domain.Contact.Kind.HOME;
import org.axiom_tools.domain.Contact.Type;
import org.axiom_tools.faces.IPersonService;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Confirms proper operation of the person service.
 * @author nik
 */
//@Ignore
public class ServiceTest {

    private static final String ConfigurationFile = "/service-client.xml";

    private ConfigurableApplicationContext context;

    private IPersonService service;
    private IPersonService getService() {
        if (this.service == null) {
            this.service = SpringContext.named(ConfigurationFile).getBean(IPersonService.class);
        }
        return this.service;
    }

    @Before
    public void startServer() {
        String[] args = { };
        getLogger().info("starting service tests");
        System.getProperties().put("spring.profiles.active", "direct");
        context = SpringApplication.run(ServiceController.class, args);
        assertFalse(getService() == null);
    }
    
    @After
    public void stopServer() {
        context.close();
    }

    @Test
    public void sampleLifecycle() {
        Person sample =
        Person.named("George Jungleman")
            .with(HOME, createSampleAddress("1234 Main St"))
            .with(HOME, createSampleEmail())
            .with(HOME, createSamplePhone());

        sample.describe();
        Response r = getService().createPerson(sample.toJSON());
        assertTrue(r.getStatus() == 200);
        Integer idA = ValueMap.fromJSON(readJSON(r)).getValue(ValueMap.ID);

        r = getService().getPerson(idA);
        assertTrue(r.getStatus() == 200);
        Person p = Person.fromJSON(readJSON(r));
        assertFalse(p == null);
        p.describe();

        PhoneNumber ph = p.getContact().getPhone(HOME);
        r = getService().getPersonWithHash(Type.phone, ph.formatNumber());
        assertTrue(r.getStatus() == 200);
        List<Person> results = Person.listFromJSON(readJSON(r));
        assertFalse(results.isEmpty());

        EmailAddress em = p.getContact().getEmail(HOME);
        r = getService().getPersonWithHash(Type.email, em.formatAddress());
        assertTrue(r.getStatus() == 200);
        results = Person.listFromJSON(readJSON(r));
        assertFalse(results.isEmpty());

        r = getService().getPersonWithHash(Type.hash, p.getName());
        assertTrue(r.getStatus() == 200);
        results = Person.listFromJSON(readJSON(r));
        assertFalse(results.isEmpty());

        MailAddress a = p.getContact().getAddress(HOME);
        p.getContact().withAddress(HOME, a.withCity("Sometown"));
        r = getService().savePerson(p.getKey(), p.toJSON());
        assertTrue(r.getStatus() == 200);

        p = Person.fromJSON(readJSON(r));
        assertFalse(p == null);
        p.describe();

        Person simple =
        Person.named("George Bungleman")
            .with(HOME, createSampleAddress("4321 Main St"))
            .with(HOME, PhoneNumber.from("415-889-9988"));

        r = getService().createPerson(simple.toJSON());
        assertTrue(r.getStatus() == 200);
        Integer idB = ValueMap.fromJSON(readJSON(r)).getValue(ValueMap.ID);

        r = getService().listPersons("George", "", "94005");
        assertTrue(r.getStatus() == 200);

        results = Person.listFromJSON(readJSON(r));
        assertFalse(results.isEmpty());
        getLogger().info("found " + results.size() + " matches");

        getService().deletePerson(idB);
        getService().deletePerson(idA);

        r = getService().listPersons("George", "", "94005");
        assertTrue(r.getStatus() == 200);
        assertTrue(Person.listFromJSON(readJSON(r)).isEmpty());
    }
    
    private MailAddress createSampleAddress(String streetAddress) {
        return MailAddress.with(streetAddress, "Anytown", "CA", "94005");
    }
    
    private EmailAddress createSampleEmail() {
        return EmailAddress.from("george@jungleman.com");
    }
    
    private PhoneNumber createSamplePhone() {
        return PhoneNumber.from("415-888-8899");
    }
    
    private String readJSON(Response r) {
        return r.readEntity(String.class);
    }

    private Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
    }

} // ServiceTest
