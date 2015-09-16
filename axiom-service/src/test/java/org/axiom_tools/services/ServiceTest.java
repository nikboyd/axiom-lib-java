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

import org.junit.*;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;

import server.ServiceController;
import org.axiom_tools.domain.*;
import org.axiom_tools.context.SpringContext;
import org.axiom_tools.faces.ICustomerService;

/**
 * Confirms proper operation of the customer service.
 * @author nik
 */
@Ignore
public class ServiceTest {

    private static final String ConfigurationFile = "/service-client.xml";

    private ICustomerService service;
    private ICustomerService getService() {
        if (this.service == null) {
            this.service = SpringContext.named(ConfigurationFile).getBean(ICustomerService.class);
        }
        return this.service;
    }

    @Before
    public void startServer() {
        String[] args = { };
        getLogger().info("starting service tests");
        SpringApplication.run(ServiceController.class, args);
        assertFalse(getService() == null);
    }

    @Test
    public void customerLifecycle() {
		Person sample =
			Person.named("George Jungleman")
                .with(Contact.Kind.HOME, MailAddress.with("1234 Main St", "Anytown", "CA", "94005"))
                .with(Contact.Kind.HOME, PhoneNumber.from("415-888-8899"));

        sample.describe();
        Response r = getService().createCustomer(sample.toJSON());
        assertTrue(r.getStatus() == 200);
        String keyA = r.readEntity(String.class);

        r = getService().getCustomer(Long.parseLong(keyA));
        assertTrue(r.getStatus() == 200);
        Person p = Person.fromJSON(r.readEntity(String.class));
        assertFalse(p == null);
        p.describe();

        MailAddress a = p.getContact().getAddress(Contact.Kind.HOME);
        p.getContact().withAddress(Contact.Kind.HOME, a.withCity("Sometown"));
        r = getService().updateCustomer(p.getKey(), p.toJSON());
        assertTrue(r.getStatus() == 200);

        p = Person.fromJSON(r.readEntity(String.class));
        assertFalse(p == null);
        p.describe();

		Person simple =
			Person.named("George Bungleman")
                .with(Contact.Kind.HOME, MailAddress.with("4321 Main St", "Anytown", "CA", "94005"))
                .with(Contact.Kind.HOME, PhoneNumber.from("415-889-9988"));

        r = getService().createCustomer(simple.toJSON());
        assertTrue(r.getStatus() == 200);
        String keyB = r.readEntity(String.class);

        r = getService().listCustomers("George", "", "94005");
        assertTrue(r.getStatus() == 200);
        String listJSON = r.readEntity(String.class);

        List<Person> results = Person.listFromJSON(listJSON);
        assertFalse(results.isEmpty());
        getLogger().info("found " + results.size() + " matches");

        getService().deleteCustomer(Long.parseLong(keyB));
        getService().deleteCustomer(Long.parseLong(keyA));

        r = getService().listCustomers("George", "", "94005");
        assertTrue(r.getStatus() == 200);
        listJSON = r.readEntity(String.class);
        assertTrue(Person.listFromJSON(listJSON).isEmpty());
    }

    private Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
    }

} // ServiceTest
