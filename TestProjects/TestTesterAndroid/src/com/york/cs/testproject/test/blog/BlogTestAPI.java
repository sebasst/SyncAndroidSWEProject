package com.york.cs.testproject.test.blog;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.test.ApplicationTestCase;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;

import com.york.cs.testproject.test.testutils.DataBaseAdm;
//import com.york.cs.todolite2.test.testutils.DataBaseAdm;
import com.york.cs.todolite2.test.blog.*;

public class BlogTestAPI extends ApplicationTestCase<Application> {

	Application application;

	// Couchlite database
	private Database database;
	Blog blog;
	Post post1;
	Author author1;
	Comment comment1;

	public BlogTestAPI() {
		super(Application.class);

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createApplication();
		application = getApplication();

		database = application.getDatabase();

		DataBaseAdm.cleanDB(application);

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

		Member member2 = new Member();
		member2.setName("Member1");
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

	public void CreateObjectsDB() throws CouchbaseLiteException {
		/* FR-06 */

		// exists object2 in db
		assertTrue(database.getExistingDocument(post1.getId())
				.getProperty("title").equals("Post1"));
		assertTrue(database.getExistingDocument(author1.getId())
				.getProperty("name").equals("author1"));
	}

	public void RetrieveObjectsDB() throws CouchbaseLiteException {

		/*******************************************************************/
		/* FR-09 */
		// test con from api
		assertEquals(1, blog.getPosts().size());
		assertEquals(1, blog.getAuthors().size());

		// with id
		Post copyPost1 = blog.getPosts().findById(post1.getId());
		assertEquals("Post1", copyPost1.getTitle());

		Author copyAuthor1 = blog.getAuthors().findById(author1.getId());
		assertEquals("author1", copyAuthor1.getName());

		// using query producer

		Iterable<Author> iterableAuthor = blog.getAuthors().findByKey(
				Author.Author_name, 0, 0, "author1");
		assertTrue(iterableAuthor.iterator().hasNext());
		Author copyAuthor2 = iterableAuthor.iterator().next();
		assertEquals("author1", copyAuthor2.getName());
		assertFalse(iterableAuthor.iterator().hasNext());

		Iterable<Post> iterablePost = blog.getPosts().findByKey(
				Comment.Post_comments_text, 0, 0, "comment1");
		assertTrue(iterablePost.iterator().hasNext());
		Post copyPost2 = iterablePost.iterator().next();
		assertEquals("Post1", copyPost2.getTitle());
		assertFalse(iterablePost.iterator().hasNext());

		

		// verify counter

		assertEquals(
				1,
				blog.getAuthors().countByKey(Author.Author_name, 0, 0,
						"author1"));
		assertEquals(
				1,
				blog.getAuthors().countByInterval(Author.Author_name, 0, 0,
						"author1", "author1"));

		// reference

		assertEquals("author1", copyPost2.getAuthor1().getName());

		/*******************************************************************/

		// nested object update

	}

	public void updateNestedObjectsDB() throws CouchbaseLiteException {

		/* FR-07 */
		// update comment 1
		System.out
				.println(" **************************  updateObjectsDB  **********************");

		Long initialPostSize = blog.getPosts().size();
		Post post1Copy = blog.getPosts().findById(post1.getId());

		Comment comment1Copy = post1Copy.getComments().get(0);
		System.out.println("text: " + comment1Copy.getText());
		comment1Copy.setText("comment1V22");

		post1Copy.getComments().add(comment1Copy);

		blog.getPosts().add(post1Copy);
		blog.sync(true);

		Long finalPostSize = blog.getPosts().size();
		// confirm that a new post was not created
		assertEquals(initialPostSize, finalPostSize);

		// confirm if data is persisted in DB
		Document doc = database.getExistingDocument(post1.getId());

		List<Map<String, Object>> commentsList = (List<Map<String, Object>>) doc
				.getProperty("comments");

		boolean result = false;
		for (Map<String, Object> mapt : commentsList) {

			String text = (String) mapt.get("text");

			result = result || (text.equals("comment1V22"));

		}

		assertTrue(result);

		// confirm that it can be queried

		Iterable<Post> postIterable = blog.getPosts().findByKey(
				Comment.Post_comments_text, 0, 0, "comment1V22");
		Iterator<Post> postIterator = postIterable.iterator();
		assertTrue(postIterator.hasNext());

		Post post2Copy = postIterator.next();

		assertEquals(post1.getId(), post2Copy.getId());

		List<Comment> comments = post2Copy.getComments();

		Comment comment2Copy = null;

		for (Comment c : comments) {
			if (c.getText().equals("comment1V22"))
				comment2Copy = c;
		}

		assertEquals(comment1.getId(), comment2Copy.getId());

	}

	public void updateRootObjectDB() throws CouchbaseLiteException {
		/* FR-07 */
		//Post copyPost1 = blog.getPosts().findById(post1.getId());
		//copyPost1.getAuthor1().setName("author1V2");

		Author authorCopy = blog.getAuthors().findById(author1.getId());
		
		authorCopy.setName("author1V2");
		
		
		
		blog.sync(true);
		// verify change in DB
		assertEquals("author1V2",database.getExistingDocument(author1.getId()).getProperty(
				"name"));

		// Veryfy if can be queried
		Iterable<Author> iterableAuthor2 = blog.getAuthors().findByKey(
				Author.Author_name, 0, 0, "author1V2");

		assertEquals(1, blog.getAuthors().size());
		assertTrue(iterableAuthor2.iterator().hasNext());
		Author copyAuthor3 = iterableAuthor2.iterator().next();
		assertEquals("author1V2", copyAuthor3.getName());
		assertEquals(author1.getId(), copyAuthor3.getId());
		// verify if not created a copy
		assertFalse(iterableAuthor2.iterator().hasNext());
	}

	public void deleteRootObjectDB() throws CouchbaseLiteException {
		// FR-08
		Post postToDelete = new Post();
		postToDelete.setTitle("postToDelete");

		blog.getPosts().add(postToDelete);
		blog.sync(true);

		// verify that was persisted in db
		Document doc = database.getExistingDocument(postToDelete.getId());

		assertEquals(postToDelete.getTitle(), doc.getProperty("title"));

		Post postToDeleteCopy = blog.getPosts().findById(postToDelete.getId());

		blog.getPosts().remove(postToDeleteCopy);

		blog.sync(true);

		// verify that was deleted in db
		Document doc2 = database.getExistingDocument(postToDelete.getId());
		assertNull(doc2);
		

	}

}
