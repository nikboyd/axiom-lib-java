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
import io.swagger.annotations.*;
import org.axiom_tools.domain.Person;

/**
 * Maintains customers.
 * @author nik
 */
@Api(value = ICustomerService.BasePath, description = "Operations on Customers")
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
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(response = Person.class, responseContainer = "List",
        value = "Gets a list of selected customers.",
        notes = "Gets a list of customers that resemble the supplied data.")
    @ApiResponses({
        @ApiResponse(code = 200, message = "found customer"), })
    public Response listCustomers(
        @ApiParam(value = "a customer name", required = true) @QueryParam(Name) String name,
        @ApiParam(value = "a customer address city", required = false) @QueryParam(City) String city,
        @ApiParam(value = "a customer address zip", required = false) @QueryParam(Zip) String zip);

    /**
     * Creates a new Customer.
     * @param customer a customer formatted as JSON
     * @return a Response containing a new customer ID.
     */
    @POST
    @Path(CustomerPath)
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @ApiOperation(response = Long.class,
        value = "Creates a new customer.",
        notes = "Creates a new customer (if not already present).")
    @ApiResponses({
        @ApiResponse(code = 201, message = "created a customer"), })
    public Response createCustomer(
        @ApiParam(value = "a customer", required = false) String customer);

    /**
     * Updates a Customer.
     * @param customerID identifies a customer
     * @param customer a customer formatted as JSON
     * @return a Response containing updated JSON
     */
    @PUT
    @Path(CustomerIdPath)
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @ApiOperation(response = Person.class,
        value = "Updates a customer." )
    @ApiResponses({
        @ApiResponse(code = 200, message = "updated the customer"),
        @ApiResponse(code = 410, message = "customer missing"), })
    public Response updateCustomer(
        @ApiParam(value = "a customer ID", required = true) @PathParam(CustomerId) long customerID,
        @ApiParam(value = "a customer", required = true) String customer);

    /**
     * Deletes a Customer.
     * @param customerID identifies a customer
     * @return a Response indicating whether accepted and deleted.
     */
    @DELETE
    @Path(CustomerIdPath)
    @ApiOperation(
        value = "Deletes a selected customer." )
    @ApiResponses({
        @ApiResponse(code = 202, message = "customer deleted"),
        @ApiResponse(code = 410, message = "customer missing"), })
    public Response deleteCustomer(
        @ApiParam(value = "a customer ID", required = true) @PathParam(CustomerId) long customerID);

    /**
     * Gets a Customer.
     * @param customerID identifies a customer
     * @return a Response containing customer data
     */
    @GET
    @Path(CustomerIdPath)
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(response = Person.class,
        value = "Gets a selected customer." )
    @ApiResponses({
        @ApiResponse(code = 410, message = "customer missing"), })
    public Response getCustomer(
        @ApiParam(value = "a customer ID", required = true) @PathParam(CustomerId) long customerID);

} // ICustomerService
