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


public class Member extends Person {
	
	
	
	
	
	
	
	
	public Member() { 
		super();
		sharingStatus="private";	
		super.setSuperTypes("com.york.cs.todolite2.test.blog.Person");
	}
	
	
		public static QueryProducer Member_name= new QueryProducer("com.york.cs.todolite2.test.blog.Member.name", "com.york.cs.todolite2.test.blog.Member");
		public static QueryProducer Author_name= new QueryProducer("com.york.cs.todolite2.test.blog.Author.name", "com.york.cs.todolite2.test.blog.Member");


	
	
	
	/*getter  REFERENCES to contained classes*/
	/* **************************    **********************/ 
	/*getter multiple REFERENCES*/

	/*Setter and getter simple CONTAIMENT REFERENCES references to inner classes*/
	
	//DOCUMENTS!!!!!!!!!!!!!!!!!
		/*Setter and getter simple NOT CONTAIMENT REFERENCES references to toher documents*/
	
	
	/*getter multiple REFERENCES*/
}