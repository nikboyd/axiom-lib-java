package org.axiom.tools.storage;

public interface HashedItem extends SurrogatedItem {
	
	int getHashKey();
	void prepareHash();

} // HashedItem
