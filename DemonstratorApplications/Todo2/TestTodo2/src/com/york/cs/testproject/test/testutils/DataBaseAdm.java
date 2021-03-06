package com.york.cs.testproject.test.testutils;

import java.util.Iterator;

import junit.framework.TestCase;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.util.Log;


public class DataBaseAdm extends TestCase {

	




	

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
				row.getDocument().purge();
		}

		result = query.run();

		System.out.println("Total Docs: " + result.getCount());
		Log.d("cleanDB", "Total Docs: " + result.getCount());
	}
}
