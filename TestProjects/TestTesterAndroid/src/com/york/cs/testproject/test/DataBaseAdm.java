package com.york.cs.testproject.test;

import java.util.Iterator;

import android.test.ApplicationTestCase;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.util.Log;


import com.york.cs.todolite2.test.blog.Application;

import junit.framework.TestCase;

public class DataBaseAdm extends ApplicationTestCase<Application> {

	Application application;

	// Couchlite database
	private Database database;

	public DataBaseAdm() {
		super(Application.class);

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createApplication();
		application = getApplication();

		database = application.getDatabase();
	}

	public void cleanDB() throws CouchbaseLiteException {
		Query query = database.createAllDocumentsQuery();
		query.setAllDocsMode(Query.AllDocsMode.ALL_DOCS);
		QueryEnumerator result = query.run();

		System.out.println("Total Docs: " + result.getCount());
		
		Log.d("cleanDB", "Total Docs: " + result.getCount());

		for (Iterator<QueryRow> it = result; it.hasNext();) {
			//Log.w("MYAPP", "iterator: ");
			QueryRow row = it.next();
			//if (row.getConflictingRevisions().size() > 0) {
				Log.w("MYAPP", "Conflict in document: %s", row.getDocumentId());

				System.out.println("delete: "+database.getDocument(row.getDocumentId()).delete());
			//}
		}

		result = query.run();

		System.out.println("Total Docs: " + result.getCount());
		Log.d("cleanDB", "Total Docs: " + result.getCount());
	}

	public static void cleanDB(Database db) throws CouchbaseLiteException {
		Query query = db.createAllDocumentsQuery();
		query.setAllDocsMode(Query.AllDocsMode.ALL_DOCS);
		QueryEnumerator result = query.run();

		System.out.println("Total Docs: " + result.getCount());
		
		Log.d("cleanDB", "Total Docs: " + result.getCount());

		for (Iterator<QueryRow> it = result; it.hasNext();) {
			//Log.w("MYAPP", "iterator: ");
			QueryRow row = it.next();
			//if (row.getConflictingRevisions().size() > 0) {
				Log.w("MYAPP", "Conflict in document: %s", row.getDocumentId());

				System.out.println("delete: "+db.getDocument(row.getDocumentId()).delete());
			//}
		}

		result = query.run();

		System.out.println("Total Docs: " + result.getCount());
		Log.d("cleanDB", "Total Docs: " + result.getCount());
	}
}
