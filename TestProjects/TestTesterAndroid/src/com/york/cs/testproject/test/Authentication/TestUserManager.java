package com.york.cs.testproject.test.Authentication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.york.cs.services.authentication.Profile;
import com.york.cs.services.authentication.User;
import com.york.cs.services.authentication.UserManager;
import com.york.cs.testproject.test.testutils.DataBaseAdm;
import com.york.cs.todolite2.test.blog.Application;
import com.york.cs.todolite2.test.blog.Author;
import com.york.cs.todolite2.test.blog.Blog;
import com.york.cs.todolite2.test.blog.Comment;
import com.york.cs.todolite2.test.blog.Member;
import com.york.cs.todolite2.test.blog.Post;
import com.york.cs.todolite2.test.blog.PostType;
import com.york.cs.todolite2.test.blog.Stats;

import flex.messaging.io.ArrayList;

public class TestUserManager extends ApplicationTestCase<Application> {

	public TestUserManager() {
		super(Application.class);
		// TODO Auto-generated constructor stub
	}

	Application application;

	// Couchlite database
	private Database database;
	Blog blog;
	Post post1;
	Author author1;
	Comment comment1;

	User user;
	Context context;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createApplication();
		user = new User();

		user.setId("usertestFromApp2");
		user.setName("usertestFromApp Name2");
		user.setEmail("usertestFromApp@email2");
		user.setPassword("usertestFromApppassword2");

		context = Application.getContext();

		application = getApplication();
		if(application==null)System.out.println("change configuration application and uninstall app before running this test");

		database = application.getDatabase();

		DataBaseAdm.cleanDB(application);

	}

	public void testSignUp() throws Exception {

		UserManager.removeCurretUserFromApp(context);
		if (UserManager.getCurrentDeviceAccount(context) != null)
			UserManager.removeAccount(context);

		Map<String, Integer> mapUserDoc = null;
		DataBaseAdm.cleanDB(application);
		mapUserDoc = testUserOwner(user.getEmail());

		assertEquals(0, mapUserDoc.get("sameUser").intValue());
		assertEquals(0, mapUserDoc.get("userNull").intValue());

		populateNewData();
		mapUserDoc = testUserOwner(user.getEmail());

		assertEquals(2, mapUserDoc.get("userNull").intValue());
		assertEquals(0, mapUserDoc.get("sameUser").intValue());

		System.out.println(">>>>>>>>***************************");

		String sesionC = UserManager.userSignUp(context, user);

		assertNotNull(sesionC);

		Profile userProfile = UserManager.getCurrentUserProfile(context);

		assertEquals(user.getEmail(), userProfile.getUserEmail());

		System.out.println(">>>>>>>>***************************");

		mapUserDoc = testUserOwner(user.getEmail());

		assertEquals(0, mapUserDoc.get("userNull").intValue());
		assertEquals(3, mapUserDoc.get("sameUser").intValue());

		UserManager.removeCurretUserFromApp(context);
		UserManager.removeAccount(context);

		mapUserDoc = testUserOwner(user.getEmail());

		assertEquals(2, mapUserDoc.get("userNull").intValue());
		assertEquals(0, mapUserDoc.get("sameUser").intValue());

		DataBaseAdm.cleanDB(application);

	}

	private Map<String, Integer> testUserOwner(String email)
			throws CouchbaseLiteException {

		int userNull = 0;
		int sameUser = 0;

		Map<String, Integer> map = new HashMap<String, Integer>();

		Log.d("updateing channel with new user", "Total ");
		Query query = getQueryByOwner(database);
		QueryEnumerator result;

		List<Object> keys = new ArrayList();
		String key = "null";

		keys.add(key);
		query.setKeys(keys);

		result = query.run();
		userNull = result.getCount();

		keys.clear();
		keys.add(email);
		result = query.run();
		sameUser = result.getCount();

		map.put("sameUser", sameUser);

		map.put("userNull", userNull);

		printKeyFromMap1(map);

		return map;



	}

	private static void printKeyFromMap1(Map<String, Integer> map) {

		System.out.println("-----------------keys----------------");

		for (String key : map.keySet()) {
			System.out.println(key + " : " + map.get(key));
		}
	}

	private void populateNewData() throws CouchbaseLiteException {
		blog = new Blog(database);

		post1 = new Post();

		blog.getPosts().add(post1);

		post1.setTitle("Post1");
		// add tags
		post1.getTags1().add("tag1");
		post1.getTags1().add("tag2");
		// add rating
		post1.getRatings().add(1);
		post1.getRatings().add(2);

		// Author

		author1 = new Author();
		author1.setName("author1");
		blog.getAuthors().add(author1);
		post1.setAuthor1(author1);

		// add comments
		Member member1 = new Member();
		member1.setName("Member1");
		comment1 = new Comment();
		comment1.setAuthor(author1);
		comment1.setText("comment1");

		Comment comment2 = new Comment();
		comment2.setAuthor(author1);
		comment2.setText("comment2");
		comment1.getReplies().add(comment2);

		post1.getComments().add(comment1);
		// stats
		Stats stats1 = new Stats();
		stats1.setVisitors(2);
		stats1.setPageloads(3);
		post1.setStats(stats1);
		// PostType -->Enum
		post1.setPostType(PostType.Sticky);

		blog.sync(true);
	}

	private Query getQueryByOwner(Database database) {

		// com.couchbase.lite.View view0 = database.getView("tester"
		// + "byOwnerId");
		// view0.delete();
		com.couchbase.lite.View view = database.getView("tester" + "byOwnerId");
		if (view.getMap() == null) {
			Mapper map = new Mapper() {
				@Override
				public void map(Map<String, Object> document, Emitter emitter) {
					if (document.get("owner_id") != null)
						emitter.emit(document.get("owner_id"),
								document.get("owner_id"));
					else
						emitter.emit("null", document.get("owner_id"));

				}
			};
			view.setMap(map, null);
		} else {

		}

		Query query = view.createQuery();

		return query;
	}

}
