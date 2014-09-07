package com.york.cs.testproject.test.testApi;

import java.util.Iterator;
import java.util.Map;

import android.test.ApplicationTestCase;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.util.Log;

import com.york.cs.todolite2.test.blog.*;

public class FirstRun extends ApplicationTestCase<Application> {
	public FirstRun() {
		super(Application.class);
	}

	Application application;

	// Couchlite database
	private Database database;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createApplication();
		application = getApplication();

		database = application.getDatabase();
	}
/*
	public void Test1() throws CouchbaseLiteException {
		
		DataBaseAdm.cleanDB(database);
		Blog blog = new Blog(database);

		Post post = new Post();

		post.setTitle("Post1");
		blog.getPosts().add(post);

		blog.sync();

		String id = post.getId();
		System.out.println("**********Post: " + post.getId());

		Document doc = database.getDocument(id);

		System.out.println("**********Post: " + doc.getId());

		Map<String, Object> pro = doc.getProperties();
		System.out.println("**********Post title: " + pro.get("title"));

		for (String s : pro.keySet()) {
			System.out.println(s);
			System.out.println(pro.get(s));

		}

		assertEquals("Post1", pro.get("title"));
		assertEquals(id, doc.getId());
		assertEquals(id, pro.get("_id"));

	}

	public void testBlog() throws CouchbaseLiteException {

		Blog blog = new Blog(database);

		Post post = new Post();
		post.setTitle("A post");

		Author author = new Author();
		author.setName("Joe Doe");
		blog.getAuthors().add(author);
		// System.out.println("-----------------------------pre set author");
		post.setAuthor(author);
		// System.out.println("-----------------------------post set author");

		

		

		Comment comment = new Comment();
		comment.setText("A comment");
		
		
		post.getComments().add(comment);

		Comment reply = new Comment();
		
		
		
		reply.setText("A reply");
		
		comment.getReplies().add(reply);
		
		
		
		Member memberLike = new Member();

		memberLike.setName("member like");
		
		Member memberNoLike = new Member();

		
		memberNoLike.setName("member no like");

		blog.getMembers().add(memberLike);
		blog.getMembers().add(memberNoLike);
		reply.getLiked().add(memberLike);
		reply.getDisliked().add(memberNoLike);
		
		blog.getPosts().add(post);
		blog.sync();
		System.out.println("-----------------------------post" + post.getId());
		Document document = database.getExistingDocument(post.getId());
		Map<String, Object> pro = document.getProperties();
		for (String s : pro.keySet()) {
			System.out.println(s);
			System.out.println(pro.get(s));

		}
		System.out.println("-----------------------------author"
				+ author.getId());
		document = database.getExistingDocument(author.getId());
		pro = document.getProperties();
		for (String s : pro.keySet()) {
			System.out.println(s);
			System.out.println(pro.get(s));

		}

		// Syncs with the underlying database

	}

	public void testBlogRead() throws CouchbaseLiteException {

		Blog blog = new Blog(database);

		System.out.println("post size: " + blog.getPosts().size());
		int counter = 1;
		for (Post post : blog.getPosts()) {
			System.out.println("counter: ");
			System.out.println("---->id: " + post.getId());
			Author author = post.getAuthor();
			System.out.println("author dbobj: " + author.getDbObject());

			System.out.println("title: " + post.getTitle() + " - "// );
					+ post.getAuthor().getName());
			System.out.println(post.getComments().size() + " comment(s)");

			for (Comment comment : post.getComments()) {
				System.out.println(comment.getText() + " comment(s)");
				System.out.println(comment.getReplies().get(0).getText()
						+ " comment(s)");

			}
			counter = counter + 1;
		}
	}
*/
	public void testRead() throws CouchbaseLiteException {

		Query query = database.createAllDocumentsQuery();
		query.setAllDocsMode(Query.AllDocsMode.ALL_DOCS);
		QueryEnumerator result = query.run();

		System.out.println("result: " + result.getCount());
		int counter = 0;
		for (Iterator<QueryRow> it = result; it.hasNext();) {
			QueryRow row = it.next();
			try {
				if (row.getDocumentProperties().containsKey("type")) {
					System.out.println("id: " + row.getDocumentId());
					System.out.println("id: "
							+ row.getDocumentProperties().get("type"));
				}
			} catch (Exception e) {
				counter = counter + 1;
				System.out.println("id: " + row.getDocumentId());
				System.out.println("counter: " + counter);
				e.printStackTrace();
			}

		}
	}

	public void printkeys() {
		// 73b8759e-2831-4e62-a09d-6a9b0b6aa787
		// Document
		// document=database.getDocument("b9cc57cb-4b84-4410-9b28-1d8e3d0ded6b");
		// Document
		// document=database.getDocument("5da060ce-6a40-4e76-9e59-e4e720b98786");
		// 0595a98f-d681-4b43-a8d5-b46aec487ae6
		Document document = database
				.getDocument("0595a98f-d681-4b43-a8d5-b46aec487ae6");
		Map<String, Object> pro = document.getProperties();
		for (String s : pro.keySet()) {
			System.out.println(s);
			System.out.println(pro.get(s));

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
