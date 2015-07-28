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
Using [JAX-RS][jax-rs] annotations, the interface defines the service [endpoint mappings][endpoints] 
for the service methods, thereby providing a RESTful API supporting both JSON and XML payloads.

The service design approach serves as an example of the [Separated Interface][separated-interface] and
[Plugin][plugin-pattern] design patterns.
The service interface is packaged into its own JAR (axiom-faces) so that both the service implementation
and its clients can (depend on and) use the interface. 
See the [diagram](#service-diagram) below to see how these parts relate to each other.

This approach prevents clients from having direct dependence upon the service implementation class.
It also allows (at least) the test clients to use [Spring][spring] + [CXF][apache-cxf] to create 
a proxy based on the interface.
Such a proxy may reference a local service instance or a remote instance without code changes, only changes
in the proxy configuration.

The interface also provides documentation that can be published automatically using the provided 
Java doc comments, esp. using a tool like [Enunciate][enunciate].

#### Axiom Service ####
The service contains a service class: **CustomerFacade**, which implements **ICustomerService**. 
The service also contains a **ServiceController**, which takes advantage of [Spring Boot][spring-boot] 
to create a self-hosting service.
The service API tests also use the **ServiceController** to launch a local service instance for which 
the test then creates a local service proxy using [Spring][spring] + [CXF][apache-cxf] + **ICustomerService**.

#### Axiom Domain ####
The _domain_ package contains classes that demonstrate usage of the idiomatic JPA storage classes.
See the [diagram](#model-diagram) below to see how these parts relate to each other.

| Element | Description |
|---------|-------------|
| Party   | a named composite with some contact information |
| Person  | represents a person -> party |
| Contact | a composite containing various contact mechanisms |
| ContactMechanism | a contact mechanism supporting: phone, email, mail |
| EmailAddress | an email address |
| MailAddress  | a typical USA street address |
| PhoneNumber  | a typical USA phone number |
| PersistenceContext | configures the JPA persistence mechanisms |

Note that **PhoneNumber, EmailAddress, MailAddress, Person** are all derived from **HashedItem**.
As such, they all have associated hash-based lookup queries (see more below).

#### Axiom Storage ####
The _storage_ package contains class that codify idiomatic JPA usage.
See the [diagram](#model-diagram) below to see how these parts relate to each other.

| Element | Description |
|---------|-------------|
| SurrogatedItem        | an item with surrogate keys |
| SurrogatedComposite   | a composite of SurrogatedItems |
| Surrogated&lt;ItemType&gt;  | a SurrogatedItem base class |
| HashedItem            | a SurrogatedItem that hashes its contents |
| Hashed&lt;ItemType&gt; | an HashedItem base class |
| StorageMechanism        | associates a JPA repository with its model type |
| StorageMechanism.Registry  | a storage mechanism registry |

In addition to a surrogate key, each **HashedItem** computes and stores a hash of its contents.
The stored hash provides a simple way to prevent duplicate instances from being stored.
It also provides an additional index on which to search for an item.
Thus, each **HashedItem** is immutable, and guaranteed to be unique within the backing store.

#### Axiom Utils ####
The utility library containing some basic utility classes:

| Element | Description |
|---------|-------------|
| ModelCodec     | converts models to and from JSON and XML using JAXB |
| SpringContext  | loads Spring Beans from a Spring context |
| Symmetric      | encrypts and decrypts payloads using AES |
| ModelValidator | validates a model using the Bean Validation framework |

#### Service Diagram ####
![Axiom Service][axiom-service]

#### Model Diagram ####
![Axiom Models][axiom-models]

[axiom-service]: https://rawgithub.com/nikboyd/axiom-lib-java/master/images/axiom-service.svg "Axiom Service"
[axiom-models]: https://rawgithub.com/nikboyd/axiom-lib-java/master/images/axiom-models.svg "Axiom Models"
[endpoints]: https://github.com/nikboyd/axiom-lib-java/blob/master/axiom-faces/src/main/java/org/axiom_tools/faces/ICustomerService.java#L44
[separated-interface]: http://martinfowler.com/eaaCatalog/separatedInterface.html
[plugin-pattern]: http://martinfowler.com/eaaCatalog/plugin.html
[repository-pattern]: http://martinfowler.com/eaaCatalog/repository.html
[spring-boot]: http://projects.spring.io/spring-boot/
[spring]: http://projects.spring.io/spring-framework/
[jax-rs]: https://docs.oracle.com/javaee/7/api/javax/ws/rs/package-summary.html
[apache-cxf]: http://cxf.apache.org/
[enunciate]: http://enunciate.codehaus.org/
