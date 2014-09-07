package com.york.cs.todolite2.document2;

import com.couchbase.lite.Database;
import com.york.cs.couchbaseapi.*;


public class TodoDB extends CouchDB {
	
	public TodoDB() {}
	
	public TodoDB(Database db) {
		setDb(db);
	}
	
	protected ListTasksCollection listTasks = null;
	protected TaskCollection tasks = null;
	
	
	
	public ListTasksCollection getListTasks() {
		return listTasks;
	}
	
	public TaskCollection getTasks() {
		return tasks;
	}
	
	
	@Override
	public void setDb(Database db) {
		super.setDb(db);
		listTasks = new ListTasksCollection(db);
		dbCollections.add(listTasks);
		tasks = new TaskCollection(db);
		dbCollections.add(tasks);
	}
}