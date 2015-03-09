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

import javax.ws.rs.core.Response;

import org.junit.*;
import static org.junit.Assert.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;

import server.ServiceController;
import org.axiom_tools.domain.*;
import org.axiom_tools.context.SpringContext;
import org.axiom_tools.faces.ICustomerService;

/**
 * Confirms proper operation of the customer service.
 * @author nik
 */
public class ServiceTest {

    private static final Log Logger = LogFactory.getLog(ServiceTest.class);
    private static final String ConfigurationFile = "/service-client.xml";

    private ICustomerService service;
    
    @Before
    public void startServer() {
        String[] args = { };
        SpringApplication.run(ServiceController.class, args);
        this.service = SpringContext.named(ConfigurationFile).getBean(ICustomerService.class);
        assertFalse(this.service == null);
    }
    
    @Test
    public void sampleGET() {
        Response r = this.service.getCustomer(1234);
        Logger.info("code = " + r.getStatus());
    }
    
    @Test
    @Ignore
    public void simpleCalls() {
		Person sample = 
			Person.named("George Jungleman")
                .with(Contact.Kind.HOME, MailAddress.with("1234 Main St", "Anytown", "CA", "94005"))
                .with(Contact.Kind.HOME, PhoneNumber.from("415-888-8899"));
        
        Response r = this.service.createCustomer(sample);
        Logger.info("code = " + r.getStatus());
    }
    
} // ServiceTest
