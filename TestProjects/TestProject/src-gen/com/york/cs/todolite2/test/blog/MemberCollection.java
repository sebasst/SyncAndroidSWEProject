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

public class MemberCollection extends DBCollection<Member> {
	protected static final String DOC_TYPE = Member.class.getCanonicalName();
	
	public MemberCollection(Database dbCollection) {
		super(dbCollection);
	}
	
	
	@Override
	public Iterator<Member> iterator() {
		
		Query query = getDBCollectionQuery(dbCollection); // TODO DEPENDENCIA
		QueryEnumerator result;
		try {
			result = query.run();
			Iterator<QueryRow> it = result;
			return new DBCursorIterator<Member>(this, result);
		} catch (CouchbaseLiteException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public void add(Member member) {
		super.add(member);
	}
	
	public void remove(Member member) {
		super.remove(member);
	}
	
	@Override
	public String getName() {
		return DOC_TYPE;
	}
}