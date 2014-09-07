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
import com.york.cs.testproject.test.testutils.DataBaseAdm;

import com.york.cs.todolite2.document2.Application;
import com.york.cs.todolite2.document2.ListTasks;
import com.york.cs.todolite2.document2.Task;
import com.york.cs.todolite2.document2.TodoDB;

import junit.framework.TestCase;

public class TodoTest extends ApplicationTestCase<Application> {

	Application application;

	// Couchlite database
	private Database database;

	ListTasks listTasks = new ListTasks();
	Task task1 = new Task();
	Task task2 = new Task();

	TodoDB todoDB;

	public TodoTest() {
		super(Application.class);

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createApplication();
		application = getApplication();

		database = application.getDatabase();

		DataBaseAdm.cleanDB(database);

		todoDB = application.getTodoDB();
		listTasks.setTitle("listtitle");

		task1.setTitle("task1");
		task2.setTitle("task2");

		todoDB.getListTasks().add(listTasks);
		todoDB.getTasks().add(task1);
		todoDB.getTasks().add(task2);

		todoDB.sync(true);

	}

	public void CreateObjectsDB() throws CouchbaseLiteException {
		/* FR-06 */

		// exists object2 in db
		assertTrue(database.getExistingDocument(listTasks.getId())
				.getProperty("title").equals(listTasks.getTitle()));
		assertTrue(database.getExistingDocument(task1.getId())
				.getProperty("title").equals(task1.getTitle()));

		assertNotNull(task2.getId());
		assertNotNull(database.getExistingDocument(task2.getId()));
		assertTrue(database.getExistingDocument(task2.getId())
				.getProperty("title").equals(task2.getTitle()));
	}

	public void testaddAttachmentsDB() throws CouchbaseLiteException {
		System.out.println("********************************");

		int w = 100, h = 100;

		Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
		Bitmap image = Bitmap.createBitmap(w, h, conf);

		task1.setImage(image);

		assertEquals(1, task1.getDbObjectAttachments().size());
		assertTrue(task1.getDbObjectAttachments().containsKey("image"));
		todoDB.getTasks().add(task1);
		todoDB.sync(true);
		/*
		 * try { Thread.sleep(10000); } catch (InterruptedException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */

		assertNotNull(database.getExistingDocument(task1.getId()));

		Document docTask1 = database.getExistingDocument(task1.getId());
		System.out.println("doc id: " + docTask1.getId());
		Revision rev = docTask1.getCurrentRevision();

		System.out.println("test current rev id: "
				+ docTask1.getCurrentRevisionId());

		System.out.println("coflicting current rev id: "
				+ docTask1.getConflictingRevisions().size());

		for (SavedRevision sr : docTask1.getConflictingRevisions()) {
			System.out.println("coflicting rev: " + sr.getId());

		}

		for (SavedRevision sr : docTask1.getLeafRevisions()) {
			System.out.println("2coflicting rev: " + sr.getId());

		}

		for (SavedRevision sr : docTask1.getRevisionHistory()) {
			System.out.println(" history rev coflicting rev: " + sr.getId());

		}

		System.out.println("2rev id: " + docTask1.getCurrentRevisionId());

		System.out.println("rev id: " + rev.getId());

		List<String> attachements = rev.getAttachmentNames();

		System.out.println("attachments: " + attachements.size());

		Document docTask2 = database.getExistingDocument(task2.getId());
		System.out.println("coflicting current rev id: "
				+ docTask2.getConflictingRevisions().size());
		for (SavedRevision sr : docTask2.getConflictingRevisions()) {
			System.out.println(" docTask2 coflicting rev: " + sr.getId());

		}

		assertTrue(attachements.contains("image"));

		assertNotNull(docTask1.getCurrentRevision().getAttachment("image"));

	}

	public void createWithAttachmentsDB() throws CouchbaseLiteException {
		System.out.println("********************************");

		int w = 100, h = 100;

		Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
		Bitmap image = Bitmap.createBitmap(w, h, conf);

		Task task3 = new Task();
		task3.setTitle("task3");
		task3.setImage(image);
		
		listTasks.getTasks().add(task3);

		assertEquals(1, task3.getDbObjectAttachments().size());
		assertTrue(task3.getDbObjectAttachments().containsKey("image"));

		todoDB.getTasks().add(task3);
		todoDB.sync(true);

		Document docTask3 = database.getExistingDocument(task3.getId());

		Revision rev = docTask3.getCurrentRevision();
		List<String> attachements = rev.getAttachmentNames();
		assertTrue(attachements.contains("image"));

		assertNotNull(docTask3.getCurrentRevision().getAttachment("image"));

		
		Task copyTask3 =todoDB.getTasks().findById(task3.getId());
		
		assertNotNull(copyTask3);
		
		assertEquals(task3.getTitle(), copyTask3.getTitle());
		
		assertNotNull(copyTask3.getImage());
		
	}

}
