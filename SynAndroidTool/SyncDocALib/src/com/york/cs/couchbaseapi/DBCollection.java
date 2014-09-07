/*
 * 
 * @author Sebastian Salazar
 * email sebassalazart@hotmail.com
 * 
 * Credits: Code based on Pongo-Java POJO generator for MongoDB, Dr. Dimitris Kolovos
 */
package com.york.cs.couchbaseapi;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.Status;
import com.couchbase.lite.UnsavedRevision;
import com.york.cs.couchbaseapi.queryBuilder.MapExplorer;
import com.york.cs.couchbaseapi.queryBuilder.QueryProducer;
import com.york.cs.services.authentication.UserManager;

public abstract class DBCollection<T extends DBObject> implements Iterable<T> {

	protected Database dbCollection;
	protected Set<DBObject> toSave = new HashSet<DBObject>();
	protected Set<DBObject> toDelete = new HashSet<DBObject>();
	protected String currentUserId = null;
	protected Set<String> docToTouch = new HashSet<String>();
	protected Set<String> nodocToTouch = new HashSet<String>();

	public Database getDbCollection() {
		return dbCollection;
	}

	public DBCollection(Database dbCollection) {
		this.dbCollection = dbCollection;
	}

	// TODO
	public void createIndex(String field) {
		// dbCollection.ensureIndex(new BasicDBObject(field, 1), new
		// BasicDBObject("background", true));
	}

	public abstract String getName();

	// public abstract Object getType();

	@SuppressWarnings("unchecked")
	public T findById(String id) {
		Document doc = dbCollection.getExistingDocument(id);

		if (doc == null) {

			return null;
		}

		Map<String, Object> dbObject = doc.getUserProperties();

		dbObject.put("_id", id);

		T t = (T) ClassDBFactory.getInstance().createDBObject(dbObject);
		if (t != null) {
			t.setDBCollection(this);
		}
		return t;
	}

	// *********************************************
	protected void add(DBObject dbObject) {
		
		dbObject.setDBCollection(this);
		toSave.add(dbObject);
		Log.d("DBCollection", "toSave" + toSave.size());
	}

	protected void remove(DBObject dbObject) {
		if (toSave.contains(dbObject)) {
			toSave.remove(dbObject);

		}
		toDelete.add(dbObject);
	}

	public T first() {
		return iterator().next();
	}

	public T second() {
		Iterator<T> iterator = iterator();
		iterator.next();
		return iterator.next();
	}

	// *******save
	public void sync() throws CouchbaseLiteException {

		Application application = ((Application) Application.getContext());

		currentUserId = UserManager.getCurrentUserId(Application.getContext());

		for (DBObject dbObject : toSave) {

			dbObject.preSave();
			save(dbObject);
			
			docToTouch.remove(dbObject.getId());

		}
		toSave.clear();

		for (DBObject dbObject : toDelete) {
			dbObject.preDelete();

			delete(dbObject);
			if (dbObject.referencedBy != null) {
				docToTouch.addAll(dbObject.referencedBy);
			}
			nodocToTouch.remove(dbObject.getId());
		}
		toDelete.clear();
	}

	// /***
	public Set<DBObject> getToSave() {
		return toSave;
	}

	public Set<DBObject> getToDelete() {
		return toDelete;
	}

	public long size() {

		Query query = getDBCollectionQuery(dbCollection);
		try {
			QueryEnumerator result = query.run();

			return result.getCount();
		} catch (CouchbaseLiteException e) {
			e.printStackTrace();

		}

		return 0;

	}

	public void save(DBObject dbObject) throws CouchbaseLiteException {

		Document doc = dbCollection.getExistingDocument(dbObject.getId());
		if (doc != null) {
			updateDocument(dbObject);
		} else
			createDocument(dbObject);

	}

