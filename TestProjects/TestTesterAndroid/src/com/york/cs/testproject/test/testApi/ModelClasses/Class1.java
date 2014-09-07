package com.york.cs.testproject.test.testApi.ModelClasses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.york.cs.couchbaseapi.DBObject;
import com.york.cs.couchbaseapi.DBList;
import com.york.cs.couchbaseapi.PrimitiveList;


public class Class1 extends DBObject {

	protected List<String> tags = null;
	protected List<Integer> ratings = null;
	protected List<Class2> comments = null;

	public Class1() {
		super();

		// dbObject.put("author", new BasicDBObject());
		dbObject.put("tags", new ArrayList<String>());
		dbObject.put("ratings", new ArrayList<Integer>());
		dbObject.put("comments", new ArrayList<HashMap<String, Object>>());
		
		

	}
	

	public String getTitle() {
		return parseString(dbObject.get("title")+"", "");
	}
	
	public Class1 setTitle(String title) {
		dbObject.put("title", title);
		notifyChanged();
		return this;
	}
	/*
	public PostType getType() {
		PostType type = null;
		try {
			type = PostType.valueOf(dbObject.get("type")+"");
		}
		catch (Exception ex) {}
		return type;
	}
	
	public Post setType(PostType type) {
		dbObject.put("type", type.toString());
		notifyChanged();
		return this;
	}
	*/
	
	/*
	public Post setAuthor(Author author) {
		if (this.author != author) {
			if (author == null) {
				dbObject.put("author", new BasicDBObject());
			}
			else {
				createReference("author", author);
			}
			this.author = author;
			notifyChanged();
		}
		return this;
	}
	
	public Author getAuthor() {
		if (author == null) {
			author = (Author) resolveReference("author");
		}
		return author;
	}
	
	public Stats getStats() {
		if (stats == null && dbObject.containsField("stats")) {
			stats = (Stats) PongoFactory.getInstance().createPongo((DBObject) dbObject.get("stats"));
		}
		return stats;
	}
	
	public Post setStats(Stats stats) {
		if (this.stats != stats) {
			if (stats == null) {
				dbObject.removeField("stats");
			}
			else {
				dbObject.put("stats", stats.getDbObject());
			}
			this.stats = stats;
			notifyChanged();
		}
		return this;
	}
*/
}
