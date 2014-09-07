/**
 * 
 */

package com.york.cs.swe.todolite.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.UnsavedRevision;
import com.york.cs.swe.todolite.Application;

public class List {
	private static final String VIEW_NAME = "lists";
	private static final String DOC_TYPE = "list";

	public static Query getQuery(Database database) {
		com.couchbase.lite.View view = database.getView(VIEW_NAME);
		if (view.getMap() == null) {
			Mapper mapper = new Mapper() {
				public void map(Map<String, Object> document, Emitter emitter) {
					String type = (String) document.get("type");
					if (DOC_TYPE.equals(type)) {
						emitter.emit(document.get("title"), document);
					}
				}
			};
			view.setMap(mapper, null);
		}

		Query query = view.createQuery();
		return query;
	}

	public static Document createNewList(Database database, String title,
			String userId) throws CouchbaseLiteException {
		// SimpleDateFormat dateFormatter = new
		// SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		// Calendar calendar = GregorianCalendar.getInstance();
		// String currentTimeString = dateFormatter.format(calendar.getTime());

		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("type", "list");
		properties.put("title", title);
		// properties.put("created_at", currentTimeString);
		properties.put("owner_id", userId);
		properties.put("membersId", new ArrayList<String>());

		Document document = database.createDocument();
		document.putProperties(properties);

		return document;
	}

	public static void assignOwnerToListsIfNeeded(Database database,
			Document user) throws CouchbaseLiteException {
		QueryEnumerator enumerator = getQuery(database).run();

		if (enumerator == null)
			return;

		while (enumerator.hasNext()) {
			Document document = enumerator.next().getDocument();

			String owner = (String) document.getProperty("owner_id");
			if (owner != null)
				continue;

			Map<String, Object> properties = new HashMap<String, Object>();
			properties.putAll(document.getProperties());
			properties.put("owner_id", user.getId());
			document.putProperties(properties);
		}
	}

	public static void addMemberToList(Document list, String userId)
			throws CouchbaseLiteException {
		Map<String, Object> newProperties = new HashMap<String, Object>();
		newProperties.putAll(list.getProperties());

		@SuppressWarnings("unchecked")
		java.util.List<String> members = (java.util.List<String>) newProperties
				.get("membersId");
		if (members == null)
			members = new ArrayList<String>();
		members.add(userId);
		newProperties.put("membersId", members);

		try {
			list.putProperties(newProperties);
		} catch (CouchbaseLiteException e) {
			com.couchbase.lite.util.Log.e(Application.TAG,
					"Cannot add member to the list", e);
		}
	}

	public static void removeMemberFromList(Document list, Document user)
			throws CouchbaseLiteException {
		Map<String, Object> newProperties = new HashMap<String, Object>();
		newProperties.putAll(list.getProperties());

		@SuppressWarnings("unchecked")
		java.util.List<String> members = (java.util.List<String>) newProperties
				.get("membersId");
		if (members != null)
			members.remove(user.getId());
		newProperties.put("membersId", members);

		list.putProperties(newProperties);
	}

	public static void updateTitle(Document list, String title)
			throws CouchbaseLiteException {
		Map<String, Object> properties = new HashMap<String, Object>();

		properties.put("title", title);
		updateList(list, properties);
	}

	public static void updateList(Document list,
			final Map<String, Object> newProperties)
			throws CouchbaseLiteException {

		list.update(new Document.DocumentUpdater() {
			@Override
			public boolean update(UnsavedRevision newRevision) {
				Map<String, Object> properties = newRevision
						.getUserProperties();
				properties.putAll(newProperties);
				newRevision.setUserProperties(properties);
				return true;
			}
		});
	}

	public static void updateVersion(Document list)
			throws CouchbaseLiteException {

		list.update(new Document.DocumentUpdater() {
			@Override
			public boolean update(UnsavedRevision newRevision) {
				Map<String, Object> properties = newRevision
						.getUserProperties();
				newRevision.setUserProperties(properties);
				return true;
			}
		});
	}
	
	public static void updateVersion(String listId)
			throws CouchbaseLiteException {

		Application application =(Application) Application.getContext();
		Document list = application.getDatabase().getExistingDocument(listId);
		if(list==null) return;
		list.update(new Document.DocumentUpdater() {
			@Override
			public boolean update(UnsavedRevision newRevision) {
				Map<String, Object> properties = newRevision
						.getUserProperties();
				newRevision.setUserProperties(properties);
				return true;
			}
		});
	}

	public static void deleteTaskVersion(Document list)
			throws CouchbaseLiteException {

		list.delete();
	}

}
