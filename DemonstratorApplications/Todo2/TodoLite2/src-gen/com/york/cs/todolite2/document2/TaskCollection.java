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

public class TaskCollection extends DBCollection<Task> {
	protected static final String DOC_TYPE = Task.class.getCanonicalName();
	
	public TaskCollection(Database dbCollection) {
		super(dbCollection);
	}
	
	
	@Override
	public Iterator<Task> iterator() {
		
		Query query = getDBCollectionQuery(dbCollection); // TODO DEPENDENCIA
		QueryEnumerator result;
		try {
			result = query.run();
			Iterator<QueryRow> it = result;
			return new DBCursorIterator<Task>(this, result);
		} catch (CouchbaseLiteException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public void add(Task task) {
		super.add(task);
	}
	
	public void remove(Task task) {
		super.remove(task);
	}
	
	@Override
	public String getName() {
		return DOC_TYPE;
	}
}