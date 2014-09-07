package com.york.cs.testproject.test.testApi;

import android.test.ApplicationTestCase;

import com.couchbase.lite.Database;
import com.york.cs.todolite2.test.blog.Application;

import junit.framework.TestCase;

public class BDRefTest extends ApplicationTestCase<Application> {
	
	public BDRefTest() {
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

}
