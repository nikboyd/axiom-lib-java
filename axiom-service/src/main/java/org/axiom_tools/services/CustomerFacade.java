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
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.springframework.stereotype.Service;

import org.axiom_tools.domain.Person;
import org.axiom_tools.faces.ICustomerService;

/**
 * A service for maintaining customers. Customer data is stored as a Person.
 * @author nik
 */
@Service
@Path(ICustomerService.BasePath)
public class CustomerFacade implements ICustomerService {
    
    private static final String Wild = "%";

    @Override
    public Response createCustomer(String customerJSON) {
        Person sample = Person.fromJSON(customerJSON);
        Person p = sample.save();
        return Response.ok(p.getKey()).build();
    }

    @Override
    public Response updateCustomer(long id, String customerJSON) {
        Person sample = Person.fromJSON(customerJSON);
        if (sample.getKey() != id) {
            return Response.status(Status.CONFLICT).build();
        }

        Person p = Person.withKey(id).reload();
        if (p == null) {
            return Response.status(Status.GONE).build();
        }

        p = sample.save();
        return Response.ok(p).build();
    }

    @Override
    public Response deleteCustomer(long id) {
        Person p = Person.withKey(id).reload();
        if (p == null) {
            return Response.status(Status.GONE).build();
        }

        boolean gone = p.remove();
        return Response.accepted().build();
    }

    @Override
    public Response getCustomer(long id) {
        Person p = Person.withKey(id).reload();
        if (p == null) {
            return Response.status(Status.GONE).build();
        }
        else {
            return Response.ok(p).build();
        }
    }

    @Override
    public Response listCustomers(String name, String city, String zip) {
        List<Person> results = Person.like(Wild + name + Wild).findLike();
        return Response.ok(results).build();
    }
    
} // CustomerFacade
