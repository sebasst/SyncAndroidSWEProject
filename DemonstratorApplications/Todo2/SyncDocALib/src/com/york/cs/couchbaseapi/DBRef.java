/*
 * 
 * @author Sebastian Salazar
 * email sebassalazart@hotmail.com
 * 
 
 */
package com.york.cs.couchbaseapi;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Status;


/**
 * Represents a database reference, which points to an object stored in the
 * database.
 * <p>
 */
public class DBRef implements Serializable {

	private static final long serialVersionUID = 3031885741395465814L;

	/**
	 * Creates a DBRefBase
	 * 
	 * 
	 * @param type
	 *            the namespace where the object is stored
	 * @param id
	 *            the object id
	 */
	public DBRef(DBObject dbObject) {
		Application application = (Application) Application.getContext();
		_db = application.getDatabase();
		_type = (String) dbObject.getDbObject().get("type");
		_id = dbObject.getId();
	}

	/**
	 * @param Type
	 *            type of document
	 * @param id
	 *            object id
	 */
	public DBRef(String type, String id) {
		Application application = (Application) Application.getContext();
		//Application application = (Application) Application.getApplication();
		_db = application.getDatabase();
		_type = type;
		_id = id;
	}

	/**
	 * Creates a DBRef
	 * 
	 * @param db
	 *            the database
	 * @param o
	 *            a BSON object representing the reference
	 */
	public DBRef(Map<String, Object> referenceMap) {
		// super( db , o.get( "$ref" ).toString() , o.get( "$id" ) );
		Application application = (Application) Application.getContext();
		//Application application = (Application) Application.getApplication();
		_db = application.getDatabase();
		if (referenceMap.containsKey("referenceId"))
			_id = (String) referenceMap.get("referenceId");
		else
			_id = null;
		if (referenceMap.containsKey("type"))
			_type = (String) referenceMap.get("type");
		else
			_type = null;
		if (_id == null || _type == null) {
			// TODO throw
			
		}

	}

	/**
	 * For use only with serialization framework.
	 */
	protected DBRef() {
		_id = null;
		_type = null;
		_db = null;
	}

	/**
	 * fetches the object referenced from the database
	 * 
	 * @return the referenced document
	 * @throws Exception
	 * @throws MongoException
	 */
	public Map<String, Object> fetch() throws Exception {
		// TODO Cambiar exception all propierties or used properties?

		
		if (_loadedPointedTo) {
			// return _pointedTo.getProperties();
			Map<String, Object> properties = _pointedTo.getUserProperties();
			properties.put("_id", _pointedTo.getId());
			return properties;
		}
		
		if (_db == null) {
			Application application = (Application) Application.getContext();
			_db = application.getDatabase();
		}

		
		try {
			_pointedTo = getDB().getExistingDocument(_id);
		
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	
		_loadedPointedTo = true;
		
		Map<String, Object> properties = _pointedTo.getUserProperties();
		properties.put("_id", _pointedTo.getId());
		return properties;
		
	}

	public Map<String, Object> fetch(String type) throws Exception {

		fetch();

		if (!_pointedTo.getProperty("type").equals(type)) {
			_loadedPointedTo = false;
			return null;// TODO EXECPTION?
		}
		_loadedPointedTo = true;
		
		Map<String, Object> properties = _pointedTo.getUserProperties();
		properties.put("_id", _pointedTo.getId());
		return properties;
		//return _pointedTo.getProperties();
	}

	/**
	 * @param db
	 *            database
	 * @param type
	 *            type of document
	 * @param id
	 *            id of the document
	 * @return the properties of the referenced document
	 * @throws CouchbaseLiteException
	 */
	public static Map<String, Object> fetch(Database db, String type, String id)
			throws CouchbaseLiteException {

		if (type != null && id != null) {
			Map<String, Object> properties = fetch(db, id);
			if (properties.containsKey("type")) {
				if (type.equals(properties.get("type"))) {
					return properties;

				} else {
					throw new CouchbaseLiteException(
							"Document has no type property", Status.NOT_FOUND);
				}
			} else {
				throw new CouchbaseLiteException(
						"Document is not of the specified type",
						Status.NOT_FOUND);
			}

		}

		throw new CouchbaseLiteException("Invalid parameters",
				Status.BAD_REQUEST);
	}

	/**
	 * @param db
	 *            database
	 * @param type
	 *            type of document
	 * @param id
	 *            id of the document
	 * @return the properties of the referenced document
	 * @throws CouchbaseLiteException
	 */
	public static Map<String, Object> fetch(Database db, String id)
			throws CouchbaseLiteException {

		if (id != null) {
			Document document = db.getExistingDocument(id);
			if (document != null) {
				Map<String, Object> properties = document.getUserProperties();
				properties.put("_id", document.getId());
				return properties;

			} else {
				throw new CouchbaseLiteException(
						"Document does not exists in the database",
						Status.NOT_FOUND);
			}

		}
		throw new CouchbaseLiteException("Invalid parameters",
				Status.BAD_REQUEST);
	}

	@Override
	public String toString() {
		return "{ \"$ref\" : \"" + _type + "\", \"$id\" : \"" + _id + "\" }";
	}

	/**
	 * Gets the object's id
	 * 
	 * @return the id of the reference
	 */
	public Object getId() {
		return _id;
	}

	/**
	 * Gets the object's namespace (collection name)
	 * 
	 * @return the name of the collection in which the reference is stored
	 */
	public String getRef() {
		return _type;
	}

	/**
	 * Gets the database, which may be null, in which case the reference can not
	 * be fetched.
	 * 
	 * @return the database
	 * @see #fetch()
	 */
	public Database getDB() {
		return _db;

		// TODO IS BETTER TO GET DIRECTLY AND NO CALL FOR IT?
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		final DBRef dbRefBase = (DBRef) o;

		if (_id != null ? !_id.equals(dbRefBase._id) : dbRefBase._id != null)
			return false;
		if (_type != null ? !_type.equals(dbRefBase._type)
				: dbRefBase._type != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = _id != null ? _id.hashCode() : 0;
		result = 31 * result + (_type != null ? _type.hashCode() : 0);
		return result;
	}

	public Map<String, Object> getReference() {

		Map<String, Object> reference = new HashMap<String, Object>();
		reference.put("referenceId", _id);
		reference.put("type", _type);
		// reference.put("dbName",_db.getName());
		return reference;
	}

	public String getFullyQualifiedId() {
		// TODO USER TYPE?
		return "" + getRef() + "." + getId().toString();

	}

	final String _id;
	final String _type;
	transient Database _db;

	private transient boolean _loadedPointedTo = false;
	private transient Document _pointedTo;
}