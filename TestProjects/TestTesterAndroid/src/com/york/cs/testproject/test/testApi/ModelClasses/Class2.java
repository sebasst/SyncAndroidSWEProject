package com.york.cs.testproject.test.testApi.ModelClasses;

import com.york.cs.couchbaseapi.DBObject;


public class Class2 extends DBObject {
	
	public String getTitle() {
		return parseString(dbObject.get("title")+"", "");
	}
	
	public Class2 setTitle(String title) {
		dbObject.put("title", title);
		notifyChanged();
		return this;
	}

}
