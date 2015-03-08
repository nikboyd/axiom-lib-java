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
package org.axiom_tools.faces;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.axiom_tools.domain.Person;

/**
 * Maintains customers.
 * @author nik
 */
public interface ICustomerService {
    
    public static final String CustomerId = "id";
    public static final String CustomerPath = "/customers";
    public static final String CustomerIdPath = CustomerPath + "/{id}";
    
    public static final String Name = "name";
    public static final String City = "city";
    public static final String Zip = "zip";
    
    /**
     * Creates a new Customer.
     * @param customer a customer
     * @return a Response indicating:
     * <ul>
     * <li>200 if properly created</li>
     * </ul>
     */
    @POST
    @Path(CustomerPath)
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response createCustomer(Person customer);
    
    @PUT
    @Path(CustomerPath)
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response updateCustomer(Person customer);
    
    @DELETE
    @Path(CustomerIdPath)
    public Response deleteCustomer(@PathParam(CustomerId) long customerID);

    @GET
    @Path(CustomerIdPath)
    @Produces({MediaType.APPLICATION_JSON})
    public Response getCustomer(@PathParam(CustomerId) long customerID);

    @GET
    @Path(CustomerPath)
    @Produces({MediaType.APPLICATION_JSON})
    public Response listCustomers(
        @QueryParam(Name) String name,
        @QueryParam(City) String city, 
        @QueryParam(Zip) String zip
    );
    
} // ICustomerService
