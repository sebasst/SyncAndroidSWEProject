/*
 * 
 * @author Sebastian Salazar
 * email sebassalazart@hotmail.com
 * 
 * Credits: Code based on Pongo-Java POJO generator for MongoDB, Dr. Dimitris Kolovos
 */
package com.york.cs.couchbaseapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.ReferenceMap;

public class ClassDBFactory {

	protected static ClassDBFactory instance = new ClassDBFactory();
	public ReferenceMap cache = null;// TODO protected
	protected List<FactoryContributor> contributors = new ArrayList<FactoryContributor>();

	// singleton

	private ClassDBFactory() {
		cache = new ReferenceMap(ReferenceMap.HARD, ReferenceMap.SOFT);
		// TODO QUITAR CONTRIBUTOR
		getContributors().add(new ClasspathFactoryContributor());
	}

	public static ClassDBFactory getInstance() {
		return instance;
	}

	// TODO
	public DBObject createDBObject(Map<String, Object> dbObject) {
		return createDBObject(dbObject, null);
	}

	public void clear() {
		cache.clear();
	}

	// TODO CAMBIAR A MAP
	public DBObject resolveReference(Object ref) {
		Map<String, Object> refMap = (Map<String, Object>) ref;

		// TODO REFF?
		if (refMap.containsKey("type") && refMap.containsKey("referenceId")) {
			DBRef dbRef = new DBRef((Map<String, Object>) ref);
			String fullyQualifiedId = dbRef.getFullyQualifiedId();// TODO CHECK
																	// QUALIFIED
																	// NAME

			DBObject dbObjectT = (DBObject) cache.get(fullyQualifiedId);
			if (dbObjectT == null) {
				// TODO GET DATABASE DIRECTLY?
				Map<String, Object> dbObject;
				try {

					dbObject = dbRef.fetch();

					if (dbObject != null) {

						dbObjectT = createDBObject(dbObject, dbRef.getRef());
					}
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
			return dbObjectT;
		} else {
			if (ref == null)
				System.out.println("------> resolve referenc  ref null");
			return null;
		}

	}

	public DBObject createDBObject(Map<String, Object> dbObject, String type) {
		if (dbObject == null)
			return null;
		try {
			String fullyQualifieId = getFullyQualifiedId(dbObject);

			DBObject dbObjecT2 = (DBObject) cache.get(fullyQualifieId);
			if (fullyQualifieId == null || dbObjecT2 == null) {
				String className = (String) dbObject.get("type");
				dbObjecT2 = createDBObject(className);
				dbObjecT2.dbObject = dbObject;
				if (fullyQualifieId != null) {

					cache.put(fullyQualifieId, dbObjecT2);

				}
			}
			return dbObjecT2;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	protected String getFullyQualifiedId(Map<String, Object> dbObject) {
		if (!dbObject.containsKey("type"))
			return null; // verify that type se crea en objecto
		return dbObject.get("type") + "." + dbObject.get("_id");
	}

	protected DBObject createDBObject(String className) throws Exception {
		for (FactoryContributor contributor : contributors) {
			if (contributor.canCreate(className)) {
				return contributor.create(className);
			}
		}

		throw new RuntimeException("Could not create DBObject for class "
				+ className);

	}

	public List<FactoryContributor> getContributors() {
		return contributors;
	}
}
