
package com.york.cs.swe.todolite.document;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.UnsavedRevision;
import com.york.cs.swe.todolite.Application;

public class Task {
    private static final String VIEW_NAME = "tasks";
    private static final String DOC_TYPE = "task";

    public static Query getQuery(Database database, String listDocId) {
        com.couchbase.lite.View view = database.getView(VIEW_NAME);
        if (view.getMap() == null) {
            Mapper map = new Mapper() {
                @Override
                public void map(Map<String, Object> document, Emitter emitter) {
                    if (DOC_TYPE.equals(document.get("type"))) {
                        java.util.List<Object> keys = new ArrayList<Object>();
                        keys.add(document.get("list_id"));
                        keys.add(document.get("created_at"));
                        emitter.emit(keys, document);
                    }
                }
            };
            view.setMap(map, null);
        }

        Query query = view.createQuery();
        query.setDescending(true);

        java.util.List<Object> startKeys = new ArrayList<Object>();
        startKeys.add(listDocId);
        startKeys.add(new HashMap<String, Object>());

        java.util.List<Object> endKeys = new ArrayList<Object>();
        endKeys.add(listDocId);

        query.setStartKey(startKeys);
        query.setEndKey(endKeys);

        return query;
    }

    public static Document createTask(Database database,
                                      String title,
                                      Bitmap image,
                                      String listId) throws CouchbaseLiteException {
        //SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        //Calendar calendar = GregorianCalendar.getInstance();
        //String currentTimeString = dateFormatter.format(calendar.getTime());

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("type", DOC_TYPE);
        properties.put("title", title);
        properties.put("checked", Boolean.FALSE);
        //properties.put("created_at", currentTimeString);
        properties.put("list_id", listId);

        Document document = database.createDocument();

        UnsavedRevision revision = document.createRevision();
        revision.setUserProperties(properties);

        if (image != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 50, out);
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            revision.setAttachment("image", "image/jpg", in);
        }

        revision.save();

        if(listId!=null){
        	updateContainingList(listId);
        }
        return document;
    }

    public static void attachImage(Document task, Bitmap image) throws CouchbaseLiteException {
        if (task == null || image == null) return;

        UnsavedRevision revision = task.createRevision();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 50, out);
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        revision.setAttachment("image", "image/jpg", in);

        revision.save();
    }

    public static void updateCheckedStatus(Document task, boolean checked)
            throws CouchbaseLiteException {
        Map<String, Object> properties = new HashMap<String, Object>();
       
        properties.put("checked", checked);
        updateTask(task, properties);
    }

    
    public static void updateTitle(Document task, String title)
            throws CouchbaseLiteException {
        Map<String, Object> properties = new HashMap<String, Object>();
       
        properties.put("title", title);
        updateTask(task, properties);
    }
    
    
    public static void updateTask(Document task, final Map<String, Object> newProperties)
            throws CouchbaseLiteException {
    	
    	
    	task.update(new Document.DocumentUpdater() {
    	    @Override
    	    public boolean update(UnsavedRevision newRevision) {
    	        Map<String, Object> properties = newRevision.getUserProperties();
    	        properties.putAll(newProperties);
    	        newRevision.setUserProperties(properties);
    	        
    	        String listId=(String) properties.get("list_id");
    	        
    	        if(listId!=null){
    	        	updateContainingList(listId);
    	        }
    	        return true;
    	    }
    	});
    }
    
    public static void addMemberToTask(Document task, String userId)
            throws CouchbaseLiteException {
        Map<String, Object> newProperties = new HashMap<String, Object>();
        newProperties.putAll(task.getProperties());

        @SuppressWarnings("unchecked")
		java.util.List<String> members = (java.util.List<String>) newProperties.get("membersId");
        if (members == null) members = new ArrayList<String>();
        members.add(userId);
        newProperties.put("membersId", members);

        try {
            task.putProperties(newProperties);
        } catch (CouchbaseLiteException e) {
            com.couchbase.lite.util.Log.e(Application.TAG, "Cannot add member to the list", e);
        }
    }
    public static void deleteTask(Document task) throws CouchbaseLiteException {
        task.delete();
    }
    
    private static void updateContainingList(String listId){
    	
	        
	        if(listId!=null){
	        	try {
					List.updateVersion(listId);
				} catch (CouchbaseLiteException e) {
					e.printStackTrace();
				}
	        }
    }
}
