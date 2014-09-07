/*
 * 
 * @author Sebastian Salazar
 * email sebassalazart@hotmail.com
 * 
 * Credits: Code based on Pongo-Java POJO generator for MongoDB, Dr. Dimitris Kolovos
 */
package com.york.cs.couchbaseapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class DBListIterator<T extends DBObject> implements Iterator<T>{
	
	protected Iterator<HashMap<String, Object>> dbListIterator = null; 
	protected DBList<T> dbList = null;
	
	public DBListIterator(DBList<T> dbList) {
		this.dbList = dbList;
		dbListIterator=dbList.dbList.iterator();
	}
		
	@Override
	public boolean hasNext() {
		return dbListIterator.hasNext();
	}

	@Override
	public T next() {
		return dbList.wrap(dbListIterator.next());
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
}
