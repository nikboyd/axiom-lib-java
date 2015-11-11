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
import org.axiom_tools.codecs.ValueMap;
import org.axiom_tools.domain.Contact;

/**
 * Maintains personal details for each registered Person.
 * @author nik
 */
public interface IPersonService {

    public static final String BasePath = "/";
    public static final String IdPath = "/{" + ValueMap.ID + "}";

    public static final String PersonPath = "/persons";
    public static final String PersonIdPath = PersonPath + IdPath;
    public static final String HashedIdPath = PersonPath + "/hash";

    public static final String Type = "type";
    public static final String Name = "name";
    public static final String City = "city";
    public static final String Zip = "zip";

    /**
     * Lists the selected persons.
     * @param name a person full name or name pattern
     * @param city a city name or pattern
     * @param zip a zip code
     * @return Contains a list of the selected persons.
     */
    @GET
    @Path(PersonPath)
    @TypeHint(List.class)
    @Produces({MediaType.APPLICATION_JSON})
    @StatusCodes({
        @ResponseCode(code = 200, condition = "selected persons")})
    public Response listPersons(
        @QueryParam(Name) String name,
        @QueryParam(City) String city,
        @QueryParam(Zip) String zip);

    /**
     * Creates and registers a new Person.
     * @param personJSON contains personal details
     * @return Contains the personal IDs usable for retrieval.
     */
    @POST
    @Path(PersonPath)
    @TypeHint(List.class)
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @StatusCodes({
        @ResponseCode(code = 201, condition = "created a person")})
    public Response createPerson(
        @TypeHint(Person.class) String personJSON);

    /**
     * Saves changes to an existing Person.
     * @param personID identifies a Person
     * @param personJSON contains personal details
     * @return Contains updated details for a Person.
     */
    @PUT
    @Path(PersonIdPath)
    @TypeHint(Person.class)
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @StatusCodes({
        @ResponseCode(code = 200, condition = "saved a person"),
        @ResponseCode(code = 410, condition = "missing person") })
    public Response savePerson(
        @PathParam(ValueMap.ID) long personID,
        @TypeHint(Person.class) String personJSON);

    /**
     * Gets a registered Person.
     * @param personID identifies a Person
     * @return Contains the details of a Person (if registered).
     */
    @GET
    @Path(PersonIdPath)
    @TypeHint(Person.class)
    @Produces({MediaType.APPLICATION_JSON})
    @StatusCodes({
        @ResponseCode(code = 200, condition = "found a person"),
        @ResponseCode(code = 410, condition = "missing person") })
    public Response getPerson(
        @PathParam(ValueMap.ID) long personID);

    /**
     * Gets a registered Person.
     * @param idType indicates a kind of ID
     * @param personID identifies a Person
     * @return Contains the details of a Person (if registered).
     */
    @GET
    @Path(HashedIdPath)
    @TypeHint(Person.class)
    @Produces({MediaType.APPLICATION_JSON})
    @StatusCodes({
        @ResponseCode(code = 200, condition = "found a person"),
        @ResponseCode(code = 410, condition = "missing person") })
    public Response getPersonWithHash(
        @QueryParam(Type) Contact.Type idType,
        @QueryParam(ValueMap.ID) String personID);

    /**
     * Deletes a registered Person.
     * @param personID identifies a Person
     * @return Indicates whether a Person was deleted.
     */
    @DELETE
    @Path(PersonIdPath)
    @TypeHint(List.class)
    @StatusCodes({
        @ResponseCode(code = 202, condition = "deleted a person"),
        @ResponseCode(code = 410, condition = "missing person") })
    public Response deletePerson(
        @PathParam(ValueMap.ID) long personID);

    /**
     * Deletes a registered Person.
     * @param idType indicates a kind of ID
     * @param personID identifies a Person
     * @return Indicates whether a Person was deleted.
     */
    @DELETE
    @Path(HashedIdPath)
    @TypeHint(List.class)
    @Produces({MediaType.APPLICATION_JSON})
    @StatusCodes({
        @ResponseCode(code = 200, condition = "deleted a person"),
        @ResponseCode(code = 410, condition = "missing person") })
    public Response deletePersonWithHash(
        @QueryParam(Type) Contact.Type idType,
        @QueryParam(ValueMap.ID) String personID);

} // IPersonService
