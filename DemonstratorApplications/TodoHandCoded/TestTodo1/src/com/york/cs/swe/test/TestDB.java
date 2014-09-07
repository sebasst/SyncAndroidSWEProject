package com.york.cs.swe.test;

import junit.framework.TestCase;

import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.york.cs.swe.todolite.Application;

import android.content.Context;
import android.test.ApplicationTestCase;
import android.util.Log;

public class TestDB extends ApplicationTestCase<Application>  {

	public TestDB() {
		super(Application.class);
		
	}
	private Database database;
	Context context;
	Application application;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createApplication();
		

		context = Application.getContext();

		application = getApplication();

		database = application.getDatabase();

		

	}
	
	public void getDocument(){
	
		Document doc=database.getDocument("8e89dde4-8697-46bb-b67d-10b6155f177d");
		if(doc==null)System.out.println("doc null");
		else System.out.println(doc.getProperties());
		
	}

}
