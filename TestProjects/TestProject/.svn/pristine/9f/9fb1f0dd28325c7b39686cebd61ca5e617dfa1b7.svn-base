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


public class Comment extends DBObject {
	
	
	protected List<Comment> replies = null;
	protected List<Member> liked = null;
	protected List<Member> disliked = null;
	protected List<Flag> flags = null;
	protected Author author = null;
	
	
	
	
	
	
	public Comment() { 
		super();
		dbObject.put("author", new HashMap<String, Object>());
		dbObject.put("flags", new ArrayList<Flag>());
		dbObject.put("replies", new ArrayList<HashMap<String, Object>>());
		dbObject.put("liked", new ArrayList<HashMap<String, Object>>());
		dbObject.put("disliked", new ArrayList<HashMap<String, Object>>());
	}
	
	
		public static QueryProducer Post_comments_text= new QueryProducer("com.york.cs.todolite2.test.blog.Post.comments.text", "com.york.cs.todolite2.test.blog.Comment");
		public static QueryProducer Post_comments_flags= new QueryProducer("com.york.cs.todolite2.test.blog.Post.comments.flags", "com.york.cs.todolite2.test.blog.Comment");
		public static QueryProducer Post_comments_author= new QueryProducer("com.york.cs.todolite2.test.blog.Post.comments.author","com.york.cs.todolite2.test.blog.Comment");
		public static QueryProducer Post_comments_liked= new QueryProducer("com.york.cs.todolite2.test.blog.Post.comments.liked","com.york.cs.todolite2.test.blog.Comment");
		public static QueryProducer Post_comments_disliked= new QueryProducer("com.york.cs.todolite2.test.blog.Post.comments.disliked","com.york.cs.todolite2.test.blog.Comment");


	public String getText() {
		return parseString(dbObject.get("text")+"", "");
	}
	
	public Comment setText(String text) {
		dbObject.put("text", text);
		notifyChanged();
		return this;
	}
	
	public List<Flag> getFlags() {
		if (flags == null) {//TODO -TIPOS DE OBJECTO //FIXME 
			flags = new PrimitiveList<Flag>(this, ((ArrayList<Flag>) dbObject.get("flags")));
		}
		return flags;
	}
	
	
	/*getter  REFERENCES to contained classes*/
	/* **************************    **********************/ 
	/*getter multiple REFERENCES*/
	public List<Comment> getReplies() {
		if (replies == null) {
			replies = new DBList<Comment>(this, "replies", true);
		}
		return replies;
	}

	/*Setter and getter simple CONTAIMENT REFERENCES references to inner classes*/
	
	//DOCUMENTS!!!!!!!!!!!!!!!!!
		/*Setter and getter simple NOT CONTAIMENT REFERENCES references to toher documents*/
	
	public Comment setAuthor(Author author) {
		if (this.author != author) {
			if (author == null) {
				dbObject.put("author", new HashMap<String, Object>());
				if(this.author !=null) this.author .removeReferencedBy(getId());
			}
			else {
				createReference("author", author);
			}
			this.author = author;
			notifyChanged();
		}
		return this;
	}
	
	public Author getAuthor() {
		if (author == null) {
			author = (Author) resolveReference("author");
		}
		return author;
	}
	
	
	/*getter multiple REFERENCES*/
	public List<Member> getLiked() {
		if (liked == null) {
			liked = new DBList<Member>(this, "liked", false);
		}
		return liked;
	}
	public List<Member> getDisliked() {
		if (disliked == null) {
			disliked = new DBList<Member>(this, "disliked", false);
		}
		return disliked;
	}
}