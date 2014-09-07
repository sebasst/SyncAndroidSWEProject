package com.york.cs.todolite2.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.test.ApplicationTestCase;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.SavedRevision;
import com.york.cs.services.authentication.Profile;
import com.york.cs.testproject.test.testutils.DataBaseAdm;
import com.york.cs.todolite2.document2.Application;
import com.york.cs.todolite2.document2.ListTasks;
import com.york.cs.todolite2.document2.Task;
import com.york.cs.todolite2.document2.TodoDB;

public class PerformanceTestQueries extends ApplicationTestCase<Application> {

	Application application;

	// Couchlite database
	private Database database;

	ListTasks listTasks = new ListTasks();
	Task task1 = new Task();
	Task task2 = new Task();

	TodoDB todoDB;

	public PerformanceTestQueries() {
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
		Task task=null;
		for (int i = 0; i < 10; i++) {
			 task = new Task();
			task.setTitle("title10");
			todoDB.getTasks().add(task);
			
		}
		todoDB.sync();
		

	}
	
	public void findTasks() throws CouchbaseLiteException{
		long time0= System.currentTimeMillis();
		Iterable<Task>it =todoDB.getTasks().findByKey(Task.Task_title, 0, 0, "title10");
		Iterator<Task> iterator = it.iterator();
		int i=0;
		while(iterator.hasNext()){
			
			String title = iterator.next().getTitle();
		}
		
		long time1= System.currentTimeMillis();
		
		Iterable<Task>it2 =todoDB.getTasks().findByKey(Task.Task_title, 0, 0, "title10");
		Iterator<Task> iterator2 = it2.iterator();
		
		while(iterator2.hasNext()){
			
			String title = iterator2.next().getTitle();
		}
		long time2= System.currentTimeMillis();
		System.out.println(""+(time1-time0));
		System.out.println(""+(time2-time1));
	}
	


	public void QueryTasks() throws CouchbaseLiteException {
		//type=com.york.cs.todolite2.document2.Task
		long time0= System.currentTimeMillis();
		com.couchbase.lite.View view = database.getView("TaskTitle");
		if (view.getMap() == null) {
			Mapper map = new Mapper() {
				@Override
				public void map(Map<String, Object> document, Emitter emitter) {
					if ("com.york.cs.todolite2.document2.Task".equals(document.get("type"))) {

						emitter.emit(document.get("title"), document);
					}
				}
			};
			view.setMap(map, null);
		}

		Query query = view.createQuery();
		List<Object> keys = new ArrayList<Object>();
		keys.add("title10");
		query.setKeys(keys);
		query.setDescending(true);
		

		QueryEnumerator result = query.run();
		
		for (Iterator<QueryRow> it = result; it.hasNext();) {
			QueryRow row = it.next();
			String title = (String) row.getDocument().getProperty("title");
		}

		long time1= System.currentTimeMillis();
		System.out.println(""+(time1-time0));
	}

	/*
	 * public void findid() throws CouchbaseLiteException { Iterable<ListTasks>
	 * listTasksIterable = todoDB.getListTasks()
	 * .findByKey(ListTasks.ListTasks_title, 0, 0, "L5");
	 * 
	 * System.out.println("tireable size" +
	 * listTasksIterable.iterator().hasNext());
	 * 
	 * ListTasks listTasksL1 = listTasksIterable.iterator().next();
	 * System.out.println(listTasksL1.getId()); }
	 * 
	 * public void findbyid() throws CouchbaseLiteException { Document document
	 * = database .getDocument("d0183b40-1cc6-4a85-905b-1f691b9f4168");
	 * 
	 * todoDB.getListTasks().findById("d0183b40-1cc6-4a85-905b-1f691b9f4168");
	 * 
	 * List<SavedRevision> conf = document.getConflictingRevisions();
	 * System.out.println("conf: " + conf.size());
	 * 
	 * System.out.println(todoDB.getListTasks()
	 * .findById("d0183b40-1cc6-4a85-905b-1f691b9f4168").getTitle());
	 * 
	 * }
	 * 
	 * public void profilechech() {
	 * 
	 * System.out.println(Profile.getCurrentProfile().getDbObject());
	 * List<Profile> profiles = Profile.getAllProfiles(true, 0);
	 * 
	 * System.out.println("profiles size: " + profiles.size());
	 * 
	 * for (Profile profilet : profiles) {
	 * System.out.println(profilet.getUserEmail()); }
	 * 
	 * }
	 */
}
