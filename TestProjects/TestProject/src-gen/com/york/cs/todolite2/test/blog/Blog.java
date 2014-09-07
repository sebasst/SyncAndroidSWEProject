package com.york.cs.todolite2.test.blog;

import com.couchbase.lite.Database;
import com.york.cs.couchbaseapi.*;


public class Blog extends CouchDB {
	
	public Blog() {}
	
	public Blog(Database db) {
		setDb(db);
	}
	
	protected PostCollection posts = null;
	protected MemberCollection members = null;
	protected AuthorCollection authors = null;
	
	
	
	public PostCollection getPosts() {
		return posts;
	}
	
	public MemberCollection getMembers() {
		return members;
	}
	
	public AuthorCollection getAuthors() {
		return authors;
	}
	
	
	@Override
	public void setDb(Database db) {
		super.setDb(db);
		posts = new PostCollection(db);
		dbCollections.add(posts);
		members = new MemberCollection(db);
		dbCollections.add(members);
		authors = new AuthorCollection(db);
		dbCollections.add(authors);
	}
}