	public void updateDocument(DBObject dbObject) throws CouchbaseLiteException {

		final Map<String, Object> properties = dbObject.getDbObject();
		if (!properties.containsKey("type"))
			throw new CouchbaseLiteException("Type is undefined",
					Status.PRECONDITION_FAILED);
		final Map<String, InputStream> dbObjectAttachments = dbObject
				.getDbObjectAttachments();
		Document doc = dbCollection.getDocument(dbObject.getId());

		doc.update(new Document.DocumentUpdater() {
			@Override
			public boolean update(UnsavedRevision newRevision) {
				Map<String, Object> propertiesOld = newRevision
						.getUserProperties();

				propertiesOld.clear();
				
				
				propertiesOld.putAll(properties);
				if (propertiesOld.containsKey("owner_id")
						&& propertiesOld.get("owner_id") == null) {

					properties.put("owner_id", currentUserId);
				}else if(!properties.containsKey("owner_id")){
					properties.put("owner_id", currentUserId);
				}

				newRevision.setUserProperties(propertiesOld);
				
				if (dbObjectAttachments == null || dbObjectAttachments.isEmpty()) {
					
					return true;
				}
				for (String att : dbObjectAttachments.keySet()) {
					
					InputStream attIn = dbObjectAttachments.get(att);

					if (attIn == null) {
						
						newRevision.removeAttachment(att);

					} else {
						
						newRevision.setAttachment(att, att, attIn);
					}

				}
				return true;
			}
		});
		
		if (dbObject.referencedBy != null) {
			docToTouch.addAll(dbObject.referencedBy);
		}
	}

	public void createDocument(DBObject dbObject) throws CouchbaseLiteException {

		Map<String, Object> properties = dbObject.getDbObject();
		if (!properties.containsKey("type"))
			throw new CouchbaseLiteException("Type is undefined",
					Status.PRECONDITION_FAILED);

		Document doc = dbCollection.getDocument(dbObject.getId());
		if (properties.containsKey("owner_id")
				&& properties.get("owner_id") == null) {
			
			properties.put("owner_id", currentUserId);
		} else if(!properties.containsKey("owner_id")){
			properties.put("owner_id", currentUserId);
		}
		doc.putProperties(properties);
		
		//System.out.println(properties.get("owner_id"));

		createAttachFiles(dbObject);

	}

	public void createAttachFiles(DBObject dbObject)
			throws CouchbaseLiteException {
		
		Map<String, InputStream> dbObjectAttachments = dbObject
				.getDbObjectAttachments();

		
		
		if (dbObjectAttachments == null || dbObjectAttachments.isEmpty()) {
			
			return;
		}
		for (String att : dbObjectAttachments.keySet()) {
			
			InputStream attIn = dbObjectAttachments.get(att);

			if (attIn == null) {
			
				dbObject.removeAttachment(att);

			} else {
			
				dbObject.addAttachment(att, attIn, att);
			}

		}

	}

	public void delete(DBObject dbObject) throws CouchbaseLiteException {
		final Map<String, Object> properties = dbObject.getDbObject();
		if (!properties.containsKey("type"))
			throw new CouchbaseLiteException("Type is undefined",
					Status.PRECONDITION_FAILED);

		Document doc = dbCollection.getDocument(dbObject.getId());

		doc.update(new Document.DocumentUpdater() {
			@Override
			public boolean update(UnsavedRevision newRevision) {
				newRevision.setIsDeletion(true);
				Map<String, Object> propertiesOld = newRevision
						.getUserProperties();
				propertiesOld.clear();
				propertiesOld.putAll(properties);
				propertiesOld.put("owner_id", currentUserId);
				newRevision.setUserProperties(propertiesOld);
				return true;
			}
		});

	}

	protected Query getDBCollectionQuery(Database database) {

		com.couchbase.lite.View view = database.getView(getName());
		if (view.getMap() == null) {
			Mapper map = new Mapper() {
				@Override
				public void map(Map<String, Object> document, Emitter emitter) {

					if (getName().equals(document.get("type"))) {
						emitter.emit(document.get("_id"), null);

					}
				}
			};
			view.setMap(map, null);
		}

		return view.createQuery();
	}

	public Iterable<T> findByInterval(QueryProducer queryProducer, int limit,
			int skip, Object startKey, Object endKey)
			throws CouchbaseLiteException {

		Query query = buildQuery(dbCollection, queryProducer);

		query.setStartKey(startKey);
		query.setEndKey(endKey);

		if (limit > 0)
			query.setLimit(limit);
		if (skip > 0)
			query.setSkip(limit);

		QueryEnumerator result = query.run();

		// TODO SIMPLIFICAR
		Iterator<QueryRow> it = result;
		DBCursorIterator<T> pci = new DBCursorIterator<>(this, it);

		return new IteratorIterable<T>(pci);
	}

