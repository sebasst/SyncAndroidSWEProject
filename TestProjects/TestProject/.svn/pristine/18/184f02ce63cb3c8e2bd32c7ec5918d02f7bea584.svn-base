package com.york.cs.todolite2.test.blog;

import java.util.Iterator;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.york.cs.couchbaseapi.DBCollection;
import com.york.cs.couchbaseapi.DBCursorIterator;
import com.york.cs.couchbaseapi.ClassDBFactory;

public class AuthorCollection extends DBCollection<Author> {
	protected static final String DOC_TYPE = Author.class.getCanonicalName();
	
	public AuthorCollection(Database dbCollection) {
		super(dbCollection);
	}
	
	
	@Override
	public Iterator<Author> iterator() {
		
		Query query = getDBCollectionQuery(dbCollection); // TODO DEPENDENCIA
		QueryEnumerator result;
		try {
			result = query.run();
			Iterator<QueryRow> it = result;
			return new DBCursorIterator<Author>(this, result);
		} catch (CouchbaseLiteException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public void add(Author author) {
		super.add(author);
	}
	
	public void remove(Author author) {
		super.remove(author);
	}
	
	@Override
	public String getName() {
		return DOC_TYPE;
	}
}