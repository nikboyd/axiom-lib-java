axiom-lib-java
==============

A library of utility classes and tools for Java.

The domain package contains classes that codify a typical model for contacts.

The storage package includes class that codify idiomatic JPA usage.

| Element | Description |
|---------|-------------|
| SurrogatedItem        | a standard interface for items with surrogate keys |
| Surrogated(ItemType)  | a base class that implements SurrogatedItem |


<table cellpadding="0" cellspacing="0" width="750">
	 <tr>
	 <td valign="TOP" colspan="2">
	 	The storage package includes classes that codify idiomatic JPA usage.
	 </td>
	 </tr><tr>
	 <td valign="TOP" width="30%">SurrogatedItem</td>
	 <td align="LEFT">
		a standard interface for items with surrogate keys
	 </td>
	 </tr><tr>
	 <td valign="TOP" width="30%">Surrogated&lt;ItemType&gt;</td>
	 <td align="LEFT">
		a base class that implements SurrogatedItem
	 </td>
	 </tr><tr>
	 <td valign="TOP" width="30%">SurrogatedComposite</td>
	 <td align="LEFT">
		a standard interface for composite items with surrogated components
	 </td>
	 </tr><tr>
	 <td valign="TOP" width="30%">HashedItem</td>
	 <td align="LEFT">
		a standard interface for surrogated items which hash their contents
	 </td>
	 </tr><tr>
	 <td valign="TOP" width="30%">Hashed&lt;ItemType&gt;</td>
	 <td align="LEFT">
		a base class that implements HashedItem
	 </td>
	 </tr><tr>
	 <td valign="TOP" width="30%">ItemRepository</td>
	 <td align="LEFT">
		a utility class for idiomatic JPA persistence
	 </td>
	 </tr><tr>
	 <td valign="TOP" width="30%">TransactionalContext</td>
	 <td align="LEFT">
		a utility class for idiomatic JPA transaction usage
	 </td>
	 </tr><tr>
	 <td valign="TOP" width="30%">Hashed&lt;ItemType&gt;</td>
	 <td align="LEFT">
		a base class that implements HashedItem
	 </td>
	 </tr>
	 <tr>
	 <td valign="TOP" colspan="2">
		The domain package includes example classes that demonstrate idiomatic usage of the storage classes.
	 </td>
	 </tr><tr>
	 <td valign="TOP" width="30%">StreetAddress</td>
	 <td align="LEFT">
 		a typical USA street address
	 </td>
	 </tr><tr>
	 <td valign="TOP" width="30%">EmailAddress</td>
	 <td align="LEFT">
		an email address
	 </td>
	 </tr><tr>
	 <td valign="TOP" width="30%">PhoneNumber</td>
	 <td align="LEFT">
		a typical USA phone number
	 </td>
	 </tr><tr>
	 <td valign="TOP" width="30%">Contact</td>
	 <td align="LEFT">
 		a composite of the foregoing contact types
	 </td>
	 </tr>
</table>

