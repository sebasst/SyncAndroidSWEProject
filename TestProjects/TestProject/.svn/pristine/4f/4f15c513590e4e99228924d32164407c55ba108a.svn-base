package com.york.cs.todolite2.test.blog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.InputStream;
import java.util.List;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.york.cs.couchbaseapi.*;
import com.york.cs.couchbaseapi.queryBuilder.QueryProducer;
import com.couchbase.lite.CouchbaseLiteException;


public class Person extends DBObject {
	
	
	
	
	
	
	
	
	public Person() { 
		super();
	}
	
	
		public static QueryProducer Member_name= new QueryProducer("com.york.cs.todolite2.test.blog.Member.name", "com.york.cs.todolite2.test.blog.Person");
		public static QueryProducer Author_name= new QueryProducer("com.york.cs.todolite2.test.blog.Author.name", "com.york.cs.todolite2.test.blog.Person");


	public String getName() {
		return parseString(dbObject.get("name")+"", "");
	}
	
	public Person setName(String name) {
		dbObject.put("name", name);
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
}