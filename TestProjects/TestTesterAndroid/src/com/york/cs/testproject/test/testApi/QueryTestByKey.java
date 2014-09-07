package com.york.cs.testproject.test.testApi;

import android.test.ApplicationTestCase;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.york.cs.couchbaseapi.queryBuilder.QueryProducer;

import com.york.cs.todolite2.test.blog.*;

public class QueryTestByKey extends ApplicationTestCase<Application> {
	public QueryTestByKey() {
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
	public void test1() throws CouchbaseLiteException{
		Blog blog = new Blog(database);
		
		Post post1 = new Post();
		post1.setTitle("post1");
		

		Post post2 = new Post();
		post2.setTitle("post");
		
		blog.getPosts().add(post1);
	//	blog.getPosts().add(post2);
		
		//blog.sync();
		
		
		int counter =0;
		Iterable<Post> terIterable=blog.getPosts().findByKey(Post.Post_title, 0, 0, "post1");
		System.out.println("en it"+terIterable.iterator().hasNext());
		while (terIterable.iterator().hasNext()) {
			counter=counter+1;
			System.out.println("en it------------ "+counter);
			Post postt = terIterable.iterator().next();
			System.out.println("------------------>post title: "+postt.getTitle());
			System.out.println("en it"+terIterable.iterator().hasNext());
			
		}
		
		System.out.println("en it------------ "+counter);
		
		
		
		
	}

	public void testByInterval() throws CouchbaseLiteException{
		Blog blog = new Blog(database);
		
	//	Post post1 = new Post();
	//	post1.setTitle("post16");
		

		Post post2 = new Post();
		post2.setTitle("post19");
		
		//blog.getPosts().add(post1);
	blog.getPosts().add(post2);
		
		blog.sync();
		
		
		int counter =0;
		Iterable<Post> terIterable=blog.getPosts().findByInterval(Post.Post_title, 0, 0, "post19", "post19");
		System.out.println("en it"+terIterable.iterator().hasNext());
		while (terIterable.iterator().hasNext()) {
			counter=counter+1;
			//System.out.println("en it------------ "+counter);
			Post postt = terIterable.iterator().next();
			System.out.println("------------------>post title: "+postt.getTitle());
			//System.out.println("en it"+terIterable.iterator().hasNext());
			
		}
		
		System.out.println("en it------------ "+counter);
		
		
		
		
	}
*/}
