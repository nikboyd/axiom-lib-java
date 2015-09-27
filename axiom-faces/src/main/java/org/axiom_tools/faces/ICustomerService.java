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

import java.util.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import org.axiom_tools.domain.Person;
import com.webcohesion.enunciate.metadata.rs.*;

/**
 * Maintains customers.
 * @author nik
 */
public interface ICustomerService {

    public static final String BasePath = "/";
    public static final String CustomerId = "id";
    public static final String CustomerPath = "/customers";
    public static final String CustomerIdPath = CustomerPath + "/{id}";

    public static final String Name = "name";
    public static final String City = "city";
    public static final String Zip = "zip";

    /**
     * Lists the customers resembling the supplied data.
     * @param name a name
     * @param city a city
     * @param zip a zip code
     * @return a Response containing a list of matching customers
     */
    @GET
    @Path(CustomerPath)
    @TypeHint(List.class)
    @Produces({MediaType.APPLICATION_JSON})
    @StatusCodes({
        @ResponseCode(code = 200, condition = "selected customers")})
    public Response listCustomers(
        @QueryParam(Name) String name,
        @QueryParam(City) String city,
        @QueryParam(Zip) String zip);

    /**
     * Creates a new Customer.
     * @param customerJSON a customer formatted as JSON
     * @return a Response containing a new customer ID.
     */
    @POST
    @Path(CustomerPath)
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @StatusCodes({
        @ResponseCode(code = 201, condition = "created customer")})
    public Response createCustomer(
        @TypeHint(Person.class) String customerJSON);

    /**
     * Gets a Customer.
     * @param customerID identifies a customer
     * @return a Response containing customer data
     */
    @GET
    @Path(CustomerIdPath)
    @TypeHint(Person.class)
    @Produces({MediaType.APPLICATION_JSON})
    @StatusCodes({
        @ResponseCode(code = 200, condition = "found customer"),
        @ResponseCode(code = 410, condition = "missing customer") })
    public Response getCustomer(
        @PathParam(CustomerId) long customerID);

    /**
     * Updates a Customer.
     * @param customerID identifies a customer
     * @param customerJSON a customer formatted as JSON
     * @return a Response containing updated JSON
     */
    @PUT
    @Path(CustomerIdPath)
    @TypeHint(Person.class)
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @StatusCodes({
        @ResponseCode(code = 200, condition = "saved customer"),
        @ResponseCode(code = 410, condition = "missing customer") })
    public Response updateCustomer(
        @PathParam(CustomerId) long customerID,
        @TypeHint(Person.class) String customerJSON);

    /**
     * Deletes a Customer.
     * @param customerID identifies a customer
     * @return a Response indicating whether accepted and deleted.
     */
    @DELETE
    @Path(CustomerIdPath)
    @StatusCodes({
        @ResponseCode(code = 202, condition = "deleted customer"),
        @ResponseCode(code = 410, condition = "missing customer") })
    public Response deleteCustomer(
        @PathParam(CustomerId) long customerID);

} // ICustomerService
