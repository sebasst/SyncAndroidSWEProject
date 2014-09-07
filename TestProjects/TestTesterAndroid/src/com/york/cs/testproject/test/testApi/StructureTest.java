package com.york.cs.testproject.test.testApi;



import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;









import javadz.beanutils.PropertyUtils;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import android.test.ApplicationTestCase;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.storage.ContentValues;
import com.couchbase.lite.util.Log;
import com.york.cs.todolite2.test.blog.Application;



public class StructureTest extends ApplicationTestCase<Application> {

	Application application;

	// Couchlite database
	private Database database;

	public StructureTest() {
		super(Application.class);

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createApplication();
		application = getApplication();

		database = application.getDatabase();
	}
/*
	public void Test1(){
		Class1 c1 = new Class1();
		
		c1.setTitle("c1");
		Class2 c2 = new Class2();
		c2.setTitle("c2");
		Class2 c22 = new Class2();
		c22.setTitle("c2");
		
		c1.getComments().add(c2);
		c1.getComments().add(c22);
		
		
		String s2=c1.getComments().get(0).getTitle();
		System.out.println(s2);
		System.out.println(c2.getTitle());
		
		Map<String, Object> mapc1=c1.getDbObject();
		
		String tc1= (String) mapc1.get("title");
		
		
		Set<String> set=mapc1.keySet();
		
		for(String s:set){
			System.out.println(s);
		}

		
		List<Class2> listc2 = (List<Class2>) mapc1.get("comments");
		
		System.out.println(listc2.size()+"s");
		
		//System.out.println(c2.containingFeature);
		//System.out.println(c1.containingFeature);
		
		
		System.out.println((String)c2.getDbObject().get("_id"));
		
	//	Set<E>PongoFactory.getInstance().cache.keySet();
		
	}

*/
	public void testMappingDB() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		String json = "{ \"data\": { \"data2\" : { \"value\" : \"hell123123\"}}}";
		ObjectMapper mapper = new org.codehaus.jackson.map.ObjectMapper();
		ContentValues contentValues = new ContentValues();
		byte[] jason;
		ObjectWriter writer = Manager.getObjectMapper().writer();
        
            
		//contentValues.toString();

		Object jsonObj; 
		try {
			
			Document doc=database.getExistingDocument("adcb355f-f0d0-4497-8fbd-9c4757655f59");
			//System.out.println(doc.getProperties());
			
			Map<String, Object> prop = doc.getProperties();
			jason = writer.writeValueAsBytes(prop);
			jsonObj = mapper.readValue(jason, Object.class);

			Object hello = PropertyUtils.getProperty(jsonObj,
					"comments");
			
		//	ObjectMapper mapper2 = new org.codehaus.jackson.map.ObjectMapper();
			
			//ObjectWriter jsonObj2 = (ObjectWriter) mapper2.readValue(jason, Object.class);
			//Object hello2 = PropertyUtils.getProperty(jsonObj,
				//	"data.data2.value");
			//

		//	System.out.println("-->"+openFileToString(jason)); // prints hello
			System.out.println("-->"+ hello);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
	
	
	public String openFileToString(byte[] _bytes)
	{
	    String file_string = "";

	    for(int i = 0; i < _bytes.length; i++)
	    {
	        file_string += (char)_bytes[i];
	    }

	    return file_string;    
	}
	
	
	public void cleanDB() throws CouchbaseLiteException {
		Query query = database.createAllDocumentsQuery();
		query.setAllDocsMode(Query.AllDocsMode.ALL_DOCS);
		QueryEnumerator result = query.run();

		System.out.println("Total Docs: " + result.getCount());

		Log.d("cleanDB", "Total Docs: " + result.getCount());

		for (Iterator<QueryRow> it = result; it.hasNext();) {
			// Log.w("MYAPP", "iterator: ");
			QueryRow row = it.next();
			// if (row.getConflictingRevisions().size() > 0) {
			Log.w("MYAPP", "Conflict in document: %s", row.getDocumentId());

			System.out.println("delete: "
					+ database.getDocument(row.getDocumentId()).delete());
			// }
		}

		result = query.run();

		System.out.println("Total Docs: " + result.getCount());
		Log.d("cleanDB", "Total Docs: " + result.getCount());
	}
}