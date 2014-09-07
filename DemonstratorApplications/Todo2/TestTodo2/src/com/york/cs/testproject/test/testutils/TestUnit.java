package com.york.cs.testproject.test.testutils;

import java.util.List;

import android.content.res.Resources.Theme;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.ApplicationTestCase;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Revision;
import com.couchbase.lite.SavedRevision;
import com.york.cs.services.authentication.Profile;
import com.york.cs.testproject.test.testutils.DataBaseAdm;
import com.york.cs.todolite2.document2.Application;
import com.york.cs.todolite2.document2.ListTasks;
import com.york.cs.todolite2.document2.Task;
import com.york.cs.todolite2.document2.TodoDB;

import junit.framework.TestCase;

public class TestUnit extends ApplicationTestCase<Application> {

	Application application;

	// Couchlite database
	private Database database;

	ListTasks listTasks = new ListTasks();
	Task task1 = new Task();
	Task task2 = new Task();

	TodoDB todoDB;

	public TestUnit() {
		super(Application.class);

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createApplication();
		application = getApplication();

		database = application.getDatabase();

		// DataBaseAdm.cleanDB(database);

		todoDB = application.getTodoDB();

	}

	public void CreateObjectsDB() throws CouchbaseLiteException {
		Document document = database.getDocument("aa939128-ca7f-4b4a-a8d2-539c531944ad");
		
		System.out.println(document.getProperties());
	
	}

	
	
}
