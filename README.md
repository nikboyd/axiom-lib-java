axiom-lib-java
==============

A library of utility classes and tools for Java, along with examples of their use.

#### Overview ####
This library contains the following packages:

| Library | Contents |
|---------|----------|
| axiom-utils | contains basic utility classes |
| axiom-storage | contains idiomatic JPA storage classes |
| axiom-domain  | contains thematic contact information classes |
| axiom-faces   | contains a thematic service interface |
| axiom-service | contains a thematic service facade |

#### Axiom Faces ####
The _faces_ package contains a service interface definition: **ICustomerService**. 
The service interface is a central organizational element, addressing several important
aspects of a service design.
Using [JAX-RS][jax-rs] annotations, the interface defines the service endpoint mappings for the service methods,
thereby providing a RESTful API supporting both JSON and XML payloads.

The service design approach serves as an example of the [Separated Interface][separated-interface] and
[Plugin][plugin-pattern] design patterns.
The service interface is packaged into its own JAR so that both the service implementation
and its clients can (depend on and) use the interface. 
This approach prevents clients from having direct dependence upon the service implementation class.
It also allows (at least) the test clients to use [Spring][spring] + [CXF][apache-cxf] to create 
a proxy based on the interface.
Such a proxy may reference a local service instance or a remote instance without code changes, only changes
in the proxy configuration.

The interface also provides documentation that can be published automatically using the provided 
Java doc comments, esp. using a tool like [Enunciate][enunciate].

#### Axiom Service ####
The _facades_ package contains a service class: **CustomerFacade**, which implements **ICustomerService**. 
The _server_ package contains a **ServiceController**, which takes advantage of [Spring Boot][spring-boot] 
to create a self-hosting service.
The service API tests also use the **ServiceController** to launch a local service instance for which 
the test then creates a local service proxy using [Spring][spring] + [CXF][apache-cxf] + **ICustomerService**.

#### Axiom Domain ####
The _domain_ package contains classes that demonstrate usage of the idiomatic JPA storage classes.

| Element | Description |
|---------|-------------|
| Party   | a named composite with some contact information |
| Person  | represents a person -> party |
| Contact | a composite containing various contact mechanisms |
| ContactMechanism | a generic contact mechanism supporting: phone, email, mail |
| EmailAddress | an email address |
| MailAddress  | a typical USA street address |
| PhoneNumber  | a typical USA phone number |

#### Axiom Storage ####
The _storage_ package contains class that codify idiomatic JPA usage.

| Element | Description |
|---------|-------------|
| SurrogatedItem        | a standard interface for items with surrogate keys |
| SurrogatedComposite   | a standard interface for composites of surrogated components |
| Surrogated&lt;ItemType&gt;  | a base class that implements SurrogatedItem  |
| HashedItem            | a standard interface for surrogates that hash their contents |
| Hashed&lt;ItemType&gt; | a base class that implements HashedItem |
| ItemRepository        | a utility class for idiomatic JPA persistence |
| TransactionalContext  | a utility class for idiomatic JPA transaction usage |

#### Axiom Utils ####
The utility library contains some basic utility classes:

| Element | Description |
|---------|-------------|
| ModelCodec     | a generic class for converting models to and from JSON and XML using JAXB |
| SpringContext  | loads Spring Beans from a Spring context |
| Symmetric      | encrypts and decrypts payloads using AES |
| ModelValidator | validates a model using the Bean Validation framework |


[separated-interface]: http://martinfowler.com/eaaCatalog/separatedInterface.html
[plugin-pattern]: http://martinfowler.com/eaaCatalog/plugin.html
[spring-boot]: http://projects.spring.io/spring-boot/
[spring]: http://projects.spring.io/spring-framework/
[jax-rs]: https://docs.oracle.com/javaee/7/api/javax/ws/rs/package-summary.html
[apache-cxf]: http://cxf.apache.org/
[enunciate]: http://enunciate.codehaus.org/
