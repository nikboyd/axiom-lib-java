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
import org.axiom_tools.codecs.ValueMap;
import org.axiom_tools.domain.Contact;
import org.axiom_tools.domain.EmailAddress;
import org.springframework.stereotype.Service;

import org.axiom_tools.domain.Person;
import org.axiom_tools.domain.PhoneNumber;
import org.axiom_tools.faces.IPersonService;
import org.axiom_tools.storage.StorageMechanism;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * A service for maintaining Persons.
 * @author nik
 */
@Service
@Transactional
@Path(IPersonService.BasePath)
public class PersonFacade implements IPersonService {

    private static final String Empty = "";
    private static final String Wild = "%";

    @Autowired
    private StorageMechanism.Registry registry;

    @Override
    public Response listPersons(String name, String city, String zip) {
        List<Person> results = Person.like(Wild + name + Wild);
        return Response.ok(results).build();
    }

    @Override
    public Response createPerson(String personJSON) {
        Person sample = Person.fromJSON(personJSON);
        Person p = sample.saveItem();
        ValueMap result = ValueMap.withID(p.getKey());
        return Response.ok(result.toJSON()).build();
    }

    @Override
    public Response savePerson(long personID, String personJSON) {
        Person sample = Person.fromJSON(personJSON);
        if (sample.getKey() != personID) {
            return Response.status(Status.CONFLICT).build();
        }

        Person p = Person.withKey(personID).findItem();
        if (p == null) {
            return Response.status(Status.GONE).build();
        }

        p = sample.saveItem();
        return Response.ok(p).build();
    }

    @Override
    public Response getPerson(long personID) {
        Person p = Person.withKey(personID).findItem();
        if (p == null) {
            return Response.status(Status.GONE).build();
        }
        else {
            return Response.ok(p).build();
        }
    }

    @Override
    public Response getPersonWithHash(Contact.Type idType, String personID) {
        switch (idType) {
            case hash: {
                Person result = Person.named(personID).findWithHash();
                Person[] results = { result };
                return Response.ok(Arrays.asList(results)).build();
            }
            case email: {
                List<Person> results = Person.findSimilar(EmailAddress.from(personID));
                return Response.ok(results).build();
            }
            case phone: {
                List<Person> results = Person.findSimilar(PhoneNumber.from(personID));
                return Response.ok(results).build();
            }
        }
        
        Person[] results = { };
        return Response.ok(Arrays.asList(results)).build();
    }

    @Override
    public Response deletePerson(long personID) {
        Person p = Person.withKey(personID).findItem();
        if (p == null) {
            return Response.status(Status.GONE).build();
        }

        boolean gone = p.removeItem();
        return Response.accepted().build();
    }

    @Override
    public Response deletePersonWithHash(Contact.Type idType, String personID) {
        switch (idType) {
            case hash: {
                Person p = Person.named(personID).findWithHash();
                if (p == null) {
                    return Response.status(Status.GONE).build();
                }

                boolean gone = p.removeItem();
                return Response.accepted().build();
            }
            case email: {
                List<Person> results = Person.findSimilar(EmailAddress.from(personID));
                if (results.isEmpty()) {
                    return Response.status(Status.GONE).build();
                }

                boolean gone = results.get(0).removeItem();
                return Response.accepted().build();
            }
            case phone: {
                List<Person> results = Person.findSimilar(PhoneNumber.from(personID));
                if (results.isEmpty()) {
                    return Response.status(Status.GONE).build();
                }

                boolean gone = results.get(0).removeItem();
                return Response.accepted().build();
            }
        }

        return Response.accepted().build();
    }
    
}