	public Iterable<T> findByKey(QueryProducer queryProducer, int limit,
			int skip, Object... keys) throws CouchbaseLiteException {

		// TODO ORDER METEN EN QUERY PRODUCER

		Query query = buildQuery(dbCollection, queryProducer);

		if (keys != null) {
			List<Object> listkeys = new ArrayList<Object>();
			for (Object o : keys) {
				if (o instanceof DBObject) {
					listkeys.add(((DBObject) o).getId());
				} else {
					listkeys.add(o);
				}

			}
			query.setKeys(listkeys);
		}
		if (limit > 0)
			query.setLimit(limit);
		if (skip > 0)
			query.setSkip(limit);

		QueryEnumerator result = query.run();

		Iterator<QueryRow> it = result;
		DBCursorIterator<T> pci = new DBCursorIterator<>(this, it);

		return new IteratorIterable<T>(pci);
	}

	public int countByInterval(QueryProducer queryProducer, int limit,
			int skip, Object startKey, Object endKey)
			throws CouchbaseLiteException {

		Query query = buildQuery(dbCollection, queryProducer);

		query.setStartKey(startKey);
		query.setEndKey(endKey);

		if (limit > 0)
			query.setLimit(limit);
		if (skip > 0)
			query.setSkip(limit);

		QueryEnumerator result = query.run();

		return result.getCount();
	}

	public int countByKey(QueryProducer queryProducer, int limit, int skip,
			Object... keys) throws CouchbaseLiteException {

		// TODO ORDER METEN EN QUERY PRODUCER

		Query query = buildQuery(dbCollection, queryProducer);

		if (keys != null) {
			List<Object> listkeys = new ArrayList<Object>();
			for (Object o : keys) {
				if (o instanceof DBObject) {
					listkeys.add(((DBObject) o).getId());
				} else {
					listkeys.add(o);
				}

			}
			query.setKeys(listkeys);
		}
		if (limit > 0)
			query.setLimit(limit);
		if (skip > 0)
			query.setSkip(limit);

		QueryEnumerator result = query.run();

		return result.getCount();
	}

	protected Query buildQuery(Database database,
			final QueryProducer queryProducer) {
		com.couchbase.lite.View view = database.getView(queryProducer
				.getAddress());

		if (view.getMap() == null) {
			Mapper map = new Mapper() {

				@Override
				public void map(Map<String, Object> document, Emitter emitter) {
					System.out
							.println("****new mapper " + document.get("type"));

					if (getName().equals(document.get("type"))) {

						List<Object> listKeys = new ArrayList<Object>();

						MapExplorer.find(document,
								queryProducer.getReducedAddress(), listKeys);

						for (Object o : listKeys)

							if (listKeys != null && listKeys.size() > 0) {
								if (queryProducer.isRepeatedEntries()) {

									Set<Object> set = new HashSet<Object>(
											listKeys);

									for (Object obj : set) {

										if (obj instanceof HashMap<?, ?>) {
											HashMap<String, Object> map = (HashMap<String, Object>) obj;
											if (map.containsKey("_id")) {
												emitter.emit(map.get("_id"),
														null);
											} else if (map
													.containsKey("referenceId")) {
												emitter.emit(
														map.get("referenceId"),
														null);
											} else {
												emitter.emit(obj, null);
											}
										} else {
											emitter.emit(obj, null);
										}
									}
								} else {
									for (Object obj : listKeys) {

										if (obj instanceof HashMap<?, ?>) {
											HashMap<String, Object> map = (HashMap<String, Object>) obj;
											if (map.containsKey("_id")) {
												emitter.emit(map.get("_id"),
														null);
											} else if (map
													.containsKey("referenceId")) {
												emitter.emit(
														map.get("referenceId"),
														null);
											} else {
												emitter.emit(obj, null);
											}
										} else {

											emitter.emit(obj, null);
										}

									}
								}
							}

					}
				}
			};
			view.setMap(map, null);
		}

		return view.createQuery();
	}
}
