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


public class Task extends DBObject {
	
	
	protected Location location = null;
	
	private Bitmap image = null;
	
	
	
	
	
	public Task() { 
		super();
		sharingStatus="shareable";	
		dbObject.put("location", new HashMap<String, Object>());
	}
	
	
		public static QueryProducer Task_title= new QueryProducer("com.york.cs.todolite2.document2.Task.title", "com.york.cs.todolite2.document2.Task");
		public static QueryProducer Task_checked= new QueryProducer("com.york.cs.todolite2.document2.Task.checked", "com.york.cs.todolite2.document2.Task");


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
	
	public Bitmap getImage() throws CouchbaseLiteException {
		

		if (image == null) {
			image=BitmapFactory.decodeStream(loadAttachment("image"));
			
		}
		return image;
	}
	
	public void setImage(Bitmap image)  {

		this.image = image;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 50, out);
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		dbObjectAttachments.put("image", in);

	}
	
	public Task setImage(String image) {
		dbObject.put("image", image);
		notifyChanged();
		return this;
	}
	
	
	
	/*getter  REFERENCES to contained classes*/
	/* **************************    **********************/ 
	/*getter multiple REFERENCES*/

	/*Setter and getter simple CONTAIMENT REFERENCES references to inner classes*/
	public Location getLocation() {
		if (location == null && dbObject.containsKey("location")) {
			location = (Location) ClassDBFactory.getInstance().createDBObject((HashMap<String, Object>) dbObject.get("location"));
			location.setContainer(this);
		}
		return location;
	}
	
	public Task setLocation(Location location) {
		if (this.location != location) {
			if (location == null) {
				dbObject.remove("location");
			}
			else {
				dbObject.put("location", location.getDbObject());
			}
			this.location = location;
			notifyChanged();
		}
		return this;
	}
	
	//DOCUMENTS!!!!!!!!!!!!!!!!!
		/*Setter and getter simple NOT CONTAIMENT REFERENCES references to toher documents*/
	
	
	/*getter multiple REFERENCES*/
}