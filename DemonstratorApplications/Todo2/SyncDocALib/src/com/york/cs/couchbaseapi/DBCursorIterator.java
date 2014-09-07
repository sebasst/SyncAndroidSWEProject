/*
 * 
 * @author Sebastian Salazar
 * email sebassalazart@hotmail.com
 * 
 * Credits: Code based on Pongo-Java POJO generator for MongoDB, Dr. Dimitris Kolovos
 */
package com.york.cs.couchbaseapi;

import java.util.Iterator;
import java.util.Map;

import com.couchbase.lite.Document;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;


public class DBCursorIterator<T> implements Iterator<T> {
	
	Iterator<QueryRow> dbCursor ;
	protected DBCollection dbCollection;
	
	public DBCursorIterator(DBCollection dbCollection, Iterator<QueryRow> dbCursor) {
		this.dbCursor = dbCursor;
		this.dbCollection = dbCollection;
	}
	
	public void setDbCursor(QueryEnumerator dbCursor) {
		this.dbCursor = dbCursor;
	}
	
	@Override
	public boolean hasNext() {
		return dbCursor.hasNext();
	}

	@Override
	public T next() {
		
		QueryRow row = dbCursor.next();  //TODO SIMPLIFY WITH NO VARIABLES
		Document doc = row.getDocument();
		Map<String, Object> properties = doc.getUserProperties();
		properties.put("_id", doc.getId());
		DBObject next = (DBObject) ClassDBFactory.getInstance().createDBObject(properties);
		next.setDBCollection(dbCollection); //TODO COLLECTION DEPENDANCY
		return (T) next;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	
}
