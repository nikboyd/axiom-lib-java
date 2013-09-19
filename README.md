axiom-lib-java
==============

A library of utility classes and tools for Java.

The storage package includes classes that codify idiomatic JPA usage.

SurrogatedItem - a standard interface for items with surrogate keys
Surrogated<ItemType> - a base class that implements SurrogatedItem
SurrogatedComposite - a standard interface for surrogated composite items with surrogated components
HashedItem = a standard interface for surrogated items which hash their contents
Hashed<ItemType> - a base class that implements HashedItem
ItemRepository - a utility class for idiomatic JPA persistence
TransactionalContext - a utility class for idiomatic JPA transaction usage

The domain package includes example classes that demonstrate usage of the storage classes.

StreetAddress - a typical USA street address
EmailAddress - an email address
PhoneNumber - a typical USA phone number
Contact - a composite of the foregoing contact types