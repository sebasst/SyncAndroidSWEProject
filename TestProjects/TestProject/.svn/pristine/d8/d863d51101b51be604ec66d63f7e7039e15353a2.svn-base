package com.york.cs.todolite2.document2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.york.cs.couchbaseapi.*;
import com.york.cs.couchbaseapi.queryBuilder.QueryProducer;


public class Task extends DBObject {
	
	
	protected ListTasks listTask = null;
	
	
	
	public Task() { 
		super();
		sharingStatus="shareable";	
		dbObject.put("listTask", new HashMap<String, Object>());
	}
	
	
		public static QueryProducer Task_title= new QueryProducer("com.york.cs.todolite2.document2.Task.title", "com.york.cs.todolite2.document2.Task");
		public static QueryProducer Task_checked= new QueryProducer("com.york.cs.todolite2.document2.Task.checked", "com.york.cs.todolite2.document2.Task");
		public static QueryProducer Task_listTask= new QueryProducer("com.york.cs.todolite2.document2.Task.listTask","com.york.cs.todolite2.document2.Task");

	
	public String getTitle() {
		return parseString(dbObject.get("title")+"", "");
	}
	
	public Task setTitle(String title) {
		dbObject.put("title", title);
		notifyChanged();
		return this;
	}
	public boolean getChecked() {
		return parseBoolean(dbObject.get("checked")+"", false);
	}
	
	public Task setChecked(boolean checked) {
		dbObject.put("checked", checked);
		notifyChanged();
		return this;
	}
	
	
	
	/*getter  REFERENCES to contained classes*/
	/* **************************    **********************/ 
	/*getter multiple REFERENCES*/

	/*Setter and getter simple CONTAIMENT REFERENCES references to inner classes*/
	
	//DOCUMENTS!!!!!!!!!!!!!!!!!
		/*Setter and getter simple NOT CONTAIMENT REFERENCES references to toher documents*/
	
	public Task setListTask(ListTasks listTask) {
		if (this.listTask != listTask) {
			if (listTask == null) {
				dbObject.put("listTask", new HashMap<String, Object>());
				if(this.listTask !=null) this.listTask .removeReferencedBy(getId());
			}
			else {
				createReference("listTask", listTask);
			}
			this.listTask = listTask;
			notifyChanged();
		}
		return this;
	}
	
	public ListTasks getListTask() {
		if (listTask == null) {
			listTask = (ListTasks) resolveReference("listTask");
		}
		return listTask;
	}
	
	
	/*getter multiple REFERENCES*/
}