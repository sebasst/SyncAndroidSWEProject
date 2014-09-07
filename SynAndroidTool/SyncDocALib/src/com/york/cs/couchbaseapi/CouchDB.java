/*
 * 
 * @author Sebastian Salazar
 * email sebassalazart@hotmail.com
 * 
 * Credits: Code based on Pongo-Java POJO generator for MongoDB, Dr. Dimitris Kolovos
 */
package com.york.cs.couchbaseapi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.UnsavedRevision;

public class CouchDB {

	protected Database db;
	@SuppressWarnings("rawtypes")
	protected List<DBCollection> dbCollections = new ArrayList<DBCollection>();
	protected Set<String> docToTouch = new HashSet<String>();
	protected Set<String> nodocToTouch = new HashSet<String>();

	protected boolean clearDBCacheOnSync = false;

	public CouchDB() {
	}

	public CouchDB(Database db) {
		setDb(db);
	}

	public void setDb(Database db) {
		this.db = db;
	}

	@SuppressWarnings("rawtypes")
	public List<DBCollection> getDBCollections() {
		return dbCollections;
	}

	@SuppressWarnings("unchecked")
	public void sync(boolean clearDBCache) throws CouchbaseLiteException {
		for (@SuppressWarnings("rawtypes")
		DBCollection c : dbCollections) {

			c.sync();
			docToTouch.addAll(c.docToTouch);
			nodocToTouch.removeAll(c.nodocToTouch);

			//

		}

		docToTouch.removeAll(nodocToTouch);
		this.touchDocuments();
		if (clearDBCache) {
			ClassDBFactory.getInstance().clear();
		}
	}

	/**
	 * increase document version
	 * 
	 * @throws CouchbaseLiteException
	 */
	private void touchDocuments() throws CouchbaseLiteException {

		System.out.println("-----------------Touch " + this.docToTouch.size());

		for (String docId : this.docToTouch) {
			Document doc = db.getExistingDocument(docId);
			if (doc != null) {
				doc.update(new Document.DocumentUpdater() {
					@Override
					public boolean update(UnsavedRevision newRevision) {
						Map<String, Object> properties = newRevision
								.getUserProperties();
						newRevision.setUserProperties(properties);
						return true;
					}
				});
			}
		}

	}

	public void setClearDBCacheOnSync(boolean clearDBCacheOnSync) {
		this.clearDBCacheOnSync = clearDBCacheOnSync;
	}

	public boolean isClearDBCacheOnSync() {
		return clearDBCacheOnSync;
	}

	public void sync() throws CouchbaseLiteException {
		sync(clearDBCacheOnSync);
	}

	public DBCollection getCollectionByName(String collectionName) {

		for (DBCollection collection : getDBCollections()) {
			if (collectionName.equals(collection.getName()))
				return collection;
		}

		return null;
	}

}
