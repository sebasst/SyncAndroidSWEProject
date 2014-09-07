package com.york.cs.todolite2.document2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.york.cs.couchbaseapi.*;
import com.york.cs.couchbaseapi.queryBuilder.QueryProducer;


public class ListTasks extends DBObject {
	
	
	protected List<Task> tasks = null;
	
	
	
	public ListTasks() { 
		super();
		sharingStatus="shareable";	
		dbObject.put("tasks", new ArrayList<HashMap<String, Object>>());
	}
	
	
		public static QueryProducer ListTasks_title= new QueryProducer("com.york.cs.todolite2.document2.ListTasks.title", "com.york.cs.todolite2.document2.ListTasks");
		public static QueryProducer ListTasks_tasks= new QueryProducer("com.york.cs.todolite2.document2.ListTasks.tasks","com.york.cs.todolite2.document2.ListTasks");

	
	public String getTitle() {
		return parseString(dbObject.get("title")+"", "");
	}
	
	public ListTasks setTitle(String title) {
		dbObject.put("title", title);
		notifyChanged();
		return this;
	}
	
	
	
	/*getter  REFERENCES to contained classes*/
	/* **************************    **********************/ 
	/*getter multiple REFERENCES*/

	/*Setter and getter simple CONTAIMENT REFERENCES references to inner classes*/
	
	//DOCUMENTS!!!!!!!!!!!!!!!!!
		/*Setter and getter simple NOT CONTAIMENT REFERENCES references to toher documents*/
	
	
	/*getter multiple REFERENCES*/
	public List<Task> getTasks() {
		if (tasks == null) {
			tasks = new DBList<Task>(this, "tasks", false);
		}
		return tasks;
	}
}