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
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.couchbase.lite.Attachment;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Revision;
import com.couchbase.lite.Status;
import com.couchbase.lite.UnsavedRevision;

public class DBObject {

	protected DBObject container; // protected
	protected String containingFeature = ""; // protected
	protected Map<String, Object> dbObject; // protected

	protected DBCollection dbCollection = null; // protected
	protected java.util.List<String> memberIds = null;
	protected String sharingStatus = null;
	protected List<String> referencedBy = null;

	// protected List<List<Object>> dbObjectAttachments;
	
	protected Map<String, InputStream> dbObjectAttachments=new HashMap<String, InputStream>();
	protected List<String> attachmentsList = null;

	public DBObject() {
		dbObject = new HashMap<String, Object>();
		setId(UUID.randomUUID().toString());
		setType(this.getClass().getCanonicalName());
		

	}

	public DBObject(Map<String, Object> dbObject) {
		this.dbObject = dbObject;

	}

	public Map<String, Object> getDbObject() {
		return dbObject;
	}
	
	public Map<String, InputStream> getDbObjectAttachments() {
		return dbObjectAttachments;
	}

	public String getId() {
		return dbObject.get("_id") + "";
	}

	private void setId(String id) {
		dbObject.put("_id", id);
	}

	private void setType(String type) {
		dbObject.put("type", type);
	}

	protected void setSuperTypes(String... supers) {
		// To be overridden.
		dbObject.put("superTypesClass", supers);

	}

	public DBObject getContainer() {
		return container;
	}

	public void setContainer(DBObject container) {
		this.container = container;
		if (container == null) {
			this.containingFeature = "";
		}
	}

	public String getContainingFeature() {
		return containingFeature;
	}

	public DBCollection getDBCollection() {
		return dbCollection;
	}

	public void setDBCollection(DBCollection dBCollection) {
		
	
		
		this.dbCollection = dBCollection;
	}

	// TODO SET AOWNER

	public void addMember(String memberId) {
		if (sharingStatus != null && sharingStatus.equals("shareable")) {
			if (memberIds == null) {
				if (dbObject.containsKey("membersId")) {
					memberIds = (List<String>) dbObject.get("membersId");
				} else {
					memberIds = new ArrayList<String>();
				}
			}
			if (memberIds.contains(memberId))
				return;
			else {
				memberIds.add(memberId);
				dbObject.put("membersId", memberIds);
				notifyChanged();
			}
		} else {
			throw new UnsupportedOperationException(
					"This object is not shareable");
		}
	}

	public void removeMember(String memberId) {
		if (sharingStatus != null && sharingStatus.equals("shareable")) {
			if (memberIds == null) {
				if (dbObject.containsKey("membersId")) {
					memberIds = (List<String>) dbObject.get("membersId");
				} else {
					return;
				}
			}
			if (memberIds.contains(memberId)) {
				memberIds.remove(memberId);
				dbObject.put("membersId", memberIds);
				notifyChanged();
			} else {
				return;

			}
		} else {
			throw new UnsupportedOperationException(
					"This object is not shareable");
		}

	}

	
	public String getPath() {
		if (container != null) {
			return container.getPath() + "." + containingFeature + "."
					+ getId();
		} else {
			return "";
		}
	}

	@SuppressWarnings("unchecked")
	protected void addReferencedBy(String _id) {
		System.out.println("************************ add referencedby");
		if (referencedBy == null) {
			if (dbObject.get("referencedBy") != null)
				referencedBy = (List<String>) dbObject.get("referencedBy");
			else
				referencedBy = new ArrayList<String>();
		}

		referencedBy.add(_id);
		dbObject.put("referencedBy",referencedBy);
	}

	@SuppressWarnings("unchecked")
	public void removeReferencedBy(String _id) {
		if (referencedBy == null) {
			if (dbObject.get("referencedBy") != null)
				referencedBy = (List<String>) dbObject.get("referencedBy");
			else
				return;
		}
		referencedBy.remove(_id);
	}

	public void setContainingFeature(String containingFeature) {
		this.containingFeature = containingFeature;
	}

	protected void createReference(String name, DBObject to) {
		dbObject.put(name, to.createDBRef());
		to.addReferencedBy(getId());

	}

