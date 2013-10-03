axiom-lib-java
==============

A library of utility classes and tools for Java.

The storage package includes classes that codify idiomatic JPA usage.

<table cellpadding="0" cellspacing="0" width="700">
	 <tr>
	 <td valign="TOP" width="25%">SurrogatedItem</td>
	 <td align="LEFT">
		a standard interface for items with surrogate keys
	 </td>
	 </tr><tr>
	 <td valign="TOP" width="25%">Surrogated&lt;ItemType&#gt;</td>
	 <td align="LEFT">
		a base class that implements SurrogatedItem
	 </td>
	 </tr><tr>
	 <td valign="TOP" width="25%">SurrogatedComposite</td>
	 <td align="LEFT">
		a standard interface for surrogated composite items with surrogated components
	 </td>
	 </tr><tr>
	 <td valign="TOP" width="25%">HashedItem</td>
	 <td align="LEFT">
		a standard interface for surrogated items which hash their contents
	 </td>
	 </tr><tr>
	 <td valign="TOP" width="25%">Hashed&#lt;ItemType&#gt;</td>
	 <td align="LEFT">
		a base class that implements HashedItem
	 </td>
	 </tr><tr>
	 <td valign="TOP" width="25%">ItemRepository</td>
	 <td align="LEFT">
		a utility class for idiomatic JPA persistence
	 </td>
	 </tr><tr>
	 <td valign="TOP" width="25%">TransactionalContext</td>
	 <td align="LEFT">
		a utility class for idiomatic JPA transaction usage
	 </td>
	 </tr><tr>
	 <td valign="TOP" width="25%">Hashed&#lt;ItemType&#gt;</td>
	 <td align="LEFT">
		a base class that implements HashedItem
	 </td>
	 </tr>
</table>

The domain package includes example classes that demonstrate usage of the storage classes.

StreetAddress - a typical USA street address

EmailAddress - an email address

PhoneNumber - a typical USA phone number

Contact - a composite of the foregoing contact types

