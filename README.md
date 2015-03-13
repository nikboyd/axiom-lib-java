axiom-lib-java
==============

A library of utility classes and tools for Java, along with examples of their use.

The faces package contains a service interface definition: **ICustomerService**. 
The service interface is a central organizational element, addressing several important
aspects of a service design.
Using JAX-RS annotations, the interface defines the service endpoint mappings for the service methods,
thereby providing a RESTful API supporting both JSON and XML payloads.

The service design approach serves as an example of the [Separated Interface][separated-interface] and
[Plugin][plugin-pattern] design patterns.
The service interface is packaged into its own JAR so that both the service implementation
and its clients can (depend on and) use the interface. 
This approach prevents clients from having direct dependence upon the service implementation class.
It also allows (at least) the test clients to use **Spring + CXF** to create a proxy based on the interface.
Such a proxy may reference a local service instance or a remote instance without code changes, only changes
in the proxy configuration.

The interface also provides documentation that can be published automatically using the provided 
Java doc comments, esp. using a tool like [Enunciate][enunciate].

The facades package contains a service class: **CustomerFacade**, which implements **ICustomerService**. 
The server package contains a ServiceController, which takes advantage of [Spring Boot][spring-boot] 
to create a self-hosting service.
The service API tests also use the ServiceController to launch a local service instance for which 
the test then creates a local service proxy using **Spring + CXF + ICustomerService**.

The domain package contains classes that demonstrate idiomatic usage of the storage classes (below).

| Element | Description |
|---------|-------------|
| Party   | a named composite with some contact information |
| Person  | represents a person -> party |
| Contact | a composite containing various contact mechanisms |
| ContactMechanism&lt;MechanismType&gt; | a generic contact mechanism |
| EmailAddress | an email address |
| MailAddress  | a typical USA street address |
| PhoneNumber  | a typical USA phone number |

The storage package contains class that codify idiomatic JPA usage.

| Element | Description |
|---------|-------------|
| SurrogatedItem        | a standard interface for items with surrogate keys |
| SurrogatedComposite   | a standard interface for composites of surrogated components |
| Surrogated&lt;ItemType&gt;  | a base class that implements SurrogatedItem  |
| HashedItem            | a standard interface for surrogates that hash their contents |
| Hashed&lt;ItemType&gt; | a base class that implements HashedItem |
| ItemRepository        | a utility class for idiomatic JPA persistence |
| TransactionalContext  | a utility class for idiomatic JPA transaction usage |


[separated-interface]: http://martinfowler.com/eaaCatalog/separatedInterface.html
[plugin-pattern]: http://martinfowler.com/eaaCatalog/plugin.html
[enunciate]:
[spring-boot]:
