package com.york.cs.todolite2.document2;

import java.util.Iterator;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.york.cs.couchbaseapi.DBCollection;
import com.york.cs.couchbaseapi.DBCursorIterator;
import com.york.cs.couchbaseapi.ClassDBFactory;

public class ListTasksCollection extends DBCollection<ListTasks> {
	protected static final String DOC_TYPE = ListTasks.class.getCanonicalName();
	
	public ListTasksCollection(Database dbCollection) {
		super(dbCollection);
	}
	
	
	@Override
	public Iterator<ListTasks> iterator() {
		
		Query query = getDBCollectionQuery(dbCollection); // TODO DEPENDENCIA
		QueryEnumerator result;
		try {
			result = query.run();
			Iterator<QueryRow> it = result;
			return new DBCursorIterator<ListTasks>(this, result);
		} catch (CouchbaseLiteException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public void add(ListTasks listTasks) {
		super.add(listTasks);
	}
	
	public void remove(ListTasks listTasks) {
		super.remove(listTasks);
	}
	
	@Override
	public String getName() {
		return DOC_TYPE;
	}
}