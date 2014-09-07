package com.york.cs.testproject.test.testApi;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import android.test.ApplicationTestCase;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.util.Log;
import com.york.cs.todolite2.test.blog.Application;



public class CreationTest extends ApplicationTestCase<Application> {

	Application application;

	// Couchlite database
	private Database database;

	public CreationTest() {
		super(Application.class);

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createApplication();
		application = getApplication();

		database = application.getDatabase();
	}

	public void Test1() throws CouchbaseLiteException {
		 String id=UUID.randomUUID().toString();

		 System.out.println(id);

		String id2 = database.generateDocumentId();

		System.out.println("****************" + id2);

		Document doc = database.getDocument(id);
		Map<String, Object> pro = new HashMap<String, Object>();
		pro.put("1", 1);
		pro.put("2", 2);
		if (doc == null) {
			android.util.Log.d("test1", "doc null");
		} else {
		
		System.out.println(doc.getId());
		doc.putProperties(pro);
		
			
			pro=	doc.getUserProperties();
			//pro = doc.getProperties();
			Set<String> setKeys=pro.keySet();
			for(String s:setKeys){
				System.out.println(s);
			}
		}

		
		Document doc2 =database.getDocument(id);
		
		pro=	doc2.getProperties();
		//pro = doc.getProperties();
		Set<String> setKeys=pro.keySet();
		for(String s:setKeys){
			System.out.println(s);
		}
	}

	public void cleanDB() throws CouchbaseLiteException {
		Query query = database.createAllDocumentsQuery();
		query.setAllDocsMode(Query.AllDocsMode.ALL_DOCS);
		QueryEnumerator result = query.run();

		System.out.println("Total Docs: " + result.getCount());

		Log.d("cleanDB", "Total Docs: " + result.getCount());

		for (Iterator<QueryRow> it = result; it.hasNext();) {
			// Log.w("MYAPP", "iterator: ");
			QueryRow row = it.next();
			// if (row.getConflictingRevisions().size() > 0) {
			Log.w("MYAPP", "Conflict in document: %s", row.getDocumentId());

			System.out.println("delete: "
					+ database.getDocument(row.getDocumentId()).delete());
			// }
		}

		result = query.run();

		System.out.println("Total Docs: " + result.getCount());
		Log.d("cleanDB", "Total Docs: " + result.getCount());
	}
}