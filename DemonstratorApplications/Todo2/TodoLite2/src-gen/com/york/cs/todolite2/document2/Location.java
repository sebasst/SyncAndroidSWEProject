package com.york.cs.todolite2.document2;

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


public class Location extends DBObject {
	
	
	
	
	
	
	
	
	public Location() { 
		super();
	}
	
	
		public static QueryProducer Task_location_name= new QueryProducer("com.york.cs.todolite2.document2.Task.location.name", "com.york.cs.todolite2.document2.Location");
		public static QueryProducer Task_location_addres= new QueryProducer("com.york.cs.todolite2.document2.Task.location.addres", "com.york.cs.todolite2.document2.Location");


	public String getName() {
		return parseString(dbObject.get("name")+"", "");
	}
	
	public Location setName(String name) {
		dbObject.put("name", name);
		notifyChanged();
		return this;
	}
	public String getAddres() {
		return parseString(dbObject.get("addres")+"", "");
	}
	
	public Location setAddres(String addres) {
		dbObject.put("addres", addres);
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