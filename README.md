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

#### Sample Service Build and Launch ####

Clone the sources from GitHub. Then, in the cloned project folder, run the following Maven build.

```
mvn -U clean install
```

The various library modules and tests will build, and produce an executable JAR in the **target** folder.
From the project base folder, launch the service with:

```
java -jar axiom-service/target/*.jar
```

The sample service will launch via Spring Boot. Then, you can browse the service API docs at:

```
http://localhost:9001/docs/
```

An example instance of this service is hosted in [OpenShift][open-shift] [here][axiom-shift].


#### Axiom Faces ####
The _faces_ package contains a service interface definition: **IPersonService**. 
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
The service contains a service class: **PersonFacade**, which implements **IPersonService**. 
The service also contains a **ServiceController**, which takes advantage of [Spring Boot][spring-boot] 
to create a self-hosting service.
The service API tests also use the **ServiceController** to launch a local service instance for which 
the test then creates a local service proxy using [Spring][spring] + [CXF][apache-cxf] + **IPersonService**.

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
The _storage_ package contains several classes that codify idiomatic JPA usage.
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

Items persisted into a SQL backing store will typically use a surrogate key for their primary keys and identity.
The **Surrogated&lt;ItemType&gt;** base class factors this common design pattern out into a base class.
Each **SurrogatedItem** may also be marked as a **SurrogatedComposite** by implementing that interface.
A **SurrogatedComposite** cooperates with the persistence layer to coordinate the persistence of its components, 
which will also typically be **SurrogatedItem**s.

Certain kinds of persisted items will typically want storage of a single unique immutable instance given 
its contents.
Examples of this kind of item include phone numbers, email addresses, mailing addresses, account numbers, 
and other forms of unique identifiers, esp. those issued to a person by an institution with which that person 
will maintain a durable (and sometimes perishable) relationship.
To support this kind of design pattern, the library includes the **Hashed&lt;ItemType&gt;** base class.
In addition to a surrogate key, each **Hashed&lt;ItemType&gt;** computes and stores a hash of its contents.
Lookup of the stored hash provides a simple way to prevent duplicate instances from being stored.
It also provides an additional index on which to search for an item given its contents.
This provides the associated persistence layer a consistent way to guarantee uniqueness within the backing store,
beyond that provided by surrogate keys.

The **StorageMechanism** class serves as a generic mechanism for associating a persisted model class with its
JPA storage interface definition. Then, a collection of these mechanisms into a **StorageMechanism.Registry**
provides a way to bootstrap the creation of the associated persistence layer classes at runtime using 
standard Spring dependency injection.

#### Axiom Utils ####
The utility library containing some basic utility classes:

| Element | Description |
|---------|-------------|
| ModelCodec     | converts models to and from JSON and XML using JAXB |
| SpringContext  | loads Spring Beans from a Spring context |
| Symmetric      | encrypts and decrypts payloads using AES |
| ModelValidator | validates a model using the Bean Validation framework |

See the associated test for examples of how to use these utility classes.

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
[open-shift]: https://www.openshift.com/
[axiom-shift]: http://demo-axioms.rhcloud.com/docs/
