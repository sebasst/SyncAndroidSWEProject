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

public class PostCollection extends DBCollection<Post> {
	protected static final String DOC_TYPE = Post.class.getCanonicalName();
	
	public PostCollection(Database dbCollection) {
		super(dbCollection);
	}
	
	
	@Override
	public Iterator<Post> iterator() {
		
		Query query = getDBCollectionQuery(dbCollection); // TODO DEPENDENCIA
		QueryEnumerator result;
		try {
			result = query.run();
			Iterator<QueryRow> it = result;
			return new DBCursorIterator<Post>(this, result);
		} catch (CouchbaseLiteException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public void add(Post post) {
		super.add(post);
	}
	
	public void remove(Post post) {
		super.remove(post);
	}
	
	@Override
	public String getName() {
		return DOC_TYPE;
	}
}