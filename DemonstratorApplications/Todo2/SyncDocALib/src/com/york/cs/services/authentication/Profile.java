package com.york.cs.services.authentication;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.Status;
import com.york.cs.couchbaseapi.Application;
import com.york.cs.couchbaseapi.DBObject;
import com.york.cs.couchbaseapi.queryBuilder.QueryProducer;

public class Profile extends DBObject {

	@SuppressWarnings("unused")
	private static final String TAG = "Profile";

	private static final String DOC_TYPE = Profile.class.getCanonicalName();

	public final static QueryProducer Profile_userId = new QueryProducer(
			Profile.class.getCanonicalName() + ".userId",
			Profile.class.getCanonicalName());
	public final static QueryProducer Profile_name = new QueryProducer(
			Profile.class.getCanonicalName() + ".name",
			Profile.class.getCanonicalName());

	private Profile() {
		super();
		sharingStatus = "shareable";
	}

	public Profile(String name, String userId) {
		super();
		sharingStatus = "shareable";
		setUserId(userId);
		setName(name);

	}

	public String getUserId() {
		return parseString(dbObject.get("userId") + "", "");
	}

	public void setUserId(String userId) {
		dbObject.put("userId", userId);

	}

	public String getUserEmail() {
		return parseString(dbObject.get("userEmail") + "", "");
	}

	public void setUserEmail(String userEmail) {
		dbObject.put("userEmail", userEmail);

	}

	public String getName() {
		return parseString(dbObject.get("name") + "", "");
	}

	public void setName(String name) {
		dbObject.put("userId", name);
	}

	public void saveProfile() throws CouchbaseLiteException {
		if (getName() == null || getUserId() == null)
			throw new CouchbaseLiteException(
					"user name and user id cannot be null",
					Status.PRECONDITION_FAILED);

		Application application = ((Application) Application.getContext());

		Database database = application.getDatabase();

		if (database == null)
			throw new CouchbaseLiteException("database not initiated",
					Status.DB_ERROR);

		Document document = database.getDocument(getId());

		document.putProperties(getDbObject());
	}

	public void deleteProfile(Database database) throws CouchbaseLiteException {

		Document document = ((Application) Application.getContext())
				.getDatabase().getDocument(getId());

		document.delete();

	}

	// queries

	public static Profile getById(Database database, String _id) {

		Document document = ((Application) Application.getContext())
				.getDatabase().getDocument(_id);
		Profile profile = new Profile();
		profile.dbObject = document.getUserProperties();
		profile.dbObject.put("_id", document.getId());

		return profile;
	}

	public static Profile getCurrentProfile() {

		Application application = ((Application) Application.getContext());
		Document document = application.getDatabase().getDocument(
				UserManager.getCurrentUserProfileId(application
						.getApplicationContext()));
		Profile profile = new Profile();
		profile.dbObject = document.getUserProperties();
		profile.dbObject.put("_id", document.getId());

		return profile;
	}

	public static List<Profile> getProfilesByUserId(Database database,
			java.util.List<Object> keys, Object startKey, Object endKey,
			boolean descendingOrder, int limit) {

		return getQuery(database, "byUserId", keys, startKey, endKey,
				descendingOrder, limit);

	}

	public static List<Profile> getProfilesByName(Database database,
			java.util.List<Object> keys, Object startKey, Object endKey,
			boolean descendingOrder, int limit) {

		return getQuery(database, "byName", keys, startKey, endKey,
				descendingOrder, limit);

	}

	public static List<Profile> getAllProfiles(boolean descendingOrder, int limit) {
		Application application = ((Application) Application.getContext());
		return getQuery(application.getDatabase(), "byUserName", null, null, null,
				descendingOrder, limit);

	}

	private static List<Profile> getQuery(Database database, String queryName,
			java.util.List<Object> keys, Object startKey, Object endKey,
			boolean descendingOrder, int limit) {
		Query query;
		switch (queryName) {
		case "byUserName":
			query = getQueryByName(database);
			break;
		case "byUserId":
			query = getQueryByUserId(database);
			break;

		default:
			throw new IllegalArgumentException("query not defined");

		}

		List<Profile> profiles = new ArrayList<Profile>();

		if (limit > 0)
			query.setLimit(limit);
		if (descendingOrder)
			query.setDescending(true);

		if (keys != null) {
			query.setKeys(keys);
		} else {
			query.setStartKey(startKey);
			query.setEndKey(endKey);

		}

		try {
			QueryEnumerator result = query.run();

			if (result != null && result.getCount() > 0) {

				profiles = queryEnumeratorToList(result);

			}

		} catch (CouchbaseLiteException e) {

			e.printStackTrace();
		}
		return profiles;

	}

	private static Query getQueryByUserId(Database database) {
		com.couchbase.lite.View view = database.getView(DOC_TYPE + ".byUserId");
		if (view.getMap() == null) {
			Mapper map = new Mapper() {
				@Override
				public void map(Map<String, Object> document, Emitter emitter) {
					if (DOC_TYPE.equals(document.get("type"))) {
						emitter.emit(document.get("userId"), null);
					}
				}
			};
			view.setMap(map, null);
		}

		Query query = view.createQuery();

		return query;
	}

	private static Query getQueryByName(Database database) {
		com.couchbase.lite.View view = database.getView(DOC_TYPE + ".byName");
		if (view.getMap() == null) {
			Mapper map = new Mapper() {
				@Override
				public void map(Map<String, Object> document, Emitter emitter) {
					if (DOC_TYPE.equals(document.get("type"))) {
						emitter.emit(document.get("name"), null);
					}
				}
			};
			view.setMap(map, null);
		}

		Query query = view.createQuery();
		return query;
	}

	private static List<Profile> queryEnumeratorToList(QueryEnumerator result) {

		List<Profile> profiles = new ArrayList<Profile>();
		for (Iterator<QueryRow> it = result; it.hasNext();) {
			QueryRow row = it.next();
			Profile profile = new Profile();
			profile.dbObject = row.getDocument().getUserProperties();
			profile.dbObject.put("_id", row.getDocumentId());
			profiles.add(profile);

		}

		return profiles;
	}

}
