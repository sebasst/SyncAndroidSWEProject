package com.york.cs.todolite2.test;

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
		System.out.println("*********add task TESTING");

		Iterable<ListTasks> listTasksIterable = todoDB.getListTasks()
				.findByKey(ListTasks.ListTasks_title, 0, 0, "L1");

		System.out.println("tireable size"
				+ listTasksIterable.iterator().hasNext());

		ListTasks listTasksL1 = listTasksIterable.iterator().next();

		System.out.println("map " + listTasksL1.getDbObject());

		System.out.println("tasks" + listTasksL1.getTasks().size());

		System.out.println("tasks"
				+ listTasksL1.getTasks().iterator().hasNext());

		Task newtask = new Task();
		newtask.setTitle("desde consola");
		System.out.println("add task **************************1");

		System.out.println("add task **************************2");
		//newtask.setListTask(listTasksL1);
		listTasksL1.getTasks().add(newtask);
		todoDB.getTasks().add(newtask);
		System.out.println("add task **************************3");

		listTasksL1.getTasks().add(newtask);
		todoDB.getListTasks().add(listTasksL1);

		System.out
				.println("add task *********" + listTasksL1.getTasks().size());

		for (Task tt : listTasksL1.getTasks()) {
			System.out.println("tasks contained");
			// System.out.println("tasks contained"+tt.getTitle() );
		}

		System.out.println(listTasksL1.getTasks().iterator().next().getTitle());

		todoDB.sync(true);
		System.out.println(database.getDocument(listTasksL1.getId())
				.getProperties());

	}

	public void findid() throws CouchbaseLiteException {
		Iterable<ListTasks> listTasksIterable = todoDB.getListTasks()
				.findByKey(ListTasks.ListTasks_title, 0, 0, "L5");

		System.out.println("tireable size"
				+ listTasksIterable.iterator().hasNext());

		ListTasks listTasksL1 = listTasksIterable.iterator().next();
		System.out.println(listTasksL1.getId());
	}
	
	public void findbyid() throws CouchbaseLiteException {
		Document document= database.getDocument("d0183b40-1cc6-4a85-905b-1f691b9f4168");
		
		todoDB.getListTasks().findById("d0183b40-1cc6-4a85-905b-1f691b9f4168");
		
		List<SavedRevision> conf= document.getConflictingRevisions();
		System.out.println("conf: "+conf.size());
		
		System.out.println(todoDB.getListTasks().findById("d0183b40-1cc6-4a85-905b-1f691b9f4168").getTitle());
		
	}
	
	public void profilechech(){
		
		System.out.println(Profile.getCurrentProfile().getDbObject());
		List<Profile> profiles=Profile.getAllProfiles(true, 0);
		
		System.out.println("profiles size: "+profiles.size());
		
		for(Profile profilet: profiles){
			System.out.println(profilet.getUserEmail());
		}
		
		
		
		
		
	}

	
	
}