	// public DBRef createDBRef() {
	public Map<String, Object> createDBRef() {
	
		if (!isReferencable()) {
			throw new IllegalStateException(
					"Attempted to create DBRef for non-referenceable object "
							+ this);
		}

		DBRef dbref = new DBRef(this.getClass().getCanonicalName(), getId());
		// TODO MEJOR TYPE?===<<< add referenced by
		return dbref.getReference();
	}

	protected DBObject resolveReference(String name) {
		return ClassDBFactory.getInstance()
				.resolveReference(dbObject.get(name));

	}

	public boolean isReferencable() {
		
		
		return (this.getContainer() == null && this.getDBCollection() != null);
	}

	protected void notifyChanged() {

		if (container != null) {
			container.notifyChanged();

		} else if (getDBCollection() != null) {

			getDBCollection().add(this);
		}
	}

	public void preSave() {

	}

	public void preDelete() {
	}

	protected String parseString(String str, String def) {
		if (str == null)
			return def;
		else
			return str;
	}

	protected int parseInteger(String str, int def) {
		if (str == null)
			return def;
		else {
			try {
				return Integer.parseInt(str);
			} catch (Exception ex) {
				return def;
			}
		}
	}

	protected long parseLong(String str, long def) {
		if (str == null)
			return def;
		else {
			try {
				return Long.parseLong(str);
			} catch (Exception ex) {
				return def;
			}
		}
	}

	protected float parseFloat(String str, float def) {
		if (str == null)
			return def;
		else {
			try {
				return Float.parseFloat(str);
			} catch (Exception ex) {
				return def;
			}
		}
	}

	protected double parseDouble(String str, double def) {
		if (str == null)
			return def;
		else {
			try {
				return Double.parseDouble(str);
			} catch (Exception ex) {
				return def;
			}
		}
	}

	protected boolean parseBoolean(String str, boolean def) {
		if (str == null || "null".equals(str))
			return def;
		else {
			try {
				return Boolean.parseBoolean(str);
			} catch (Exception ex) {
				return def;
			}
		}
	}

	protected InputStream loadAttachment(String attachmentName)
			throws CouchbaseLiteException {
		
		
		Application application = (Application) Application.getContext();
		Database database = application.getDatabase();
		Document doc = database.getExistingDocument(getId());
		if (doc == null) {
			return null;
		}
		Revision rev = doc.getCurrentRevision();
		Attachment att = rev.getAttachment(attachmentName);
		if (att != null) {
			
			InputStream is = att.getContent();
			return is;
		} else
			
			return null;
	}
	
	
	

	protected void addAttachment(String name, InputStream contentStream,
			String contentType) throws CouchbaseLiteException {
		
		Application application = (Application) Application.getContext();
		Database database = application.getDatabase();

		// Add or update an image to a document as a JPEG attachment:
		// Document doc = database.getDocument(getId());
		Document doc = database.getExistingDocument(getId());
		if (doc == null) {
			
			throw new CouchbaseLiteException(
					"Document must be persisted on DB before attaching file to it",
					Status.PRECONDITION_FAILED);

		}
		UnsavedRevision newRev = doc.getCurrentRevision().createRevision();
		
		newRev.setAttachment(name, contentType, contentStream);
		newRev.save();
		
		doc = database.getExistingDocument(getId());
		
		Revision rev = doc.getCurrentRevision();
		
		
		for(String names:rev.getAttachmentNames()){
			System.out.println("------ post set add attachment list name: "+names);
		}
	}

	protected void removeAttachment(String name) throws CouchbaseLiteException {
		Application application = (Application) Application.getContext();
		Database database = application.getDatabase();
		Document doc = database.getDocument(getId());
		UnsavedRevision newRev = doc.getCurrentRevision().createRevision();
		newRev.removeAttachment(name);
		// (You could also update newRev.properties while you're here)
		newRev.save();
	}

	protected List<String> getAttachmentList() {

		if (this.attachmentsList == null) {
			Application application = (Application) Application.getContext();
			Database database = application.getDatabase();
			Document doc = database.getDocument(getId());
			Revision rev = doc.getCurrentRevision();
			this.attachmentsList = rev.getAttachmentNames();
		}
		return this.attachmentsList;

	}
}
