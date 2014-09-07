package com.york.cs.testproject.test.blog;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.ReferenceMap;

import android.test.ApplicationTestCase;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.york.cs.couchbaseapi.ClassDBFactory;
import com.york.cs.todolite2.test.blog.Application;


public class BlogTest extends ApplicationTestCase<Application> {

	Application application;

	// Couchlite database
	private Database database;

	public BlogTest() {
		super(Application.class);

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createApplication();
		application = getApplication();

		database = application.getDatabase();
	}
/*
	public void testCreation() throws CouchbaseLiteException {

		Blog blog = new Blog(database);

		Post post = new Post();

		post.setTitle("Post1");
		blog.getPosts().add(post);

		blog.sync(true);

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

	public void testReferences() throws CouchbaseLiteException {
		ReferenceMap cache2 = PongoFactory.getInstance().cache;

		System.out.println("+++++++++++++++" + cache2.keySet().size());
		Blog blog = new Blog(database);

		Post post = new Post();

		post.setTitle("Post1");
		blog.getPosts().add(post);

		Author author1 = new Author();
		author1.setName("Author 1");
		blog.getAuthors().add(author1);

		Author author2 = new Author();
		author2.setName("Author 2");
		blog.getAuthors().add(author2);

		post.getAuthors2().add(author1);
		post.getAuthors2().add(author2);
		System.out.println("+++++++++++++++" + cache2.keySet().size());
		blog.sync(true);

		PongoFactory.getInstance().clear();
		Blog blog2 = new Blog(database);

		Post post2 = blog2.getPosts().findById(post.getId());

		assertEquals(post.getTitle(), post2.getTitle());
		
		Author author12= blog2.getAuthors().findById(author1.getId());
		assertEquals(author1.getName(), author12.getName());
		System.out.println("+++++++++++++++" +author12.getId() +" : "+author12.getName());
		
		
		Author author22= blog2.getAuthors().findById(author2.getId());
		assertEquals(author2.getName(), author22.getName());
		System.out.println("+++++++++++++++" +author22.getId() +" : "+author22.getName());
		
		
		System.out.println("+++++++++++++++" + cache2.keySet().size());
		
		assertEquals(2,post2.getAuthors2().size());
		List<Author> authorList=post2.getAuthors2();
		Document doc =database.getExistingDocument(post.getId());
		
		System.out.println(doc.getProperties());
		
		for(Author authort: authorList){
			System.out.println("------>Author: "+authort.getId());
			System.out.println("------>Author: "+authort.getName());
		}
		
		

	}*/
}
