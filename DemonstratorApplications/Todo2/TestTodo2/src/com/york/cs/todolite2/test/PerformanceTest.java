package com.york.cs.todolite2.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.test.ApplicationTestCase;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.SavedRevision;
import com.york.cs.services.authentication.Profile;
import com.york.cs.testproject.test.testutils.DataBaseAdm;
import com.york.cs.todolite2.document2.Application;
import com.york.cs.todolite2.document2.ListTasks;
import com.york.cs.todolite2.document2.Task;
import com.york.cs.todolite2.document2.TodoDB;

public class PerformanceTest extends ApplicationTestCase<Application> {

	Application application;

	// Couchlite database
	private Database database;

	ListTasks listTasks = new ListTasks();
	Task task1 = new Task();
	Task task2 = new Task();

	TodoDB todoDB;

	public PerformanceTest() {
		super(Application.class);

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createApplication();

		application = getApplication();

		database = application.getDatabase();

		todoDB = application.getTodoDB();

	}

	public void cleanDatabase() throws CouchbaseLiteException {
		DataBaseAdm.cleanDB(database);
	}

	public void CreateObjectsDB() throws CouchbaseLiteException {

		for (int i = 0; i < 1000; i++) {
			Task task = new Task();
			task.setTitle("title" + i);
			todoDB.getTasks().add(task);
		}
		todoDB.sync();

	}

	public void CreateDocumentsDB() throws CouchbaseLiteException {

		for (int i = 0; i < 10; i++) {
			Document doc = database.createDocument();
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put("type", "task");
			properties.put("title", "title"+i);
			properties.put("owner", "user1@h.com");
			properties.put("members", new ArrayList<String>());
			Document document = database.createDocument();
			document.putProperties(properties); 
		}
		

	}
/*
	public void findid() throws CouchbaseLiteException {
		Iterable<ListTasks> listTasksIterable = todoDB.getListTasks()
				.findByKey(ListTasks.ListTasks_title, 0, 0, "L5");

		System.out.println("tireable size"
				+ listTasksIterable.iterator().hasNext());

		ListTasks listTasksL1 = listTasksIterable.iterator().next();
		System.out.println(listTasksL1.getId());
	}

	public void findbyid() throws CouchbaseLiteException {
		Document document = database
				.getDocument("d0183b40-1cc6-4a85-905b-1f691b9f4168");

		todoDB.getListTasks().findById("d0183b40-1cc6-4a85-905b-1f691b9f4168");

		List<SavedRevision> conf = document.getConflictingRevisions();
		System.out.println("conf: " + conf.size());

		System.out.println(todoDB.getListTasks()
				.findById("d0183b40-1cc6-4a85-905b-1f691b9f4168").getTitle());

	}

	public void profilechech() {

		System.out.println(Profile.getCurrentProfile().getDbObject());
		List<Profile> profiles = Profile.getAllProfiles(true, 0);

		System.out.println("profiles size: " + profiles.size());

		for (Profile profilet : profiles) {
			System.out.println(profilet.getUserEmail());
		}

	}
*/
}
