/*
 * 
 * @author Sebastian Salazar
 * email sebassalazart@hotmail.com
 * 
 * Credits: Code based on Pongo-Java POJO generator for MongoDB, Dr. Dimitris Kolovos
 */
package com.york.cs.couchbaseapi;

import java.util.Iterator;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.york.cs.services.authentication.Profile;

public class ProfileCollection extends DBCollection<Profile> {
	protected static final String DOC_TYPE = Profile.class.getCanonicalName();
	
	
	public ProfileCollection(Database dbCollection) {
		super(dbCollection);
	}
	
	
	@Override
	public Iterator<Profile> iterator() {
	
		Query query = getDBCollectionQuery(dbCollection); 
		QueryEnumerator result;
		try {
			result = query.run();
			Iterator<QueryRow> it = result;
			return new DBCursorIterator<Profile>(this, result);
		} catch (CouchbaseLiteException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public void add(Profile profile) {
		super.add(profile);
	}
	
	public void remove(Profile profile) {
		super.remove(profile);
	}
	
	@Override
	public String getName() {
		return DOC_TYPE;
	}
	
	
	public Iterable<Profile> getAllProfiles(int limit, int skip) throws CouchbaseLiteException{
		
		Query query =getDBCollectionQuery(dbCollection);
		
		if (limit > 0)
			query.setLimit(limit);
		if (skip > 0)
			query.setSkip(limit);
		
		QueryEnumerator result = query.run();
		
		Iterator<QueryRow> it = result;
		DBCursorIterator<Profile> pci = new DBCursorIterator<>(this, it);
		return new IteratorIterable<Profile>(pci);
	}
	
	
	public Iterable<Profile> getProfilesByUserId(
			 int limit, String... ids) throws ConflictException, CouchbaseLiteException {
		
		
		return this.findByKey(Profile.Profile_userId, 0, 0, ids);
		

	}

	public  Iterable<Profile> getProfilesByName(boolean descendingOrder, int limit, String... names) throws ConflictException, CouchbaseLiteException {

		return this.findByKey(Profile.Profile_name, 0, 0, names);
	}

	
	
}